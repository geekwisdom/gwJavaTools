/* *************************************************************************************
' Script Name: GWDataIO.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is a shared component available to all PHP applications. It allows a common 
' @(#)    object that can CREATE (INSERT), RETRIEVE (SEARCH) UPDATE, AND DELETE from a variety of IO
' @(#)    sources. Specifically using the GWDataTable / GWDataRow format
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2019-07-23 - Initial Architecture
' 
' **************************************************************************************
'Note: GWDataIO is the base class. The actual heavy lifting is done by FileIO or 
'DataBaseIO which extend this class for the specific IO ability
'getInstance(FlieName) to return the appropriate type from the file. 
'This class defines those protected methods common to all extended children
'Uses: json.jar https://stleary.github.io/JSON-java/
'https://www.json.org/license.html
'The Software shall be used for Good, not Evil.
'https://en.wikipedia.org/wiki/Douglas_Crockford#%22Good,_not_Evil%22
' **************************************************************************************/
package org.geekwisdom;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.*;

public class GWDataIO implements GWDataIOInterface {

	protected GWDataTable dataTable;
	protected String _configFile;
	protected String defaultObj="org.geekwisdom.GWDataRow";
	
	public GWDataIO(String configfile,String defObj)
	{
	_configFile = configfile;
	defaultObj = defObj;
	}
	
	public GWDataIO(String configfile)
	{
	_configFile = configfile;
	}
	
	public GWDataIO()
	{
		//Hmm. what happens here?
	}
	
	@Override
		public GWDataIOInterface getInstance(String configfile) {
		// TODO Auto-generated method stub
		GWSettings settingsManger = new GWSettings();
		//System.out.println(configfile);
		//String r = mySettings.GetSetting("c:\\temp\\settingstest.config","test","default","");
		String objType = settingsManger.GetSetting(configfile,"IOTYPE","test","");
		//System.out.println(objType);
		try {
			
			Class clazz = Class.forName(objType);
			Constructor c = Class.forName(objType).getDeclaredConstructor(String.class,String.class);
			return (GWDataIOInterface) c.newInstance(configfile,defaultObj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
			}

	@Override
	public GWDataIOInterface getInstance() {
		// TODO Auto-generated method stub
		return getInstance(defaultObj);
	}

	@Override
	public String insert(String JSONROW, String configfile) {
		// TODO Auto-generated method stub
		return getInstance(configfile).insert(JSONROW);
	}

	@Override
	public String insert(String JSONROW) {
		// TODO Auto-generated method stub
		return insert(JSONROW,_configFile);
	}

	@Override
	public String update(String JSONROW, String configfile) {
		// TODO Auto-generated method stub
		return getInstance(configfile).update(JSONROW);
	}

	@Override
	public String update(String JSONROW) {
		// TODO Auto-generated method stub
		return update(JSONROW,_configFile);
	}

	@Override
	public GWDataTable search(String whereclause, String configfile) throws GWException
	{
		// TODO Auto-generated method stub
		return getInstance(configfile).search(whereclause);
	}

	@Override
	public GWDataTable search(String whereclause)  throws GWException{
		// TODO Auto-generated method stub
		return search(whereclause,_configFile);
	}

	@Override
	public String delete(String id, String configfile) {
		// TODO Auto-generated method stub
		return getInstance(configfile).delete(id);
	}

	@Override
	public String delete(String id) {
		// TODO Auto-generated method stub
		return delete(id,_configFile);
	}

	@Override
	public String lock(String id, String configfile) {
		// TODO Auto-generated method stub
		return getInstance(configfile).lock(id);
	}

	@Override
	public String lock(String id) {
		return lock(id,_configFile);
		
	}

	@Override
	public String unlock(String id, String configfile) {
		// TODO Auto-generated method stub
		return getInstance(configfile).unlock(id);
	}

	@Override
	public String unlock(String id) {
		// TODO Auto-generated method stub
		return unlock(id,_configFile);
	}

	@Override
	public void open(String configfile) {
		// TODO Auto-generated method stub
		getInstance(configfile).open();
		return;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		open(_configFile);
		return;
	}

	@Override
	public void save(String configfile) {
		// TODO Auto-generated method stub
		getInstance(configfile).save();
		return;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		save(_configFile);
		return;
	}
	
	protected LinkedHashMap<String, String> translate (String InputJSON)
	{
    try {
		JSONObject obj = new JSONObject(InputJSON);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

	    Iterator<String> keysItr = obj.keys();
	    while(keysItr.hasNext()) 
	      {
	        String key = keysItr.next();
	        Object value = obj.get(key);
/*
	        if(value instanceof JSONArray) {
	            //value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        */
	        map.put(key, value.toString());
	      }
	    //GWDataRow newrow = new GWDataRow(map);
	    return map;
    }
    catch (Exception E)
    {
    	E.printStackTrace();
    	return null;
    }
    
	}

}
