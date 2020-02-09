/**************************************************************************************
' Script Name: GWDataRow.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is a shared component available to all JAVA applications. It allows a common 
' @(#)    data row / data table object that for manipulating sets of related data abstractly.
' @(#)    Regardless of the specific architecture (database, files, xml, json used)
' **************************************************************************************
'  Written By: Brad Detchevery
' Created:     2019-05-29 - Initial Architecture
' 
' **************************************************************************************
'Note: Changing this routine effects all programs that manipulate data sets
'-------------------------------------------------------------------------------*/

package org.geekwisdom;
import java.util.*;
import java.util.Map.Entry;
public class GWDataRow implements GWRowInterface {
private LinkedHashMap<String,String> dataItem;

public GWDataRow (HashMap<String,String> i)
{
dataItem = (LinkedHashMap<String,String>) i;
}


public GWDataRow ()
{
dataItem = new LinkedHashMap<String,String>();
}



public void set(String key,String value)
{
dataItem.put(key, value);
}

public String get(String key)
{
String result = dataItem.get(key);
if (result == null) 
{
	for (Map.Entry<String, String> entry : dataItem.entrySet()) {
		   String ColName = entry.getKey();
		   String []a = ColName.split("\\.");
	       if (a.length > 0) ColName = a[a.length-1];
		   String ColValue = entry.getValue();
		   if (ColName.equals(key))  return ColValue;
	}
}
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
public LinkedHashMap<String,String> toArray()
{
	//return copy of item without the "." column info
	LinkedHashMap<String,String> retval = new LinkedHashMap<String,String>();
	return dataItem;
	//return dataItem;
}

public HashMap<String,String> toRawArray()
{
	return dataItem;
}

public boolean has_column(String columnname)
{
	return dataItem.containsKey(columnname);
}

}


