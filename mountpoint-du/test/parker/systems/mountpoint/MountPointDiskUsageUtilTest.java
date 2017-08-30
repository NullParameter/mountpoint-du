package parker.systems.mountpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import parker.systems.mountpoint.usage.FileUsageInfo;

public class MountPointDiskUsageUtilTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private MountPointDiskUsageUtil cut;
	
	@Before
	public void before() {
		this.cut = new MountPointDiskUsageUtil();
	}

	@Test
	public void validateAndParseArguments_noArgs_throwsException() {
		final String[] NO_ARGS = {};
		assertThatThrownBy(() -> cut.validateAndParseArguments(NO_ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Please provide a single parameter for the mountpoint to be processed.");
	}
	
	@Test
	public void validateAndParseArguments_tooManyArgs_throwsException() {
		// Use a valid path in the first arg.  The method should still fail.
		final String validPath = tempFolder.getRoot().getAbsolutePath();
		
		final String[] MANY_ARGS = {validPath, "something else"};
		assertThatThrownBy(() -> cut.validateAndParseArguments(MANY_ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Please provide a single parameter for the mountpoint to be processed.");
	}
	
	@Test
	public void validateAndParseArguments_nonExistentFile_throwsException() {
		final File rootDir = tempFolder.getRoot();
		final File nonExistentFile = new File(rootDir, "foo");
		
		final String[] ARGS = {nonExistentFile.getAbsolutePath()};
		assertThatThrownBy(() -> cut.validateAndParseArguments(ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Specified path does not exist on the file system.");
	}
	
	@Test
	public void validateAndParseArguments_nonDirectory_throwsException() throws IOException {
		final File file = tempFolder.newFile("foo");
		
		final String[] ARGS = {file.getAbsolutePath()};
		assertThatThrownBy(() -> cut.validateAndParseArguments(ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Specified path is not a directory.");
	}
	
	@Test
	public void validateAndParseArguments_validDirectory_returnsPath() throws IOException {
		final File dir = tempFolder.newFolder("bar");
		
		final String[] ARGS = {dir.getAbsolutePath()};
		Path path = cut.validateAndParseArguments(ARGS);
		assertThat(path).isEqualTo(dir.toPath());
	}
	
	@Test
	public void processMountPoint_errorWalkingTree_ThrowsException() {
		final File rootDir = tempFolder.getRoot();
		
		// Easiest way to cause an IOException is to pass in a nonexistent file, 
		// because you can't walk something that doesn't exist.  Could also abstract out 
		// a method for processing a file, and make it throw an exception through mocking.
		final File nonExistentFile = new File(rootDir, "foo");
		
		assertThatThrownBy(() -> cut.processMountPoint(nonExistentFile.toPath()))
			.isInstanceOf(MountPointProcessingException.class)
			.hasCauseInstanceOf(IOException.class);
	}
	
	@Test
	public void processMountPoint_noFiles_returnsEmptyList() {
		final File rootDir = tempFolder.getRoot();
		
		List<FileUsageInfo> infos = cut.processMountPoint(rootDir.toPath());
		assertThat(infos).isEmpty();
	}
	
	@Test
	public void processMountPoint_dirWithOnlyDirectories_returnsEmptyList() throws IOException {
		final File rootDir = tempFolder.getRoot();
		tempFolder.newFolder("aaa");
		tempFolder.newFolder("bbb");
		tempFolder.newFolder("ccc");
		
		List<FileUsageInfo> infos = cut.processMountPoint(rootDir.toPath());
		assertThat(infos).isEmpty();
	}
	
	@Test
	public void processMountPoint_dirWithFiles_returnsList() throws IOException {
		final File rootDir = tempFolder.getRoot();
		
		final int FOO_SIZE = 12;
		File fooFile = tempFolder.newFile("foo");
		writeBytesToFile(fooFile, FOO_SIZE);
		
		final int BAR_SIZE = 24;
		File barFile = tempFolder.newFile("bar");
		writeBytesToFile(barFile, BAR_SIZE);
		
		final int BAZ_SIZE = 36;
		File bazFile = tempFolder.newFile("baz");
		writeBytesToFile(bazFile, BAZ_SIZE);
		
		final int QUX_SIZE = 48;
		File quxFile = tempFolder.newFile("qux");
		writeBytesToFile(quxFile, QUX_SIZE);
		
		List<FileUsageInfo> infos = cut.processMountPoint(rootDir.toPath());
		assertThat(infos).hasSize(4);
		assertThat(infos).contains(
				new FileUsageInfo(fooFile.getAbsolutePath(), FOO_SIZE),
				new FileUsageInfo(barFile.getAbsolutePath(), BAR_SIZE),
				new FileUsageInfo(bazFile.getAbsolutePath(), BAZ_SIZE),
				new FileUsageInfo(quxFile.getAbsolutePath(), QUX_SIZE)
			);
	}
	
	@Test
	public void processMountPoint_dirTree_returnsListWithAllFiles() throws IOException {
		final File rootDir = tempFolder.getRoot();
		
		final int ONE_SIZE = 4;
		File oneFile = tempFolder.newFile("one");
		writeBytesToFile(oneFile, ONE_SIZE);
		
		final int TWO_SIZE = 8;
		File subFolder = Files.createDirectories(rootDir.toPath().resolve("sub")).toFile();
		File twoFile = Files.createFile(subFolder.toPath().resolve("two")).toFile();
		writeBytesToFile(twoFile, TWO_SIZE);
		
		final int RED_SIZE = 16;
		File subsubFolder = Files.createDirectories(subFolder.toPath().resolve("sub")).toFile();
		File redFile = Files.createFile(subsubFolder.toPath().resolve("two")).toFile();
		writeBytesToFile(redFile, RED_SIZE);
		
		final int BLUE_SIZE = 32;
		File subsubsubFolder = Files.createDirectories(subsubFolder.toPath().resolve("sub")).toFile();
		File blueFile = Files.createFile(subsubsubFolder.toPath().resolve("two")).toFile();
		writeBytesToFile(blueFile, BLUE_SIZE);
		
		List<FileUsageInfo> infos = cut.processMountPoint(rootDir.toPath());
		assertThat(infos).hasSize(4);
		assertThat(infos).contains(
				new FileUsageInfo(oneFile.getAbsolutePath(), ONE_SIZE),
				new FileUsageInfo(twoFile.getAbsolutePath(), TWO_SIZE),
				new FileUsageInfo(redFile.getAbsolutePath(), RED_SIZE),
				new FileUsageInfo(blueFile.getAbsolutePath(), BLUE_SIZE)
			);
	}
	
	private static void writeBytesToFile(File file, int numBytes) throws IOException {
		byte[] bytesToWrite = new byte[numBytes];
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(bytesToWrite);
		}
	}
	
}
