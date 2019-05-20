import org.geekwisdom.*;

public class UnitTester {

	public static void main(String[] args) {
		
	LogUnitTest();		
	return;
	}

	public static void LogUnitTest()
	{
		GWLogger myLogger = GWLogger.getInstance(5);
		myLogger.WriteLog(2,GWLogger.LogType.Error,"just a test3");	
		return;
	}
}
