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
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class GWEZWebService {
	protected String ServiceFile;
	protected String ServiceName;
	protected String UserName;
	protected GWDataTable fault403;
	protected boolean UseOracle;

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
	 GWDataRow newrow = new GWDataRow();
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
	if (format.equalsIgnoreCase("XML")) return errormsg.toXml();
	if (format.equalsIgnoreCase("JSON")) 
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

	
	
	public  String Fulfill(String Operation,ArrayList<String> Params,String format)
{
   if (Operation.contentEquals("")) return this.showError("404 Not Found", "Missing Operation", format);
   //GWDataTable retval = new GWDataTable("","Results");
   String FinalOutput = "";
  GWDataTable GWServiceResults = new GWDataTable("","Results");
  GWDataTable WebServiceConfig = new GWDataTable();
  File f = new File(this.ServiceFile);
  if (!(f.exists()))
  {
	return this.showError("404 Not Found", "No such service available: " + this.ServiceName, format);
  }
  
  String GWServiceFile = file_get_contents(this.ServiceFile);
  WebServiceConfig.loadXml(GWServiceFile);
  String OPType="";
  String OpSource="";
  String UserName="";
  try {
	GWDataTable FoundRows = WebServiceConfig.find("[ OperationName _EQ_ \"" + Operation +"\" ]");
    
    if (FoundRows.length() == 0)
    {
    	return this.showError("404 Not Found", "No such operation available" + Operation, format);
    }
  
    if (FoundRows.length() >0)
    {
    	GWDataRow FoundRow = (GWDataRow) FoundRows.getRow(0);
    	OPType=FoundRow.get("OperationType");
    	OpSource=FoundRow.get("OperationSource");
    	boolean AuthEnabled = FoundRow.has_column("AllowedUsers");
    	if (AuthEnabled)
    	{
    		String UserAuthNames=FoundRow.get("AllowedUsers");
    		
            if (this.UserName == "")
    		{
    		if (format.equalsIgnoreCase("XML")) return this.fault403.toXml();
    		if (format.equalsIgnoreCase("JSON")) return this.fault403.toJSON();
    		return this.fault403.toString();
    		}
    		int has_semi = UserAuthNames.indexOf(";");
    		String[] AllowedUsers;
    		if (has_semi > 0)
    		{
    			AllowedUsers=UserAuthNames.split(";");
    		}
    		else
    		{
    			AllowedUsers = new String[1];
    			AllowedUsers[0] = UserAuthNames;
    		}
    		boolean AccessGranted=false;
    		for (int i=0;i<AllowedUsers.length;i++)
    		{
    			if (UserName.equalsIgnoreCase(AllowedUsers[i])) AccessGranted=true;
    		}
    		if (AccessGranted == false) 
    		{
    		if (format.equalsIgnoreCase("XML")) return this.fault403.toXml();
    		if (format.equalsIgnoreCase("JSON")) return this.fault403.toJSON();
    		return this.fault403.toString();
    		}
    	}             		
    		int isStoredProcedure = OpSource.indexOf(".class");
            if (isStoredProcedure < 0)  isStoredProcedure = OpSource.indexOf(".jar");
            if (isStoredProcedure > 0)
            {
            ArrayList<String> PArray = new ArrayList<String>();
            String[] MethodData;
            //$checkOpenBracket = strpos($MethodData,"(");
            //$checkClosedBracket = strpos($MethodData,")");
            
            try 
            {
            @SuppressWarnings("rawtypes")
            String[] MthdInfo = parseMethod(OPType);
            
            if (Params.size() != MthdInfo.length-2)
            {
    			return this.showError("500 Server Error","Incorrect Parameters for Operation " + Operation,format);
            }
            @SuppressWarnings("rawtypes")
			Class cls;
            int pnum=1;
            if (MthdInfo != null) cls = Class.forName(MthdInfo[0]);
            else cls = Class.forName(OPType);
            Object obj = cls.newInstance();
            @SuppressWarnings("rawtypes")
        	ArrayList<Class> plist = new ArrayList<Class>();
            ArrayList<String> pvalues = new ArrayList<String>();
            //for (Map.Entry<String, String[]> entry : ParmsArray.entrySet())   
            int stopat=Params.size();
            if (MthdInfo !=null && stopat < MthdInfo.length-2) stopat=MthdInfo.length-2;
            //Iterator<Map.Entry<String,String[]>> entries = ((Map) Params).entrySet().iterator();
            HashMap <String,String> ParamsArray = orderParams(Params);
         	for (Map.Entry<String, String> entry : ParamsArray.entrySet())   
             {
         		 String key = entry.getKey();
         	    String  value = entry.getValue();
          	  plist.add(Class.forName("java.lang.String"));
             pvalues.add(value.replace("\"", ""));
             }
            Class [] parameters = plist.toArray(new Class[plist.size()]);
            String [] pva = pvalues.toArray(new String[pvalues.size()]);
            Method method;
            
            if (MthdInfo != null)
            {
           	 //System.out.println ("Class Name: " + MthdInfo[0]);
           	 //System.out.println ("Method Name: " + MthdInfo[1]);
           	 method= cls.getDeclaredMethod(MthdInfo[1],parameters);
            }
            else method= cls.getDeclaredMethod(Operation,parameters);
            String ret = method.invoke(obj,pva).toString();
            
            if (ret.indexOf("<") == 0) GWServiceResults.loadXml(ret);
            else
            {
            	GWDataRow newrow = new GWDataRow();
            	newrow.set("Return", ret);
            	GWServiceResults.add(newrow);
            }
     
            
            
            
            //   out.println ("Returned was: " + ret);
           }
          catch (Exception e) {e.printStackTrace(); }//e.printStackTrace(new java.io.PrintWriter(out)); }
    	}

    	else
    	{
    		//Stored Procedure Stuff
    		try
            {
        	   Connection conn =null;
        	   int TestJNDI = OpSource.indexOf("java:");
         	if (TestJNDI == 0)
         	{
         		Context initCtx = new InitialContext();
         	DataSource ds = (DataSource)initCtx.lookup(OpSource);
         	conn = ds.getConnection();
         	}
         	
         		else
         		{
         			GWDBConnection dbconn = new GWDBConnection(OpSource);
         			String driver=dbconn.getConnectInfo("driver");
         			if (driver.indexOf("oracle") >=0) UseOracle=true;
         			conn = dbconn.getConnection();
         		}
         	
         	DatabaseMetaData dbmd = conn.getMetaData();
         	if (UseOracle)
         	{
         		OPType = OPType.replace("{","{? = ");
         	}
         	CallableStatement calStat =  conn.prepareCall(OPType);
         	
         	
            	//  ArrayList<String> pvalues = new ArrayList<String>();
         	
         	int i=1;
         	if (UseOracle)
         		{
         		calStat.registerOutParameter (1, OracleTypes.CURSOR);
         		i++;
         		}
         		
         	
         	//Iterator<Map.Entry<String,String[]>> entries = ((Map) Params).entrySet().iterator();
         	HashMap <String,String> ParamsArray = orderParams(Params);
         	for (Map.Entry<String, String> entry : ParamsArray.entrySet())   
             {
         		 String key = entry.getKey();
         	    String  value = entry.getValue();
         //		 String [] nv = parts[i].split("=");
   		//if (dbmd.supportsNamedParameters() == true)
   			//private void setbytype(CallableStatement cs,String TypeName,String Paramname,String Paramvalue)
   			//calStat = setbytype(calStat,"varchar",key,value);
   			
    		//else 
    			calStat = setbytype(calStat,"varchar",i,value);
   		i++;			

                }

         	
         	
         	boolean result = calStat.execute();
         	  //ResultSet rset = (ResultSet)call.getObject (1);
         	ResultSet rs ;
         	try {
         	// rs= calStat.getResultSet();
         		rs = (ResultSet) calStat.getObject (1);
         	}
         	catch  (SQLException e) {
                 // Ignore ORA-17283: No resultset available (1)
                 if (e.getErrorCode() == 17283)
                     calStat.getMoreResults();
                 	rs = calStat.getResultSet();

         	}
         	 ResultSetMetaData meta = rs.getMetaData();
         	 int colCount = meta.getColumnCount();
         	 //GWDataTable retval = new GWDataTable("","Results");
         	 /*$retval = new GWDataTable("","Results");
    		$retval->readList($rows);
    		if ($format == "XML") return $retval->toXML();
    		if ($format == "JSON") return $retval->toJSON();
    		    		return $retval;*/
         	   while (rs.next()) 
                          {
         		   GWDataRow item = new GWDataRow();
         		   //Map<String,String> item = new HashMap<String,String>();
                 	 for (int col=1; col <= colCount; col++) 
             		    {
                 		Object value = rs.getObject(col);
                 		if (value != null) 
                 		{
                     		String colname=meta.getColumnName(col);
         			String vstr = value.toString();
         			item.set(colname, vstr);
                 		}
         			//else out.println("Value is null!");
         				
         		   }
         		GWServiceResults.add(item);		
                         }
                  }
           catch(Exception ex) 
         		{
            		 // handle any errors
         		// out.println("SQLException: " + ex.getMessage());
         	    	//out.println("SQLState: " + ex.getSQLState());
         	    	//System.out.println("VendorError: " + ex.getErrorCode());
                ex.printStackTrace(); 	
         		}

    	
    	}
    	   if (format.equalsIgnoreCase("XML")) return GWServiceResults.toXml();
           if (format.equalsIgnoreCase("JSON")) return GWServiceResults.toJSON();
           return "format: " + GWServiceResults.toString();
    }
 
 
} 
  catch (GWException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
  
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

private String[] parseMethod(String MethodType)
{
//given a method type like Enterprise.SBB.MultiCRUD.MultiCrud(?,"abc",?)
//return the method name and an array of default parameters 
ArrayList<String> retval = new ArrayList<String>();
	 int foundbraket = MethodType.indexOf("(");
int founddot = MethodType.indexOf(".");

if (foundbraket < 0) return null;
if (founddot < 0) return null;
String firstpart = MethodType.substring(0,foundbraket);
int lastdot = firstpart.lastIndexOf(".");
String ClassName=firstpart.substring(0,lastdot);
String [] MethodParts = firstpart.split("\\.");
String BracketInfo = MethodType.substring(foundbraket);
BracketInfo = BracketInfo.replace("(","");
BracketInfo = BracketInfo.replace(")","");
String [] ParamInfo = BracketInfo.split(",");
String mname = MethodParts[MethodParts.length-1];
int b2 = mname.indexOf("(");
retval.add(ClassName);
retval.add(mname);
for (int j=0;j<ParamInfo.length;j++) retval.add(ParamInfo[j]);
return retval.toArray(new String[retval.size()]);
}

private HashMap<String,String> orderParams(ArrayList<String> Params)
{
HashMap<String,String> retval = new HashMap<String,String>();
for (int i=0;i<Params.size();i++)
{
String p = Params.get(i);
if (p.indexOf("=") > 0)
{
	String [] parts = p.split("=");
	retval.put(parts[0], parts[1]);
}
else 
	 retval.put(Integer.toString(i), p);
}
return retval;
}
private CallableStatement setbytype(CallableStatement cs,String TypeName,String Paramname,String Paramvalue)
{
 String typename = TypeName.toLowerCase();
 try
 {
 if (typename.equals("int"))
 {
	  cs.setInt(Paramname,Integer.parseInt(Paramvalue) );
	  
 }
 else if (typename.equals("nvarchar"))
 {
	  cs.setString(Paramname,Paramvalue);
	  
 }
 else if (typename.equals("varchar"))
 {
	  cs.setString(Paramname,Paramvalue );
	  
 }
 else if (typename.equals("Date"))
 {
	  cs.setTimestamp(Paramname,Timestamp.valueOf(Paramvalue));
	  
 }
 else if (typename.equals("Double"))
 {
	  cs.setDouble(Paramname,Double.valueOf(Paramvalue));
 }
 
 
 else if (typename.equals("Text"))
 {
	  cs.setString(Paramname,Paramvalue);
 }
 }
 catch (Exception e)
 {
	e.printStackTrace();  
 }

 return cs;
 }

 private CallableStatement setbytype(CallableStatement cs,String TypeName,int Paramname,String Paramvalue)
 {
  String typename = TypeName.toLowerCase();
  try
  {
  if (typename.equals("int"))
  {
	  cs.setInt(Paramname,Integer.parseInt(Paramvalue) );
	  
  }
  else if (typename.equals("nvarchar"))
  {
	  cs.setString(Paramname,Paramvalue);
	  
  }
  else if (typename.equals("varchar"))
  {
	  cs.setString(Paramname,Paramvalue );
	  
  }
  else if (typename.equals("string"))
  {
	  cs.setString(Paramname,Paramvalue );
	  
  }
  else if (typename.equals("date"))
  {
	  cs.setTimestamp(Paramname,Timestamp.valueOf(Paramvalue));
	  
  }
  else if (typename.equals("text"))
  {
	  cs.setString(Paramname,Paramvalue);
  }
  
  else if (typename.equals("double"))
  {
	  cs.setDouble(Paramname,Double.valueOf(Paramvalue));
  }
  
  
  
  }
  catch (Exception e)
  {
	e.printStackTrace();  
  }
 
  return cs;
  }


}

