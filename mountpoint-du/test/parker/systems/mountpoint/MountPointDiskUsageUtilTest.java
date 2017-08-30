package parker.systems.mountpoint;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MountPointDiskUsageUtilTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private MountPointDiskUsageUtil cut;
	
	@Before
	public void before() {
		this.cut = new MountPointDiskUsageUtil();
	}

	@Test
	public void validateAndParseArguments_noArgs_throwsException() throws Exception {
		final String[] NO_ARGS = {};
		assertThatThrownBy(() -> cut.validateAndParseArguments(NO_ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Please provide a single parameter for the mountpoint to be processed.");
	}
	
	@Test
	public void validateAndParseArguments_tooManyArgs_throwsException() throws Exception {
		// Use a valid path in the first arg.  The method should still fail.
		final String validPath = tempFolder.getRoot().getAbsolutePath();
		
		final String[] MANY_ARGS = {validPath, "something else"};
		assertThatThrownBy(() -> cut.validateAndParseArguments(MANY_ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Please provide a single parameter for the mountpoint to be processed.");
	}
	
	@Test
	public void validateAndParseArguments_nonExistentFile_throwsException() throws Exception {
		final File rootDir = tempFolder.getRoot();
		final File nonExistentFile = new File(rootDir, "foo");
		
		final String[] ARGS = {nonExistentFile.getAbsolutePath()};
		assertThatThrownBy(() -> cut.validateAndParseArguments(ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Specified path does not exist on the file system.");
	}
	
	@Test
	public void validateAndParseArguments_nonDirectory_throwsException() throws Exception {
		final File rootDir = tempFolder.getRoot();
		final File file = new File(rootDir, "foo");
		file.createNewFile();
		
		final String[] ARGS = {file.getAbsolutePath()};
		assertThatThrownBy(() -> cut.validateAndParseArguments(ARGS))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Specified path is not a directory.");
	}
	
	@Test
	public void validateAndParseArguments_validDirectory_returnsPath() throws Exception {
		final File rootDir = tempFolder.getRoot();
		final File dir = new File(rootDir, "foo");
		dir.mkdir();
		
		final String[] ARGS = {dir.getAbsolutePath()};
		Path path = cut.validateAndParseArguments(ARGS);
		assertThat(path).isEqualTo(dir.toPath());
	}
}
