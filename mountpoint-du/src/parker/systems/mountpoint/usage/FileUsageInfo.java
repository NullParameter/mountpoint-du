package parker.systems.mountpoint.usage;

import java.nio.file.Path;

import org.assertj.core.util.VisibleForTesting;

public class FileUsageInfo {
	
	private final String path;
	private final long usage;
	
	public static FileUsageInfo fromPath(Path p) {
		return new FileUsageInfo(p.toString(), p.toFile().length());
	}

	@VisibleForTesting
	public FileUsageInfo(String filePath, long usage) {
		this.path = filePath;
		this.usage = usage;
	}
	
	public String getPath() {
		return path;
	}
	
	public long getUsage() {
		return usage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (int) (usage ^ (usage >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileUsageInfo other = (FileUsageInfo) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (usage != other.usage)
			return false;
		return true;
	}
	
	
}