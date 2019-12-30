/* *************************************************************************************
' Script Name: GWEZWebService
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is a shared component available to all PHP applications. It allows an easy 
' @(#)    ability to serve a php class OR stored procedure as a web service
' @(#)    Simple Setup works with XML and JSON formats
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2019-11-03 - Initial Architecture
' 
' **************************************************************************************/

package org.geekwisdom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GWEZWebService {
	protected String ServiceFile;
	protected String ServiceName;
	protected String UserName;
	protected GWDataTable fault403;

//Constructor
	
	public GWEZWebService (String servicename, String filepath, String username)
	{
		ServiceName = servicename;
		UserName = username;
		ServiceFile = filepath + servicename + ".xml";
		
		initialize403();		
	}
	
		public GWEZWebService (String servicename, String filepath)
	{
		ServiceName = servicename;
		ServiceFile = filepath + servicename + ".xml";
	
	}
	
		
	public GWEZWebService (String servicename)
	{
		ServiceName = servicename;
		ServiceFile = "/tmp/" + servicename + ".xml";
		initialize403();
	}

	private void initialize403()
	{
	 fault403 = new GWDataTable("","fault");
	 GWDataRow newrow = new GWDataRow(null);
     newrow.set ("code","Server");
	 newrow.set ("faultstring","403 Access Denied");
	 fault403.add(newrow);
	}

	private String showError(String faultstring,String detail, String format)
	{
	GWDataTable errormsg = new GWDataTable("","fault");
	GWDataRow newrow = new GWDataRow();
	newrow.set ("code","Server");
	newrow.set ("faultstring",faultstring);
	if (detail !="") newrow.set ("detail",detail);
	errormsg.add(newrow);
	if (format == "XML") return errormsg.toXml();
	if (format == "JSON") 
	{
		try 
		 {
			return errormsg.toJSON();
		 }
		catch (Exception e)
		{
			return "UNKOWN JSON DETAIL DECODE ERROR: ";
		}
		 }
	
	return errormsg.toXml();
}

	
	
	public  String Fulfill(String Operation,String Params,String format)
{
   if (Operation.contentEquals("")) return this.showError("404 Not Found", "Missing Operation", format);
  String FinalOutput = "";
  GWDataTable GWServiceResults = new GWDataTable();
  GWDataTable WebServiceConfig = new GWDataTable();
  String GWServiceFile = file_get_contents(this.ServiceFile);
  WebServiceConfig.loadXml(GWServiceFile);
 
  //  WebServiceConfig.find_row("[ OperationName _EQ_ \"" . $Operation . "\" ]");
  return FinalOutput;
}
private String file_get_contents(String file)
{
	String contents;
	try {
		contents = new String(Files.readAllBytes(Paths.get(file)));
		return contents;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return "";
			
}
}

