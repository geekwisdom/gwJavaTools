import java.util.HashMap;
import java.util.LinkedHashMap;

import org.geekwisdom.*;

import org.geekwisdom.GWDataRow;
import org.geekwisdom.GWDataTable;

public class student extends GWDataRow {
	
	public student (HashMap<String,String> i)
	{
	super(i);
	}

	private static final HashMap<String,String> _construct(String Name, String Address)
	{
		HashMap<String,String> i = new HashMap<String,String>();
		i.put("Name",Name);
		i.put("Address",Address);
		return i;
	}
	public student(String Name, String Address)
	{

		super(student._construct(Name,Address));
	}
public String getName()
{
return super.get("Name");
}
	
}
