
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
import java.util.stream.Stream;
import java.nio.file.*;
import org.geekwisdom.*;

public class UnitTester {

	public static void main(String[] args) {
//		TestDec();
		//TestEnc();
		///TestData();
	//	testhex();
		//TestJavaDBGWQL2();
	//	TestStudentService();
//				TestFileIO();
		//TestStudentService();
		//TestData3();
		//TestParsedCommand();
	//	TestWebServiceServer();
		//TestWebServiceServer2();
	//	TestGWQL();
		/*
		try {
		TestError();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
	LogUnitTest();
	//TestSettings();
	//TestData2();
	//TestStudent();
	return;
	}

	public static void TestEnc()
	{
		GWSecSharedKeyCrypt myEnc = new GWSecSharedKeyCrypt("0123456789abcdef0123456789abcdef",null,null);
		String enc = myEnc.encrypt("plain text","CryptTest.SimpleXOR");
		if (enc == null) System.out.println("Encryption error!");
		else System.out.println("Encrypted is: \n" + enc);
	}
	
	public static void TestDec()
	{
	 //String enc="<?xml version=\"1.0\"?>\n<xmlDS>\n<table1>\n<TIMESTAMP>2020-06-23T16:35Z</TIMESTAMP>\n<METHOD>AES-256-CBC</METHOD>\n <IV>519ab61329d7dd4b4bab30948739c037</IV>\n <HMAC>78163af8c4cbb7ea246a28432563f692cde03a8b65877cbb729bb45c487d55f8</HMAC>\n <MESSAGE>w/7Bm/e9D+sg9NGViHxUtpb92EdFeOV6JH2N5VkE8ZU=</MESSAGE>\n</table1>\n</xmlDS>";
	 /* String enc="<?xml version=\"1.0\"?>\n" + 
	 		"<xmlDS>\n" + 
	 		"  <root>\n" + 
	 		"    <TIMESTAMP>2020-06-22T17:50:20+00:00</TIMESTAMP>\n" + 
	 		"    <METHOD>AES-256-CBC</METHOD>\n" + 
	 		"    <IV>8yAo5J1Q4SlE6UrMPL+sbQ==</IV>\n" + 
	 		"    <HMAC>294d9f294f02f40d2260646a2bc99098295ef79d532da0b07adc6696488c75d3</HMAC>\n" + 
	 		"    <MESSAGE>Cgg3VKxswTzFnYth1O5JIQ==</MESSAGE>\n" + 
	 		"  </root>\n" + 
	 		"</xmlDS>\n";
	 		*/ 
	 String enc =  readLineByLineJava8("c:\\temp\\v.txt");
	 
	 		
		GWSecSharedKeyCrypt myEnc = new GWSecSharedKeyCrypt("0123456789abcdef0123456789abcdef",null,null);
		String plaintext = myEnc.decrypt(enc);
		if (plaintext == null) System.out.println("decryption error!");
		else System.out.println("Decrypted is: \n" + plaintext);
	
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
	String xmldata = readFile("c:\\temp\\testme.xml",  StandardCharsets.UTF_8);
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
	GWDataTable results = mytable.find("[ Name _LIKE_ \"Cat\" ] ");
	
	
	//System.out.println(results.toXml());
	System.out.println(results.toJSON());
	//System.out.println(mytable.toXml());
	}
catch (Exception e)
	{
	e.printStackTrace();
	}
}


