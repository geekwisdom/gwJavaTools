/**************************************************************************************
' Script Name: GWDataTable.java
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
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
public class GWDataTable 
{
private ArrayList<GWRowInterface> data = new ArrayList<GWRowInterface>();
String xml="";
String tablename="table1";
String defObject="org.geekwisdom.GWDataRow";
private	LinkedHashMap<String,String> parsedMap = new  LinkedHashMap<String,String>();

public GWDataTable()
{
	
}

public GWDataTable(String xmlinfo,String TableName)
{
xml=xmlinfo;
if (TableName == "") tablename="root";
else tablename=TableName;
}

public GWDataTable(String xmlinfo,String TableName,String _defObject)
{
xml=xmlinfo;
if (TableName == "") tablename="root";
else tablename=TableName;
defObject=_defObject;
}


public GWDataTable find (String whereclause) throws GWException
{
 
			try {
				return find(whereclause,defObject);
			}
	catch (GWException e) { throw e; }
}
public GWDataTable find (String qlwhereclause,String rowType) throws GWException
{	
	GWQL xPathTester = new GWQL(qlwhereclause);
	GWQLXPathBuilder myxpath = new GWQLXPathBuilder();
	String whereclause="";
	try
	{
	whereclause = xPathTester.getCommand(myxpath);
	}
	catch (GWException e) { throw e; }
	String xmldata = toXml();
	//System.out.println(xmldata);
	GWDataTable retTable = new GWDataTable("",tablename);
	try { 
   	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document document = db.parse(new InputSource(new StringReader(xmldata)));// same xml comments as above.

         XPathFactory xpf = XPathFactory.newInstance();
         XPath xpath = xpf.newXPath();
         //tablename="Student";
         String qry="//xmlDS/" + tablename + "[" + whereclause + "]";
 //       System.out.println(qry); 
         
         NodeList nodes = (NodeList) xpath.evaluate(qry,document, XPathConstants.NODESET);
         //System.out.println("Length is: " + nodes.getLength());
         for (int i = 0; i < nodes.getLength(); i++) {
             //values.add(nodes.item(i).getNodeValue());
             Node node = nodes.item(i);
             LinkedHashMap<String,String> theitem = new LinkedHashMap<String,String>();
		for(Node childNode = node.getFirstChild(); childNode!=null;)
                  {
   			Node nextChild = childNode.getNextSibling();
			String NodeName=childNode.getNodeName();
			if (!NodeName.equals("#text"))
			{        	
			String NodeValue=childNode.getFirstChild().getNodeValue();
			theitem.put(tablename + "." + NodeName,NodeValue.replace("%26","&"));
			}
		        childNode = nextChild;
		}
		if (theitem != null)
			{
//			System.out.println("Updating dataset");
			Class clazz = Class.forName(rowType);
			Constructor c = Class.forName(rowType).getDeclaredConstructor(HashMap.class);
			GWRowInterface newcol = (GWRowInterface) c.newInstance(theitem);
       	    retTable.add(newcol);		
			}
         }
         
}
	catch (Exception e) {e.printStackTrace(); }
	return retTable;
}

public void loadXml(String xmlstring)
{
//read xml file add to array
ReadXml(xmlstring);		
}

private void ReadXml(String XMLInput) {
    String XMLString = XMLInput.replace("&","%26");
//    XMLString =XMLString.replaceAll( "(<\\?[^<]*\\?>)?", "");
    XMLString =XMLString.replaceAll(" xmlns.*?(\"|\').*?(\"|\')", "");
    XMLString =XMLString.replaceAll("(<)(\\w+:)(.*?>)", "$1$3");
    XMLString =XMLString.replaceAll("(</)(\\w+:)(.*?>)", "$1$3");
    XMLString =XMLString.replaceAll("(?i)<Envelope>", "");
    XMLString =XMLString.replaceAll("(?i)</Envelope>", "");
    XMLString =XMLString.replaceAll("(?i)<Body>", "<xmlDS>");
    XMLString =XMLString.replaceAll("(?i)</Body>", "</xmlDS>");
    XMLString =XMLString.replaceAll("(?i)<Header>", "");
    XMLString =XMLString.replaceAll("(?i)</Header>", "");
    XMLString =XMLString.replaceAll("(?i)<Header/>", "");
    XMLString =XMLString.replaceAll("(?i)<Envelope/>", "");
    XMLString =XMLString.replaceAll("(?i)<Body/>", "");
  //  System.out.println(XMLString);
    Document doc = null;

    try {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        dbf.setIgnoringComments(true);
    InputSource is = new InputSource(new StringReader(XMLString));
        doc = db.parse(is);
    }
    catch (Exception e) {             System.out.println("Please specify an XML source [LOG IT!]");
}
    Element root = doc.getDocumentElement();
    Node firstChlid = root.getFirstChild().getNextSibling();
    tablename=firstChlid.getNodeName();
    try {
    Class clazz = Class.forName(defObject);
	Constructor c = Class.forName(defObject).getDeclaredConstructor(HashMap.class);
	GWRowInterface newrow = (GWRowInterface) c.newInstance(readnode(root,"",""));
	data.add(newrow);
    }
    catch (Exception e)
    {
    	GWDataRow newrow = new GWDataRow(readnode(root,"",""));
    	data.add(newrow);
    }
    //
	 
     
   // data.add(item);
}



private HashMap<String, String> readnode(Node node, String buildedKey,String lastkey) 
{
    String nodeName =  node.getNodeName();
 //   int thislength=0;
    boolean newone=false;
    if (!nodeName.equals("#text")) {
    //buildedKey = nodeName;
        String []a = buildedKey.split("\\.");
    if (a.length == 2)
		{
		//System.out.println("Added hashmap to array!" + parsedMap.get("company.staff.firstname"));
             if (parsedMap.size() > 0) 
              {
            	 GWDataRow newrow = new GWDataRow(parsedMap);
            	 data.add(newrow);
             parsedMap = new  LinkedHashMap<String,String>();
             newone=false;
             }
         //    parsedMap.put(buildedKey,node.getNodeValue());

	}
  //  System.out.println ("B  is " + buildedKey + " " + a.length);
    lastkey = buildedKey;
        buildedKey +=  "." + nodeName;
    }
    //else nodeName = "";
    if (node.getNodeValue() != null && !node.getNodeValue().matches("^\\W*$")) {
	buildedKey = buildedKey.substring(1);
            String val = parsedMap.get(buildedKey);
//     System.out.println(newone);
	
//        System.out.println ("This key: " + buildedKey + " Last: " + lastkey);
//	System.out.println("Adding to parsedmap");
    
            parsedMap.put(buildedKey,node.getNodeValue().replace("%26","&"));
            
    }

    if (node.getNodeType() == Node.ELEMENT_NODE) {
        if (node.hasAttributes()) {
            NamedNodeMap startAttr = node.getAttributes();
            for (int i = 0; i < startAttr.getLength(); i++) {
                Node attr = startAttr.item(i);
               // buildedKey += attr.getNodeValue() + ".";
            }
        }
    }
    int c=0;
    for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
        c++;
        readnode(child, buildedKey,lastkey);
    }
    return parsedMap;
}

public GWRowInterface getRow(int RowNum)
{
return data.get(RowNum);	
}

public void add(GWRowInterface newrow)
{
	//data.add(newrow);
	LinkedHashMap<String,String> newitem = new LinkedHashMap<String,String>();
	newitem=newrow.toArray();
	add(newitem);
	
}

public void add(HashMap<String,String> newrow)
{
	
	LinkedHashMap<String,String> newitem = new LinkedHashMap<String,String>();
			
		for (Map.Entry<String, String> entry : newrow.entrySet()) {
		   String ColName = entry.getKey();
		   if (ColName.indexOf(".") < 0) ColName = tablename + "." + ColName;
		   String ColValue = entry.getValue();
		   newitem.put(ColName, ColValue);	
			}
	GWDataRow col = new GWDataRow(newitem);
	if (validatearray(col)) data.add(col);
}


public String toJSON()
{
//Writre out current object as JSON String
JSONArray j = new JSONArray();
//SrrayList<LinkedHashMap<String,String>> mylist = new <LinkedHashMap<String,String>>();
ArrayList<LinkedHashMap<String, String>> mylist = new ArrayList<LinkedHashMap<String, String>>();
String TableName = this.tablename;
for (int i=0;i<data.size();i++) 
{ 
	//JSONObject jo = new JSONObject();
	JSONArray ja = new JSONArray();
	
	 LinkedHashMap<String,String> newitem = new LinkedHashMap<String,String>();
	 GWRowInterface col = data.get(i);
	 LinkedHashMap<String,String> item = col.toArray();
	for (Map.Entry<String, String> entry : item.entrySet()) 
	  {
	    String key = entry.getKey();
	    //System.out.println("Key is: " + key);
	    String value = entry.getValue();
	    String []a = key.split("\\.");
	    String TableNameTest = a[0];
	    if (TableName.contentEquals(TableNameTest))
	    {
	    	String TrueKey = a[1];
	    	if (value !=null) 
		    {
		    		value = value.toString().replace("&","%26");
		    		try
		    		{
		    		newitem.put(TrueKey,value);
		    		//	jo.put(TrueKey, value);
		    		}
		    		catch (Exception e) { }
		    }
	    	else
	    	{
	    		try { newitem.put(TrueKey,null);} catch (Exception e) { }
	    	}
	    }
	    }
mylist.add(newitem);
	//j.put(newitem);
}
JSONObject resJSON = new JSONObject();
try
{
resJSON.put(TableName, mylist);
}
catch (Exception e) { }
		
String result;
result = resJSON.toString();
return result;
}



public String toXml()
{
	StringWriter retval = new StringWriter();
	WriteXml(retval);
	return retval.toString();
}
private void WriteXml(StringWriter sw)
{
WriteXml(sw,"");
}
public void WriteXml(StringWriter finalsw,String nodename)
{
int colstart=0;
if (nodename == "") nodename="xmlDS";
else colstart=1;
StringWriter sw = new StringWriter();
for (int i=0;i<data.size();i++)
  {
  GWRowInterface col = data.get(i);
  LinkedHashMap<String,String> item = col.toArray();
  String header="";
  String footer="";
  String Retval="";
  for (Map.Entry<String, String> entry : item.entrySet()) 
  {
    String key = entry.getKey();
    String value = entry.getValue();
    if (value !=null) 
    {
    		value = value.toString().replace("&","%26");
    
    String []a = key.split("\\.");
    if (header == "")
       {
       for(int j=colstart;j<a.length-1;j++)
          {
          String tabs = repeat(" ",j);
          if (!(a[j].equals(nodename))) header=header+tabs+"<"+a[j] + ">\n";
          }
       }

    if (footer == "")
       {
       for(int j=a.length-2;j>=colstart;j--)
          {
         // int count=-1 * j + a.length;
	  int count=j;
	  String tabs = repeat(" ",count);
	  if (!(a[j].equals(nodename))) footer=footer+tabs+"</"+a[j] + ">\n";
          }
       }
      Retval = Retval + repeat(" ",a.length) + "<" + a[a.length-1] + ">" + value + "</" + a[a.length-1] + ">\n";
    }
      //    System.out.println(key + " " + value);  
}
sw.append(header);
sw.append(Retval);
sw.append(footer);
  }
finalsw.append("<?xml version=\"1.0\"?>\n");
finalsw.append("<" + nodename + ">\n");
finalsw.append(sw.toString());
finalsw.append("</" + nodename + ">\n");
}

public int length()
{
	return data.size();
}

private boolean validatearray(GWDataRow row)
{
	return true;
}

private String repeat(String pattern, int count) 
{
String retval="";
for (int i=0;i<count;i++)
{
retval+=pattern;
}
return retval;
}

public void remove(int rownum) {
//remove row num from table	
	data.remove(rownum);
}


}



