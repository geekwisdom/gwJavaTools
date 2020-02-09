package org.geekwisdom;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class GWDBConnection  
{
private String finalconnectstring = "";
private String un="";
private String pw="";
	public GWDBConnection (String connectstr)
{
	File f = new File (connectstr);
    Object [] ar;
	if (f.exists()) 
    {
    ArrayList<String> cArray = file(connectstr);
    if (cArray.size() == 1) ar = cArray.get(0).split(";");
    else ar = cArray.toArray();
    }
	else
	{
	ar = connectstr.split(";");
	}
	HashMap<String,String> connectinfo = new HashMap<String,String>();
	String newstr="";
	un="";
	pw="";
	for (int i=0;i<ar.length;i++)
	{
	String itv=(String) ar[i];
    itv = itv.trim();			
	if (itv.indexOf("=") > 0)
	{
    String [] parts = itv.split("=");
    if (parts[0].indexOf(":") > 0)
    {
    	String [] m = parts[0].split(":");
    	connectinfo.put("driver", m[0]);
    	connectinfo.put("host", parts[1]);
    }
    else
    {
     connectinfo.put(parts[0].toLowerCase(), parts[1]);	
    }
	}
	}
    String port="";
    if (connectinfo.containsKey("port")) port = connectinfo.get("port");
   	String driver="mysql";
   	String host="localhost";
   	if (connectinfo.containsKey("driver")) driver=connectinfo.get("driver");
   	if (connectinfo.containsKey("host")) host=connectinfo.get("host");
   	finalconnectstring = "jdbc:" + driver+ "://" + host;
   	if (port != "" ) finalconnectstring = finalconnectstring + ":" + port;
   	finalconnectstring = finalconnectstring + "/" + connectinfo.get("dbname");
    
	un=connectinfo.get("uid");
	pw=connectinfo.get("pw");
	


}

public PreparedStatement prepare(String stmt)
{
	Connection con;
	System.out.println(finalconnectstring);
	try {
		con = DriverManager.getConnection(finalconnectstring,un,pw);
		return con.prepareStatement(stmt);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
	
}
//DatabaseMetaData dbmd = conn.getMetaData();
//	CallableStatement calStat =  conn.prepareCall(OPType);

public DatabaseMetaData getMetaData()
{
Connection con;
try {
	con = DriverManager.getConnection(finalconnectstring,un,pw);
	return con.getMetaData();
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
return null;
}

public CallableStatement prepareCall(String stmt)
{
	Connection con;
	System.out.println(finalconnectstring);
	try {
		con = DriverManager.getConnection(finalconnectstring,un,pw);
		return con.prepareCall(stmt);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
	
}

public Connection getConnection()
{
	Connection con;
	//System.out.println(finalconnectstring);
	try {
		con = DriverManager.getConnection(finalconnectstring,un,pw);
		return con;
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
	
}


private ArrayList<String> file (String filename)
{
	ArrayList<String> result = new ArrayList<>();
	 
	try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	    while (br.ready()) {
	        result.add(br.readLine());
	    }
	}
     catch (Exception e) { return null; }
	 return result;
	}
}