public static void TestData3()
{
	try {
		//GWDataTable mytable = new GWDataTable("","students");
		GWDataTable mytable = new GWDataTable();
				
		//LinkedHashMap<String,String> myrow = new LinkedHashMap<String,String>();
		GWDataRow myrow = new GWDataRow();
	myrow.set("Name", "Brad");
	myrow.set("Age", "43");
	mytable.add(myrow);
	GWDataRow myrow2 = new GWDataRow();
	myrow2.set("Name", "Cathy");
	myrow2.set("Age", "49");
    	
	mytable.add(myrow2);
	//GWDataTable results = mytable.find("[ Name _EQ_ 'Cathy' _OR_ Name _EQ_ 'Brad' ] ");
	GWDataTable results = mytable.find("[ Name _LIKE_ \"Cat\" ] ");
	
	
	//System.out.println(results.toXml());
	System.out.println(results.toJSON());
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

static void TestWebServiceServer()
{

	//$myWebService = new GWEZWebService("def","./","adminbrad123");
	GWEZWebService myWebService = new GWEZWebService("abc","c:/temp/","adminbrad123");
	ArrayList<String> Params = new ArrayList<String>();
	Params.add("LogVerbosity");
	String result=myWebService.Fulfill("GetSetting",Params,"JSON");
	//$rstr=$result->toXML();
	System.out.println( "Result is " + result);
	
}

static void TestWebServiceServer2()
{
	//REMINDERS: the def.xml operation MUST be in the JAVA CLASS PATH (Eg: MathClass.jar!)
	GWEZWebService myWebService = new GWEZWebService("def","c:/temp/","adminbrad123");
	ArrayList<String> Params = new ArrayList<String>();
	Params.add("A=1");
	Params.add("B=3");
	String result=myWebService.Fulfill("Add",Params,"JSON");
	//$rstr=$result->toXML();
	System.out.println( "Result is " + result);
	
}



static void TestJavaDB()
{
	//java database connection testing
	 String url = "jdbc:mysql://localhost:3306/test?useSSL=false";
     String user = "adminbrad";
     String password = "password";
     
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
	 String url = "jdbc:mysql://localhost:3306/test?useSSL=false";
     String user = "adminbrad";
     String password = "password";
     
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
             
            // String theline =  rs.getString(1) + "\t" + rs.getString(2) + "   \t" + rs.getString(3) +  "\t" + rs.getString(4) + "\t\t" + rs.getString(5);
             String theline =  rs.getString(1) + "\t" + rs.getString(2) + "   \t" + rs.getString(3);
        	 System.out.println(theline);
         }
    			 }
     } 
     
 
     
     catch (Exception ex) {
         
         ex.printStackTrace();
     } 
}


static void TestJavaDBGWQL2()
{
	//java database connection testing
	 GWDBConnection myconnection = new GWDBConnection("c:/temp/testconn.dsn");
     
     String query = "SELECT * FROM sec_UsersTable WHERE ";
     HashMap<String,String> allowedFields = new HashMap<String,String>();
     allowedFields.put("ID","UserID");
     
     try {
    	 
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
	if (sqltester.getFlag("locked")) st= myconnection.prepare(query+" locked = true AND " + finalCmd);
	else st= myconnection.prepare(query+finalCmd);
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
             
            // String theline =  rs.getString(1) + "\t" + rs.getString(2) + "   \t" + rs.getString(3) +  "\t" + rs.getString(4) + "\t\t" + rs.getString(5);
             String theline =  rs.getString(1) + "\t" + rs.getString(2) + "   \t" + rs.getString(3);
        	 System.out.println(theline);
         }
    			 }
     } 
     
 
     
     catch (Exception ex) {
         
         ex.printStackTrace();
     } 
}

static void testhex()
{
	//48656c6c6f
	
	//byte[] test = { 0x48,0x65,0x6C,0x6C,0x6F};
	byte[] test = { 'H','e','l','l','o'};
	
	String p1=bin2hex("Hello");
	String p2=hex2str(p1);
	System.out.println(p2);
	
}

private static String bin2hex(byte[] bytes) 
    
	 {
	 final char[] hexArray = "0123456789abcdef".toCharArray(); 
	char[] hexChars = new char[bytes.length * 2];
	        for ( int j = 0; j < bytes.length; j++ ) {
	            int v = bytes[j] & 0xFF;
	            hexChars[j * 2] = hexArray[v >>> 4];
	            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	        }
	        return new String(hexChars);
	    }

private static String bin2hex(String hexString) 

{
byte [] barray = hexString.getBytes();
return bin2hex(barray);
   }
	
		
	private static byte [] hex2bin(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	private static String hex2str(String s) {
	    
	    return new String (hex2bin(s));
	}

	   private static String readLineByLineJava8(String filePath) 
	    {
	        StringBuilder contentBuilder = new StringBuilder();
	 
	        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
	        {
	            stream.forEach(s -> contentBuilder.append(s).append("\n"));
	        }
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	 
	        return contentBuilder.toString();
	    }
	
}
