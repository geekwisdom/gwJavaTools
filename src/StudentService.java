import org.geekwisdom.GWDataIO;
import org.geekwisdom.GWDataTable;

public class StudentService extends GWDataIO {
public StudentService()
{
	//GWDataTable mytable = new GWDataTable("","root","student");
	super("c:\\temp\\DataIOTest.config","student");
}
}
