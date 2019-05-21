package org.geekwisdom.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
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

public class GWDataTable 
{
private ArrayList<GWDataRow> data = new ArrayList<GWDataRow>();
String xml="";
String tablename="table1";
private	HashMap<String,String> parsedMap = new  LinkedHashMap<String,String>();

public GWDataTable()
{
	
}

public GWDataTable(String xmlinfo,String TableName)
{
xml=xmlinfo;
if (TableName == "") tablename="root";
else tablename=TableName;
}

public GWDataTable find (String whereclause)
{
	String xmldata = toXml();
	//System.out.println(xmldata);
	GWDataTable retTable = new GWDataTable("",tablename);
	try { 
   	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document document = db.parse(new InputSource(new StringReader(xmldata)));// same xml comments as above.

         XPathFactory xpf = XPathFactory.newInstance();
         XPath xpath = xpf.newXPath();
         String qry="//xmlDS/" + tablename + "[" + whereclause + "]";
        //System.out.println(qry); 
         
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
			GWDataRow newcol = new GWDataRow(theitem);
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
    tablename=root.getNodeName();
    GWDataRow newrow = new GWDataRow(readnode(root,"",""));
	 data.add(newrow);
     
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

public GWDataRow getRow(int RowNum)
{
return data.get(RowNum);	
}

public void add(GWDataRow newrow)
{
	data.add(newrow);
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
  GWDataRow col = data.get(i);
  HashMap<String,String> item = col.toArray();
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



