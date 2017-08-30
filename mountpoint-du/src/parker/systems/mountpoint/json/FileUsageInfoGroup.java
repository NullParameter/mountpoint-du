package parker.systems.mountpoint.json;

import java.util.List;

public class FileUsageInfoGroup {
	
	public final List<FileUsageInfo> files;
	
	public FileUsageInfoGroup(List<FileUsageInfo> files) {
		this.files = files;
	}
}