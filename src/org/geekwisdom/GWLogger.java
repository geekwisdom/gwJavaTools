/* *************************************************************************************
' Script Name:   GWLogger.java 
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is a shared component available to all JAVA applications. It allows simple
' @(#)    logging to a central location. The log file name is configurable but defaults to the application name.
' @(#)    The Log is to be initialized with a specified LogVerbosity. It wraps the apache log4js  component
' @(#)    writes to the log file if the LogLevel <= LogVerbosity as defined by the application
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2019-05-19 - Initial Architecture
' GEEKWISDOM.ORG
' **************************************************************************************
'Note: Changing this routine effects all programs that log to a common
'location.
'-------------------------------------------------------------------------------*/
package org.geekwisdom;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.LoggingException;
import java.util.Map;
import java.util.ArrayList;

public class   GWLogger {
	
	private static final Logger logger = LogManager.getLogger();
	private static  GWLogger instance = null;
    private boolean isInitialized=false;
    private String LogName="";
    private int LogVerbosity=10;
		
    public static  GWLogger getInstance()
    {
    	if (instance != null) return instance;
    	instance = new  GWLogger();
    	return instance;
    	
    }
    
    public static  GWLogger getInstance(int Verbosity)
    {
    	if (instance != null) return instance;
    	instance = new  GWLogger();
    	instance.Initialize(Verbosity);
    	return instance;
    	
    }

    public void Initialize() throws LoggingException
    {
        Initialize(null,10,null);
    }

       
    public  void Initialize(int Verbosity) throws LoggingException
    {
    	Initialize(null,Verbosity,"logname.txt");
    }
    public  void Initialize(String configFile,int Verbosity, String LogName) throws LoggingException
    {
        if (!isInitialized)
        {
            LoggerConstruct(Verbosity, LogName);
            /* Not used in log4j2
            if (configFile !=null && configFile != "")
                
            	XmlConfigurator.ConfigureAndWatch(new FileInfo(configFile));
            else
                XmlConfigurator.Configure();
                */
            isInitialized = true;
        }
        else
            throw new LoggingException("Logging has already been initialized.");
    }

    private void LoggerConstruct(int Verbosity, String _LogName)
  {
      LogVerbosity = Verbosity;
      LogName = _LogName;
  }

    
    public  GWLogger(){	}
        //public static void LogInfo(string LogItem, int LogLevel, LogType ltype = LogType.Debug, Exception exception = null)
		public void LogInfo(String LogItem, int LogLevel, LogType ltype,Exception e) 
	{
            if (LogLevel <= LogVerbosity)
            {
                if (ltype == LogType.Debug) DoLog("DebugLogger", ltype, LogItem, e);
                else if (ltype == LogType.Security) DoLog("SecurityLogger", ltype, LogItem, e);
                else DoLog(null, ltype, LogItem + "," + LogLevel, e);
            }

			//logger.error(LogItem);
		//System.out.println(LogItem);
            
	}
		
        private void DoLog(String LogFile, LogType ltype, String Message, Exception exception)
        {
            if (LogFile == null)
            //Log to all running loggers
            {
            	for(String logname : GetAllLoggers()) 
                {
                    //Console.WriteLine("Here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                	Logger log = LogManager.getLogger(); 
                	LogMain(log, ltype, Message, exception);
                }
            }
            else
            {
                //Log to a specific logger!
                Logger log = LogManager.getLogger(LogFile);
                if (log != null)
                {
                    //Console.WriteLine("Here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + LogFile);
                    LogMain(log, ltype, Message, exception);
                }
                else
                    throw new LoggingException("The log \"" + LogFile + "\" does not exist or is invalid.");
            }
        }

            private void LogMain(Logger log, LogType ltype, String message, Exception exception)
            {
                String msg=message;
                if (exception != null) msg=msg + " Ex: - " + exception.getMessage();
            	if (ShouldLog(log, ltype))
                {
                    switch (ltype)
                    {
                        
                    case Debug: log.debug(msg); break;
                        case Info: log.info(msg); break;
                        case Warning: log.warn(msg); break;
                        case Error: log.error(msg); break;
                        case Fatal: log.fatal(msg); break;
                        case Security: log.info(msg); break;
                    }
                }

            }
            
        private ArrayList<String> GetAllLoggers()
        {
        	LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
        	Map<String, LoggerConfig> allogs= logContext.getConfiguration().getLoggers();
        	ArrayList<String> retval = new ArrayList<String>();
        	for (Map.Entry<String, LoggerConfig> entry : allogs.entrySet())
        		
        	{
        		retval.add(entry.getKey());
        	}
            return retval;
        }
        
            private static boolean ShouldLog(Logger log, LogType ltype)
            
            {
                switch (ltype)
                {
                    case Debug: return log.isDebugEnabled();
                    case Info: return log.isInfoEnabled();
                    case Warning: return log.isWarnEnabled();
                    case Error: return log.isErrorEnabled();
                    case Fatal: return log.isFatalEnabled();
                    case Security: return true;
                    default: return false;
                }
            }

            
            public void LogInfo(String LogItem, int LogLevel)
		{
		LogInfo(LogItem,LogLevel,LogType.Debug,null);
		}
            
           
            public void LogInfo(String LogItem, int LogLevel,LogType ltype)
		{
		LogInfo(LogItem,LogLevel,ltype,null);
		}
            public void WriteLog(int LogLevel, LogType ltype,String message, Exception exception)
            {
            	
            	LogInfo(message,LogLevel,ltype,exception);
            }
            
            public void WriteLog(int LogLevel, LogType ltype,String message)
            {
            LogInfo(message,LogLevel,ltype);
            }
            

            
            public enum LogType 
		{
		    Debug,Info,Warning, Error,Fatal, Security
		}	

}

   

    

	
	


