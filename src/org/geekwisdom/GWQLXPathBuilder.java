package org.geekwisdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GWQLXPathBuilder implements GWQLCmdBuilderInterface {

	HashMap<String,String> mymap = new HashMap<String,String>();
    ArrayList<HashMap<String,String>> Params;
    String commandPart = "";
    String runningString="";
	public GWQLXPathBuilder()
	{
		
		mymap.put("OR","or");
		mymap.put("AND","and");
		mymap.put("OPENBRACKET","(");
		mymap.put("CLOSEBRACKET",")");
		
		Params = new ArrayList<HashMap<String,String>>();
	}
	
	@Override
	public void buildString(String inputstr,ArrayList<String> substs,HashMap<String,String> allowedFields) {
		// TODO Auto-generated method stub
		//update commandPart based in inputstr
		
			
		try {
			boolean b1 = inputstr.contains("[");
			boolean b2 = inputstr.contains("]");
			if (!(b1 || b2)) throw new GWException("ERROR: MISSING OPENING/CLOSING BRACKET",99);

			String OP="";
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
			if (mycmd.getOperator().equals("_LIKE_")) OP="CONTAINS";
			if (OP.equals("")) throw new GWException("ERROR: MISSING/INCORRECT OPERATOR",98);
			String value=mycmd.getValue().trim();
			
			String part="";
			
			String field=mycmd.getField();
			if (allowedFields.size() > 0)
				{
					if (allowedFields.containsKey(field)) field=allowedFields.get(field);
					else { throw new GWException("INVALID FIELD: " + field,95);}
				}
			if (OP.equals("CONTAINS"))
					{
					part = "contains(" + field + "," + value + ")";
					}
					else
					part = field + OP + value;
			String replace = inputstr.replace(newstring.trim(), part);
			runningString = runningString + replace;
			runningString.replace(newstring, part);
			//remember rplace value with ? and add value to params!
			commandPart = commandPart + part;
			if (substs !=null) for (int i=0;i<substs.size();i++) runningString = runningString + " " + mymap.get(substs.get(i)) + " " ;
		} catch (GWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
		//commandPart =commandPart + " " + inputstr;
		return;
	}

	
	
	public List<HashMap<String, String>> getParams() {
		// TODO Auto-generated method stub
		return Params;
	}
	
	public String getFinalCmd()
	{
		//return commandPart;
		commandPart = runningString.trim();
		if (commandPart.substring(0, 1).equals("[")) commandPart = commandPart.substring(1);
		if (commandPart.substring(commandPart.length()-1, commandPart.length()).equals("]")) commandPart = commandPart.substring(0,commandPart.length()-2);
		//System.out.println(commandPart);
		return commandPart;
	}

}
