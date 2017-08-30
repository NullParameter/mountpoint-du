package parker.systems.mountpoint.usage.json;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import parker.systems.mountpoint.usage.FileUsageInfo;

public class FileUsageInfoSerializer implements JsonSerializer<FileUsageInfo>{

	@Override
	public JsonElement serialize(FileUsageInfo info, Type typeOfSrc, JsonSerializationContext context) {
		JsonPrimitive usage = new JsonPrimitive(info.getUsage());
		JsonObject json = new JsonObject();
		json.add(info.getPath(), usage);
		return json;
	}	
}
