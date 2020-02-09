/* *************************************************************************************
' Script Name: GWDataIOInterface.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is a shared component available to all JAVA applications. It allows a common 
' @(#)    object that can CREATE (INSERT), RETRIEVE (SEARCH) UPDATE, AND DELETE from a variety of IO
' @(#)    sources. Specifically using the GWDataTable / GWDataRow format
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2019-07-23 - Initial Architecture
' 
' **************************************************************************************
'Note: GWDataIOInterface is the interface for GWDataIO. The actual heavy lifting is done by FileIO or 
'DataBaseIO which extend this class for the specific IO ability
'getInstance(FlieName) to return the approperiate type from the file. 
'This class defines those protected methods common to all extended children
' **************************************************************************************/
package org.geekwisdom;

public interface GWDataIOInterface {

	public GWDataIOInterface getInstance(String configfile);
	public GWDataIOInterface getInstance();
	public String insert(String JSONROW,String configfile);
	public String insert(String JSONROW);
	public String update(String JSONROW,String configfile);
	public String update(String JSONROW);
	public GWDataTable search(String whereclause,String configfile) throws GWException;
	public GWDataTable search(String whereclause) throws GWException;
	public String delete(String id,String configfile);
	public String delete(String id);
	public String lock(String id,String configfile);
	public String lock(String id);
	public String unlock(String id,String configfile);
	public String unlock(String id);
	public void open(String configfile);
	public void open();
	public void save(String configfile);
	public void save();
}
