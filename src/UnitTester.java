import org.geekwisdom.*;

public class UnitTester {

	public static void main(String[] args) {
		
	//LogUnitTest();
	TestSettings();
	return;
	}

	public static void LogUnitTest()
	{
		GWLogger myLogger = GWLogger.getInstance(5);
		myLogger.WriteLog(2,GWLogger.LogType.Error,"just a test3");	
		return;
	}

public static void TestSettings()
{
	GWSettings mySettings = new GWSettings();
	String r = mySettings.GetSetting("c:\\temp\\settingstest.config","test","default","");
	System.out.println(r);
}
}
