package parker.systems.mountpoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.VisibleForTesting;

import parker.systems.mountpoint.usage.FileUsageInfo;
import parker.systems.mountpoint.usage.FileUsageInfoGroup;

public class MountPointDiskUsageUtil {

	public static void main(String[] args) throws IOException {
		try {
			new MountPointDiskUsageUtil().execute(args);
		} catch (IllegalArgumentException |
				MountPointProcessingException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	@VisibleForTesting
	String execute(String[] args) {
		
		final Path path = validateAndParseArguments(args);
		List<FileUsageInfo> fileUsages = processMountPoint(path);
		
		return new FileUsageInfoGroup(fileUsages).toJson();
	}
	
	@VisibleForTesting
	Path validateAndParseArguments(String[] args) throws IllegalArgumentException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Please provide a single parameter for the mountpoint to be processed.");
		}
		
		Path path = Paths.get(args[0]);
		if(!Files.exists(path)) {
			throw new IllegalArgumentException("Specified path does not exist on the file system.");
		}
		
		if(!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Specified path is not a directory.");
		}
		
		return path;
	}
	/**
	 * Walk the entire directory tree, processing only the files, returning a {@link FileUsageInfo} for each.
	 * 
	 * @throws MountPointProcessingException if there is any IO errors while processing the tree.
	 */
	@VisibleForTesting
	List<FileUsageInfo> processMountPoint(Path path) {
		try {
			return Files.walk(path)
					.filter(Files::isRegularFile)
					.map(FileUsageInfo::fromPath)
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new MountPointProcessingException("Error while attempting to process mount point files.", e);
		}
	}

}
