package tw.darkk6.mcmod.imetweak.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tw.darkk6.mcmod.imetweak.config.Reference;

public class Log {
	public static Logger log=null;
	
	private static void initLogger(){
		if(log==null) log=LogManager.getLogger(Reference.LOG_TAG);
	}
	
	public static Logger info(String str){
		initLogger();
		log.info(str);
		return log;
	}
}