package net.jonstout.java.simpletempengine.factory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import net.jonstout.java.simpletempengine.data.DataContext;

/**
 * Static factory method to use data from a JSON text file to create a DataContext object.
 * @author Jon Stout
 * @see net.jonstout.java.simpletempengine.data.DataContext
 */
public class DataFactory {

	public static DataContext loadData(String filePath) throws JsonParseException, JsonMappingException, IOException {
		File jsonFile = new File(filePath);
		if (jsonFile.exists()) {
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> json = mapper.readValue(jsonFile, Map.class);
			DataContext context = new DataContext(json);
			return context;
		}
		return null;
	}
	
}
