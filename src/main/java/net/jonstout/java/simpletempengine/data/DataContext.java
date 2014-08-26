package net.jonstout.java.simpletempengine.data;

import java.util.Map;

/**
 * Data object which contains both the data loaded from the
 * given JSON text file, as well as variables created by the
 * template as it is processed.
 * @author Jon Stout
 */
public class DataContext {
	private Map<String, Object> context;

	public DataContext(Map<String, Object> context) {
		super();
		this.context = context;
	}
	
	public void add(String key) {
		this.context.put(key, null);
	}
	
	public void add(String key, Object value) {
		this.context.putIfAbsent(key, value);
	}
	
	public Object get(String token) {
		return this.getValue(token, this.context);
	}
	
	public void put(String key, Object value) {
		this.context.put(key, value);
	}
	
	public void remove(String key) {
		if (this.context.containsKey(key)) {
			this.context.remove(key);
		}
	}
	
	public void clear() {
		this.context.clear();
	}
	
	private Object getValue(String key, Map<String,Object> map) {
		int nextDotIndex = key.indexOf(".");
		String property = null;
		String path = null;
		if (nextDotIndex > -1) {
			property = key.substring(0, nextDotIndex);
			path = key.substring(nextDotIndex+1); // cut out the dot
		} else {
			property = key;
		}
		if (map.containsKey(property)) {
			Object value = map.get(property);
			if (value.getClass() == java.util.LinkedHashMap.class && path != null) {
				@SuppressWarnings("unchecked")
				Map<String, Object> valueMap = (Map<String, Object>) value;
				return this.getValue(path, valueMap);
			} else {
				return value;
			}
		}
		return null;
	}
}
