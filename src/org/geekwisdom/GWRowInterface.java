package org.geekwisdom;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

public interface GWRowInterface {
	public void set(String key,String value);
	public String get(String key);
	public Set<Entry<String,String>> entrySet();
	public LinkedHashMap<String,String> toArray();
	
	
	
}
