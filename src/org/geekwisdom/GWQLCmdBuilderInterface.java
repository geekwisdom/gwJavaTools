	package org.geekwisdom;
	
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	
	public interface GWQLCmdBuilderInterface {
	
		public void buildString(String inputstr, ArrayList<String> substs, HashMap<String,String> allowedFields) throws GWException;
				public String getFinalCmd();
		
		
		
	}
