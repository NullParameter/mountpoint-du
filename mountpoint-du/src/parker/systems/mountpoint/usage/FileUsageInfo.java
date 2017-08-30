package parker.systems.mountpoint.usage;

import java.nio.file.Path;

public class FileUsageInfo {
	
	private final String path;
	private final long usage;
	
	public static FileUsageInfo fromPath(Path p) {
		return new FileUsageInfo(p.toString(), p.toFile().length());
	}

	private FileUsageInfo(String filePath, long usage) {
		this.path = filePath;
		this.usage = usage;
	}
	
	public String getPath() {
		return path;
	}
	
	public long getUsage() {
		return usage;
	}
}