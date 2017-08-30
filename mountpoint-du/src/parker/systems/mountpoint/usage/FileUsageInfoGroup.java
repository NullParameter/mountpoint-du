package parker.systems.mountpoint.usage;

import java.util.List;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import parker.systems.mountpoint.usage.json.FileUsageInfoSerializer;

/**
 * Used mostly as a container for a list of {@link FileUsageInfo} objects, to support the proper 
 * JSON output.
 */
public class FileUsageInfoGroup {
	
	public final List<FileUsageInfo> files;
	
	public FileUsageInfoGroup(List<FileUsageInfo> files) {
		this.files = files;
	}
	
	public String toJson() {
		return export(FileUsageInfoGroup::infoGroupToJson);
	}
	
	public String export(Function<FileUsageInfoGroup, String> exportFunction) {
		return exportFunction.apply(this);
	}
	
	static String infoGroupToJson(FileUsageInfoGroup group) {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(FileUsageInfo.class, new FileUsageInfoSerializer())
				.create();
		
		return gson.toJson(group);
	}
}