package org.geekwisdom.data;
import java.util.*;
import java.util.Map.Entry;
public class GWDataRow  {
private LinkedHashMap<String,String> dataItem;

public GWDataRow (HashMap<String,String> i)
{
dataItem = (LinkedHashMap<String,String>) i;
}


public void set(String key,String value)
{
dataItem.put(key, value);
}

public String get(String key,String value)
{
String result = dataItem.get(key);
return result;
}


public Set<Entry<String,String>> entrySet()
{
	//Set<Entry<String, String>>
	HashMap<String,String> retval = new HashMap<String,String>();
	for (Map.Entry<String, String> entry : dataItem.entrySet()) {
	   String ColName = entry.getKey();
	   String []a = ColName.split("\\.");
       if (a.length > 0) ColName = a[a.length-1];
	   String ColValue = entry.getValue();
	   retval.put(ColName, ColValue);	
	}
	return retval.entrySet();
	}
public HashMap<String,String> toArray()
{
	//return copy of item without the "." column info
	LinkedHashMap<String,String> retval = new LinkedHashMap<String,String>();
	
	return dataItem;
}

public HashMap<String,String> toRawArray()
{
	return dataItem;
}



}


