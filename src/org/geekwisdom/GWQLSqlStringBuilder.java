package org.geekwisdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GWQLSqlStringBuilder implements GWQLCmdBuilderInterface {

    HashMap<String,String> mymap = new HashMap<String,String>();
    ArrayList<String> params = new ArrayList<String>();
    String commandPart = "";
    String runningString="";
	public GWQLSqlStringBuilder()
	{
		
		mymap.put("OR","OR");
		mymap.put("AND","AND");
		mymap.put("OPENBRACKET","(");
		mymap.put("CLOSEBRACKET",")");
		
		
	}
	
	@Override
	public void buildString(String inputstr,ArrayList<String> substs,HashMap<String,String> allowedFields) throws GWException {
		// TODO Auto-generated method stub
		//update commandPart based in inputstr
		
			
		
			String OP="";
			boolean b1 = inputstr.contains("[");
			boolean b2 = inputstr.contains("]");
			if (!(b1 || b2)) throw new GWException("ERROR: MISSING OPENING/CLOSING BRACKET",99);
			
			String newstring = inputstr.replace("[","");
			newstring = newstring.replace("]", "");
			String bracketstring = "[ " + newstring + " ]";
			GWParsedCommand mycmd = new GWParsedCommand(bracketstring);
			if (mycmd.getOperator().equals("_EQ_")) OP="=";
			if (mycmd.getOperator().equals("_LT_")) OP="<";
			if (mycmd.getOperator().equals("_LE_")) OP="<=";
			if (mycmd.getOperator().equals("_GT_")) OP=">";
			if (mycmd.getOperator().equals("_GE_")) OP=">=";
			if (mycmd.getOperator().equals("_NE_")) OP="!=";
			if (mycmd.getOperator().equals("_LIKE_")) OP=" LIKE ";
			if (OP.equals("")) throw new GWException("ERROR: MISSING/INCORRECT OPERATOR",98);
			String value=mycmd.getValue().replace("\"","");
			value=value.replace("'","");
			
			String field=mycmd.getField();
			if (allowedFields.size() > 0)
				{
					if (allowedFields.containsKey(field)) field=allowedFields.get(field);
					else { throw new GWException("INVALID FIELD: " + field,95);}
				}
			
			String part = field + OP + "?";
			params.add(value);
			String replace = inputstr.replace(newstring.trim(), part);
			runningString = runningString + replace;
			runningString.replace(newstring, part);
			//remember rplace value with ? and add value to params!
			commandPart = commandPart + part;
			if (substs !=null) for (int i=0;i<substs.size();i++) runningString = runningString + " " + mymap.get(substs.get(i)) + " " ;
		 
		
				
		//commandPart =commandPart + " " + inputstr;
		return;
	}

	
	
	public ArrayList<String> getParams() {
		// TODO Auto-generated method stub
		return params;
	}
	
	public String getFinalCmd()
	{
		//return commandPart;
		commandPart = runningString.replace("[", mymap.get("OPENBRACKET"));
		commandPart = commandPart.replace("]", mymap.get("CLOSEBRACKET"));
		if (commandPart.substring(0, 1).equals("(")) commandPart = commandPart.substring(1);
		if (commandPart.substring(commandPart.length()-1, commandPart.length()).equals(")")) commandPart = commandPart.substring(0,commandPart.length()-2);
		return commandPart;
	}
}
