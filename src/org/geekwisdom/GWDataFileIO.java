package org.geekwisdom;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

public class GWDataFileIO extends GWDataIO{

	GWDataFileIO(String configfile,String defObj)
	{
	super(configfile,defObj);
	}
	
	public GWDataFileIO()
	{
		//Hmm. what happens here?
	}
	
	
	@Override
	public String insert(String JSONROW, String configfile) {
		LinkedHashMap<String,String> rowobj = super.translate(JSONROW);
		if (this.dataTable == null) this.loadData(configfile);
		this.dataTable.add(rowobj);
		this.saveData(configfile);
		return "SUCCESS";
	}
	
	@Override
	public GWDataTable search(String whereclause, String configfile) throws GWException 
	{
		GWDataTable ret;
		this.loadData(configfile);
	try {
	 ret = this.dataTable.find(whereclause);
	}
	catch (GWException e) { throw e; }
	return ret;
	}
	
	
	private void loadData(String configfile)
	{
		GWSettings settingsManager = new GWSettings();
		String configFile = settingsManager.GetSetting(configfile,"connectionInfo","");
		File f = new File(configFile);
		if (f.exists() && !f.isDirectory())
		{
			List<String> lines;
			try {
				lines = Files.readAllLines(Paths.get(configFile), StandardCharsets.UTF_8);
				String xmlinfo = String.join("\n", lines);
				this.dataTable = new GWDataTable("","root",this.defaultObj);
				this.dataTable.loadXml(xmlinfo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
		
    private void saveData(String configfile)	
    {
		GWSettings settingsManager = new GWSettings();
		String configFile = settingsManager.GetSetting(configfile,"connectionInfo","");
		String xmloutput = this.dataTable.toXml();
		try (PrintWriter out = new PrintWriter(configFile)) 
		    {
		    out.println(xmloutput);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
		@Override
		public String update(String JSONROW, String configfile) {
			// TODO Auto-generated method stub
			return "Inside GWDataFile IO UPDATE!";
		}

		@Override
		public String update(String JSONROW) {
			// TODO Auto-generated method stub
			return "Inside GWDataFile IO UPDATE!";
		}

		
		@Override
		public String delete(String id, String configfile) {
			// TODO Auto-generated method stub
			return "Inside GWDataFile IO DELETE!";
		}

		@Override
		public String lock(String id, String configfile) {
			// TODO Auto-generated method stub
			 return "Inside GWDataFile IO LOCK!";
		}

		@Override
		public String unlock(String id, String configfile) {
			// TODO Auto-generated method stub
			return "Inside GWDataFile IO UNLOCK";
		}

		@Override
		public void open(String configfile) {
			// TODO Auto-generated method stub
			System.out.println("INSIDE OPEN");
			return;
		}

		
		@Override
		public void save(String configfile) {
			// TODO Auto-generated method stub
			System.out.println("INSIDE OPEN");
			return;
		}
    
    
		
}
