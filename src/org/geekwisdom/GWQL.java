/* *************************************************************************************
' Script Name: GWQL.JAVA
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is the GEEK WISDOM QUERY LANGUAGE. It is designed to be a language
' @(#)    independant queyr language for use with SQL, JSON, and the GeekWisdom
' @(#)    table object.
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2019-07-23 - Initial Architecture
 
' **************************************************************************************/

package org.geekwisdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GWQL {

private String Clause="";
private ArrayList<HashMap<String,String>> Params;
private HashMap<String,String> allowedFields = new HashMap<String,String>();
private HashMap<String,Boolean> cFlags = new HashMap<String,Boolean>();


public GWQL(String clause)
{
	this.Clause = setFlags(clause);
	Params = new ArrayList<HashMap<String,String>>();
}

public void setClause(String newclause)
{
	
	
	this.Clause = setFlags(newclause);
}

private String setFlags(String clause_input)
{
	//remove all flags from the last ] (if exist)
	int last = clause_input.lastIndexOf("]");
	if (last == -1) return clause_input;
    String retval = clause_input.substring(0,last+1);
    //System.out.println("Last is " + last);
    //System.out.println("Len is " + clause_input.length());
    if (last+1 < clause_input.length())
    {
    String flagsStr = clause_input.substring(last+2);
    //System.out.println(flagsStr);
    String [] flags = flagsStr.split(" ");
    for (int i=0;i<flags.length;i++)
    {
    	boolean v=true;
    	String flagname = flags[i];
    	if (flagname.substring(0,1) == "!") 
    	{
    		v=false;
    		flagname = flagname.substring(1,flagname.length()-1);
    	
    	}
    	cFlags.put(flagname.trim(),v);
    }
    }
   //System.out.println(retval);
    return retval;		
}  

public boolean getFlag(String flagname)
{
	if (!(cFlags.containsKey(flagname))) return false;
	else return cFlags.get(flagname);
}

public void setAllowedFields(HashMap<String,String> Fields)
{
	this.allowedFields = Fields;
}


public String getCommand(GWQLCmdBuilderInterface cmdObj) throws GWException
{
  //parse through the clause each time calling the cmdObj BuildString method to 
  //swtch the final string together
	build_outer_string(this.Clause,cmdObj);
	return cmdObj.getFinalCmd();
}

private boolean build_outer_string(String inputstr,GWQLCmdBuilderInterface cmdObj) throws GWException
{
	//Reminder: $arry is substs
	boolean retval=false;
	
	//$v = preg_split("/[\[\]]+/", $inputstr, -1, PREG_SPLIT_NO_EMPTY);
	
	String regExp="[\\[\\]]+ "	;
	String LHS="";
	String RHS="";
	String comp="";
	 List<String> allMatches = new ArrayList<String>();
	 Matcher m = Pattern.compile(regExp)
	     .matcher(inputstr);
	 String equation_sides="";
	 int itemCount=-1;
	 while (m.find()) {
		 equation_sides=m.group().trim();
		 //System.out.println(equation_sides);
		 if ((equation_sides.equals("[")  || equation_sides.equals("]")  || equation_sides.equals(" ") || equation_sides.equals("(") || equation_sides.equals(")") || equation_sides.equals("")))
		  {
		  //donothing ignore
		  }
		 else
		 {
			 itemCount++;
  		    if (itemCount == 0) LHS=equation_sides;
			if (itemCount == 1) comp=equation_sides;
			if (itemCount == 2) RHS=equation_sides;
		  }
	 }		   
		if (comp.trim().equals("_AND_"))
		{
		ArrayList<String> LHS_SIDE = new ArrayList<String>();
		ArrayList<String> RHS_SIDE = new ArrayList<String>();
		LHS_SIDE.add("AND");
		//LHS_SIDE.add("LEFTBRACKET");
		//RHS_SIDE.add("RIGHTBRACKET");
		try {
		rec_build_string(LHS,cmdObj,LHS_SIDE);
		rec_build_string(RHS,cmdObj,null);
		}
		catch (GWException e) { throw e; }
		return true;
		
		}
	
	 
	 if (itemCount == -1) return rec_build_string(inputstr,cmdObj,null);
return retval;
}

private boolean rec_build_string(String cmp,GWQLCmdBuilderInterface cmdObj,ArrayList<String> substs) throws GWException
{
	boolean and_test = cmp.contains(" _AND_ ");
	if (!and_test)
	{
		boolean or_test = cmp.contains(" _OR_ ");
		if (!or_test)
		{
			cmdObj.buildString(cmp,substs,allowedFields);
			return true;
		}
		else
		{
			String[] parts = cmp.split(" _OR_ ");
			ArrayList<String> the_or = new ArrayList<String>();
			the_or.add("OR");
			rec_build_string(parts[0],cmdObj,the_or);
			rec_build_string(parts[1],cmdObj,null);
			return true;
		}
	}
	else
	{
		String[] parts = cmp.split(" _AND_ ");
		ArrayList<String> the_and = new ArrayList<String>();
		the_and.add("AND");
		rec_build_string(parts[0],cmdObj,the_and);
		rec_build_string(parts[1],cmdObj,null);
		return true;
	}	
//	return true;
}

public ArrayList<HashMap<String,String>> getParams()
{
return Params;
}
}




