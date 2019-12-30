
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geekwisdom.*;

public class UnitTester {

	public static void main(String[] args) {
	//	TestJavaDBGWQL();
	//	TestStudentService();
//				TestFileIO();
		//TestStudentService();
		//TestData2();
		//TestParsedCommand();
		TestGWQL();
		/*
		try {
		TestError();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
	//LogUnitTest();
	//TestSettings();
	//TestData2();
	//TestStudent();
	return;
	}

	public static void TestGWQL()
	{
		String COMPARESTRING_A="[ [ A _EQ_ \"2\" ] _AND_  [ B _EQ_ \"3\"  _OR_  C _EQ_ \"1\" ] ]";
		
		String COMPARESTRING_B="[ [ A _EQ_ \"2\"  _AND_ B _EQ_ \"3\" ] _OR_  [ C _EQ_ \"1\" ] ]";
		
		String COMPARESTRING_C="[ [ A _LIKE_ \"Brad\"  _AND_ B _EQ_ \"3\" ] _OR_  [ C _EQ_ \"1\" ] ]";
		//String COMPARESTRING_C="[ A LIKE \"Cathy\" OR [ [ A _LIKE_ \"Brad\"  _AND_ B _EQ_ \"3\" ] _OR_  [ C _EQ_ \"1\" ] ] ]";

		//$COMPARESTRING='[ Name _EQ_ "Mike Gold" ]';
		GWQL sqltester = new GWQL(COMPARESTRING_C	);
		GWQLSqlStringBuilder mysqlbuilder = new GWQLSqlStringBuilder();
		GWQLXPathBuilder myxpath = new GWQLXPathBuilder();
		String finalCmd="";
		try {
		 finalCmd= sqltester.getCommand(mysqlbuilder);
		}
		catch (Exception e) { e.printStackTrace();}
		//String finalCmd = sqltester.getCommand(myxpath);
		System.out.println("Final command is: " + finalCmd);
	}
	
	public static void TestParsedCommand()
	{
		try {
	
		//GWParsedCommand mycmd = new GWParsedCommand("[ ( BRAD _EQ_ 3 ] ) ");
			GWParsedCommand mycmd = new GWParsedCommand("[ ( Name _EQ_ \"Brad Detchevery\" ] ) ");
		System.out.println("Field:" + mycmd.getField());
		System.out.println("Op:" + mycmd.getOperator());
		System.out.println("Value:" + mycmd.getValue());
		
		}
		catch (GWException e)
		{
			e.printStackTrace();
		}
	}
	public static void LogUnitTest()
	{
		GWLogger myLogger = GWLogger.getInstance(5);
		myLogger.WriteLog(2,GWLogger.LogType.Error,"just a test3");	
		return;
	}

	public static void TestError() throws GWException
	{
		throw new GWException("This is just a test");
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
	FileTest.insert("{\"Name\":\"Brad\",\"Address\":\"Test\",\"ID\":\"3\"}");
	GWDataTable result;
	try {
	result = FileTest.search("[ Name _EQ_ \"Mike Gold\"");
	System.out.println(result.toJSON());
	}
	catch (GWException e)
	{
		e.printStackTrace();
	}

     
   // System.out.println(result.toXml());
	//FileTest.FileTest.System.out.println(result.toXml());
		
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
		GWDataTable ret = mytable.find("[ Name _EQ_ \"Mike Gold\" ]");
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
	//GWDataTable results = mytable.find("[ Name _EQ_ 'Cathy' _OR_ Name _EQ_ 'Brad' ] ");
	GWDataTable results = mytable.find("[ Name _LIKE_ 'Cat' ] ");
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
	GWDataTable all;
	try {
	all =myStudents.search("[ ID _GT_ 0 ]");
	student first=(student) all.getRow(0);
	System.out.println ("Name is " + first.getName());
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
	
}


static void TestJavaDB()
{
	//java database connection testing
	 String url = "jdbc:mysql://192.168.0.15:3306/braddb?useSSL=false";
     String user = "adminbrad";
     String password = "blc4fr";
     
     String query = "SELECT * FROM sec_UsersTable WHERE ";

     try {
    	 Connection con = DriverManager.getConnection(url, user, password);
    	 String where_clause="";
    	 while (!(where_clause.equals("QUIT")))
    			 {
    	 System.out.print("\nEnter Query: ");
    	    	
    		 
    	BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));
    	where_clause=SystemIn.readLine();
    	Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(query + where_clause);
        		 
    	 System.out.println("ID\tUser ID \tLocked\tFirst Name\tLast Name");
    	 System.out.println("==============================================================");
         while (rs.next()) {
             
             String theline =  rs.getString(1) + "\t" + rs.getString(2) + "   \t" + rs.getString(3) +  "\t" + rs.getString(4) + "\t\t" + rs.getString(5);
        	 System.out.println(theline);
         }
    			 }
     } catch (Exception ex) {
         
         ex.printStackTrace();
     } 
}

static void TestJavaDBGWQL()
{
	//java database connection testing
	 String url = "jdbc:mysql://192.168.0.15:3306/braddb?useSSL=false";
     String user = "adminbrad";
     String password = "blc4fr";
     
     String query = "SELECT * FROM sec_UsersTable WHERE ";
     HashMap<String,String> allowedFields = new HashMap<String,String>();
     allowedFields.put("ID","UserID");
     
     try {
    	 Connection con = DriverManager.getConnection(url, user, password);
    	 String where_clause="";
    	 while (!(where_clause.equals("QUIT")))
    			 {
    	 System.out.print("\nEnter Query: ");
   		 
    	BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));
    	where_clause=SystemIn.readLine();
    	
    	GWQL sqltester = new GWQL(where_clause);
    	sqltester.setAllowedFields(allowedFields);
		GWQLSqlStringBuilder mysqlbuilder = new GWQLSqlStringBuilder();
		String finalCmd="";

	try {
		finalCmd= sqltester.getCommand(mysqlbuilder);
	}
	catch (GWException gw1)
    {
   	    	 gw1.printStackTrace();
   	 return;
    }

	System.out.println(sqltester.getFlag("locked"));    
	PreparedStatement st;
	if (sqltester.getFlag("locked")) st= con.prepareStatement(query+" locked = true AND " + finalCmd);
	else st= con.prepareStatement(query+finalCmd);
         ArrayList<String> p = mysqlbuilder.getParams();
         for (int i=0;i<p.size();i++) 
        	  {
        	 //System.out.println("i:" + i + "d: " + p.get(i));
        	 st.setString(i+1, p.get(i));
        	  }
         ResultSet rs = st.executeQuery();		 
    	 System.out.println("ID\tUser ID \tLocked\tFirst Name\tLast Name");
    	 System.out.println("==============================================================");
         while (rs.next()) {
             
             String theline =  rs.getString(1) + "\t" + rs.getString(2) + "   \t" + rs.getString(3) +  "\t" + rs.getString(4) + "\t\t" + rs.getString(5);
        	 System.out.println(theline);
         }
    			 }
     } 
     
 
     
     catch (Exception ex) {
         
         ex.printStackTrace();
     } 
}



}
