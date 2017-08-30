package parker.systems.mountpoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MountPointDiskUsageUtil {

	public static void main(String[] args) throws IOException {
		
		if (args.length != 1) {
			System.err.println("Please provide a single parameter for the mountpoint to be processed.");
			System.exit(1);
		}
		
		Path path = Paths.get(args[0]);
		
		if(!Files.exists(path)) {
			System.err.println("Specified path does not exist on the file system.");
			System.exit(1);
		}
		
		if(!Files.isDirectory(path)) {
			System.err.println("Specified path is not a directory.");
			System.exit(1);
		}
		 
		List<FileUsageInfo> fileUsages = Files.walk(path)
				.filter(Files::isRegularFile)
				.map(FileUsageInfo::fromPath)
				.collect(Collectors.toList());
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(new FileUsageInfoGroup(fileUsages)));
	}
	
	public static class FileUsageInfoGroup {
		
		public final List<FileUsageInfo> files;
		
		public FileUsageInfoGroup(List<FileUsageInfo> files) {
			this.files = files;
		}
	}
	
	public static class FileUsageInfo {
		
		private final String path;
		private final long usage;
		
		private FileUsageInfo(String filePath, long usage) {
			this.path = filePath;
			this.usage = usage;
		}
		
		public static FileUsageInfo fromPath(Path p) {
			return new FileUsageInfo(p.toString(), p.toFile().length());
		}
	}

}
