package org.geekwisdom;

/* *************************************************************************************
' Script Name: GWSettings.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is a shared component available to all JAVA applications. It allows simple
' @(#)    settings system that can be used to store and retrieve an application's settings
' @(#)    You can store settings databases,Property, an INI file, etc.
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2019-05-20 - Initial Architecture
' 
' **************************************************************************************
'Note: Changing this routine effects all programs that change system settings
'-------------------------------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class GWSettings {
	
    private String ApplicationName;
    public GWSettings(String AppName)
    {
        ApplicationName = AppName;
        //Console.WriteLine(ApplicationName);
    }
    public GWSettings()
    {
        //NOte there are several methods for getting the application name. If the settings manager is constructued 
        //with s specific name then this is waht we will use, otherwise will use the name of the ese
        
        ApplicationName = "App";
        //Console.WriteLine(ApplicationName);
    }

    public String GetSetting(String SettingName, String DefaultValue)
    {
        
        try
        {
        	Properties props = System.getProperties();
        	String configfile= props.getProperty(ApplicationName+ ".config");
        	if (configfile !=null && configfile !="" ) return GetSetting(configfile,SettingName,DefaultValue,"");
        	String result = props.getProperty(SettingName);
        	if (result == null || result == "") return DefaultValue;
        	return result;
            
        }
        catch (Exception e)
        {
            return DefaultValue;
        }
    }

    public String GetSetting(String SettingName)
    {
    	return GetSetting(SettingName,"");
    }

    public String GetSetting(String FromLocation, String SettingName, String DefaultValue, String VersionNumber)
    {
    	File f = new File(FromLocation);
    	
    	if (f.isFile()&& f.canRead())
    	{
    	     if (FromLocation.indexOf(".mdb") >= 0 || FromLocation.indexOf(".accdb") >= 0)
    	     {
    	    	 return GetSetting("jdbc:ucanaccess"+FromLocation, SettingName, DefaultValue, VersionNumber); 
    	     }
    	     else if (FromLocation.indexOf(".properties") > 0)
    	     {
    	    	 Properties prop = new Properties();
    	    	InputStream input = null;

    	    		try {

    	    			input = new FileInputStream(FromLocation);

    	    			// load a properties file
    	    			prop.load(input);

    	    			// get the property value and print it out
    	    			String result = prop.getProperty(SettingName);
    	    			if (result == null || result == "") return DefaultValue;
    	    			return result;
    	     }
    	    		catch (Exception ex) { return DefaultValue;}
    	    		
    	}
    	     else if (FromLocation.indexOf(".ini") > 0 )
    	     {
    	    	 try {
    	    	 IniParser myini = new IniParser(FromLocation);
    	    	 return myini.getString(ApplicationName, SettingName, DefaultValue);
    	    	 }
    	    	 catch (Exception e1)
    	    	 {
    	    	 return DefaultValue;
    	    	 }
    	     }
    	     else if (FromLocation.indexOf(".config") > 0 || FromLocation.indexOf(".xml") > 0 )
    	     {
    	    
    	    	 //attempt to read a C# type config flie
    	    	 String test1= ReadSettingFromNetFile(FromLocation,SettingName);
    	    	 if (test1 !="") return test1;
    	    	 //attempt to read as an xml file
    	    	 try {
    	    	 Properties prop = new Properties();
    	    	 prop.loadFromXML(new FileInputStream(FromLocation));
    	    	 String retval = prop.getProperty(SettingName);
    	    	 if (retval == null || retval =="") return DefaultValue;
    	    	 return retval;
    	    	 }
    	    	 catch (Exception e1)
    	    	 {
    	    		 //not a valid prperites.xml file. Let's try reading it as a plain
    	    		 //text file
    	    		 return ReadSettingFromTextFile(FromLocation,SettingName,DefaultValue);
    	    		 
    	    	 }
    	    	 
    	    	 
    	    	 
    	     }
    }
       
    	 else if (FromLocation.indexOf("jdbc:") ==0)
    	    {
    	   	 //return from database

    	   	 try (Connection connection = DriverManager.getConnection(FromLocation)) 
    	   	 {
    	   		 //System.out.println("Database connected!");
    	   		 CallableStatement callableStatement = null;
    	   		 String TheCommand = "{call GetSetting (?)}";
    	   		 if (FromLocation.indexOf(".mdb") >= 0 || FromLocation.indexOf(".accdb") >= 0)
    	   		 {
    	   			 TheCommand="SELECT SettingValue FROM SYSTEM_CONFIGURATION WHERE SettingName=?";
    	   		 }
    				callableStatement = connection.prepareCall(TheCommand);
    				callableStatement.setString(1,SettingName);
    				callableStatement.executeQuery();
    				ResultSet rs = callableStatement.getResultSet();
    				String SettingValue = null; 
    				while (rs.next()) 
    				 {
    					SettingValue = rs.getString("SettingValue");  
    				 }
    					if (SettingValue == null) return DefaultValue;
    					return SettingValue;
    	   	 } 
    	   	 catch (SQLException e) {
    	throw new IllegalStateException("Cannot connect the database!", e);
    	}
    	    }
    	
    	
    return "";
    }
	
    public String GetSettingReverse (String FromLocation, String settingName)
    {
    	//get the 'reverse' setting in a .NET styel config 
    	try { 
    	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document document = db.parse(new FileInputStream(new File(FromLocation)));// same xml comments as above.

          XPathFactory xpf = XPathFactory.newInstance();
          XPath xpath = xpf.newXPath();
          String key="//add[@value='" + settingName + "']";
          
          Element userElement = (Element) xpath.evaluate(key, document,
              XPathConstants.NODE);
          String value=userElement.getAttribute("key");
          //System.out.println(value);
          return value;
    	 }
    	 catch (Exception e2)
    	 {
    		 //e2.printStackTrace();
    		 return "";
    	 }
    	 
    }

    
    
    private String ReadSettingFromNetFile (String FromLocation, String settingName)
    {
    	 try { 
    	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document document = db.parse(new FileInputStream(new File(FromLocation)));// same xml comments as above.

          XPathFactory xpf = XPathFactory.newInstance();
          XPath xpath = xpf.newXPath();
          String key="//add[@key='" + settingName + "']";
          
          Element userElement = (Element) xpath.evaluate(key, document,
              XPathConstants.NODE);
          String value=userElement.getAttribute("value");
          //System.out.println(value);
          return value;
    	 }
    	 catch (Exception e2)
    	 {
    		 //e2.printStackTrace();
    		 return "";
    	 }
    	 
    }
    private String ReadSettingFromTextFile(String FromLocation,String settingName,
			String defaultValue) {
        String line;
        String result="";
        boolean done = false;
        
        Charset charset = Charset.forName("US-ASCII");
        Path theFile = Paths.get(FromLocation);
        try (BufferedReader reader = Files.newBufferedReader(theFile, charset)) 
        {
            line = reader.readLine();
            if (line == null) done = true;
            while (!done) {

                String firstchar = line.substring(0, 1);
                if (firstchar != "#" && firstchar != ";")
                		
                {
                 if (line.indexOf("=") >0)
                 {
                     String myString = line.replace(System.lineSeparator(), "");
                     String[] pairs = myString.split("=");
                     if (pairs[0].trim().toLowerCase().equals(settingName.trim().toLowerCase())) { result = pairs[1]; done = true; }
                 }
                }
                line = reader.readLine();
                if (line == null) done = true;
            }
            reader.close();
            }
         catch (Exception e) {
             //reader.close();
        	 return defaultValue;
        }
        // Read the file and display it line by line.
        
        
        //reader.Close();
        if (result != "") return result;
		return defaultValue;
	}
}
 