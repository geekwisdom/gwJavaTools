import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geekwisdom.*;

import org.geekwisdom.data.GWDataRow;
import org.geekwisdom.data.GWDataTable;

public class UnitTester {

	public static void main(String[] args) {
		
	//LogUnitTest();
	//TestSettings();
	TestData2();
	return;
	}

	public static void LogUnitTest()
	{
		GWLogger myLogger = GWLogger.getInstance(5);
		myLogger.WriteLog(2,GWLogger.LogType.Error,"just a test3");	
		return;
	}

public static void TestSettings()
{
	GWSettings mySettings = new GWSettings();
	String r = mySettings.GetSetting("c:\\temp\\settingstest.config","test","default","");
	String r2 = mySettings.GetSettingReverse("c:\\temp\\settingstest.config","Cool Dude");
	System.out.println(r);
	System.out.println(r2);
}

public static void TestData()
{
	try {
	String xmldata = readFile("c:\\temp\\abc.xml",  StandardCharsets.UTF_8);
	GWDataTable mytable = new GWDataTable();
	mytable.loadXml(xmldata);
	for (int i=0;i<mytable.length();i++)
	{
		GWDataRow col = mytable.getRow(i);
		Iterator it = col.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
					}
		 
	
		
	}
	System.out.println(mytable.toXml());
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
}

public static void TestData2()
{
	try {
		//GWDataTable mytable = new GWDataTable("","students");
		GWDataTable mytable = new GWDataTable();
				
		LinkedHashMap<String,String> myrow = new LinkedHashMap<String,String>();
	myrow.put("Name", "Brad");
	myrow.put("Age", "43");
	mytable.add(myrow);
	LinkedHashMap<String,String> myrow2 = new LinkedHashMap<String,String>();
	myrow2.put("Name", "Cathy");
	myrow2.put("Age", "49");
    	
	mytable.add(myrow2);
	GWDataTable results = mytable.find("Name='Cathy'");
	System.out.println(results.toXml());
	//System.out.println(mytable.toXml());
	}
catch (Exception e)
	{
	e.printStackTrace();
	}
}


static String readFile(String path, Charset encoding) 
		  throws IOException 
		{
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, encoding);
		}

}
