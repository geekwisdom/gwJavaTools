package org.geekwisdom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;  
public class GWParsedCommand {

private String Field;
private String Operator;
private String Value;

	public GWParsedCommand(String thecommand) throws GWException
{
	doParse(thecommand);
}

private void doParse(String thecommand) throws GWException
{
	//actual parse
	String tmpField="";
	String tmpOp = "";
	String tmpValue="";
	String regExp="\"(?:\\\\\\\\.|[^\\\\\\\\\"])*\"|\\S+";	
	
	 List<String> allMatches = new ArrayList<String>();
	 Matcher m = Pattern.compile(regExp)
	     .matcher(thecommand);
	 int i=0;
	 int bracketCount=0;
	 String cmdItem="";
	 while (m.find()) {

		  cmdItem=m.group();
		  if (cmdItem.equals("["))
		  {
			  bracketCount++;
			  if (i != 0 ) throw new GWException("GWQL SYNTAX ERROR MISSING OPEN BRACKET [  "+ thecommand ,23);
			  
		  }
		  
		  else if (cmdItem.equals("]"))
		  {
			  bracketCount++;
			  if (i != 3 ) throw new GWException("GWQL SYNTAX ERROR MISSING CLOSE BRACKET ] "+ thecommand ,23);
		  }
		  
		  else if (cmdItem.equals("(") || cmdItem.equals(")") || cmdItem.equals(""))
		  {
			  //ignore it
		  }
		  else
				{
				 
		   if (i == 0) tmpField=cmdItem;
		   if (i == 1) tmpOp = cmdItem;
		  if (i == 2) tmpValue = cmdItem;		  
		    i++;
				}
	
		    }
	 
	 
	 if (bracketCount != 2) throw new GWException("GWQL SYNTAX ERROR MISSING BRACKETS "+ thecommand ,23);
	 if (i > 3) throw new GWException("GWQL SYNTAX ERROR at "+ cmdItem,23);
	 if (tmpField.equals(""))  throw new GWException("GWQL SYNTAX ERROR MISSING FIELD IN " + thecommand,24);
	 if (tmpOp.equals("")) throw new GWException("GWQL SYNTAX ERROR MISSING OPERATOR IN " + thecommand,25);
	 if (tmpValue.equals("")) throw new GWException("GWQL SYNTAX ERROR MISSING VALUE IN " + thecommand,26);
     this.Field =tmpField;
     this.Operator = tmpOp;
     this.Value = tmpValue;
		 //System.out.println(m.group());
		 //allMatches.add(m.group());
	 }
	
	 

	
public String getField() { return Field;} 
public String getOperator() {return Operator; }
public String getValue() { return Value; }

}
