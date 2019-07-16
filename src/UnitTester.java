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

import org.geekwisdom.GWDataRow;
import org.geekwisdom.GWDataTable;

public class UnitTester {

	public static void main(String[] args) {
		//TestStudentService();
		TestFileIO();
	//LogUnitTest();
	//TestSettings();
	//TestData2();
	//TestStudent();
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

public static void TestFileIO()
{
	//Method #1 
	//GWDataIO FileTest = new GWDataIO();
	//FileTest.insert("test","c:\\temp\\DataIOTest.config");
	//Medhot #2
	GWDataIO FileTest = new GWDataIO("c:\\temp\\DataIOTest.config");
	FileTest.insert("{\"Name\":\"Brad\",\"Address\":\"Test\",\"ID\":\"4\"}");
	GWDataTable result = FileTest.search("Name='Brad'");
	System.out.println(result.toXml());
	
}
public static void TestData()
{
	try {
	String xmldata = readFile("c:\\temp\\abc.xml",  StandardCharsets.UTF_8);
	GWDataTable mytable = new GWDataTable();
	mytable.loadXml(xmldata);
	for (int i=0;i<mytable.length();i++)
	{
		GWRowInterface col = mytable.getRow(i);
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
	public static void TestStudent()
	{
		try {
		String xmldata = readFile("c:\\temp\\student.xml",  StandardCharsets.UTF_8);
		GWDataTable mytable = new GWDataTable("","root","student");
		mytable.loadXml(xmldata);
		GWDataTable ret = mytable.find("Name='Mike Gold'");
		 System.out.println("Len is " + ret.length());
		for (int i=0;i<ret.length();i++)
		{
			student col = (student) ret.getRow(i);
				 System.out.println("Name is " + col.getName());
		}
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



static void TestStudentService()
{
	StudentService myStudents = new StudentService();
	myStudents.insert("{\"Name\":\"Brad\",\"Address\":\"Test\",\"ID\":\"4\"}");
	GWDataTable all =myStudents.search("ID > 0");
	student first=(student) all.getRow(0);
	System.out.println ("Name is " + first.getName());
	
}
}
