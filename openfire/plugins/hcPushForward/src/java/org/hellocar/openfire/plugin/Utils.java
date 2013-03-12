package org.hellocar.openfire.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	public static final String PLUGIN_NAME = "hcPushForwardPlugin";
	private static Logger tracer = LoggerFactory.getLogger(Utils.PLUGIN_NAME);
	
	public static void Debug(String msg) {
		
	}
	
	public static void Info(String msg) {
		tracer.error(arg0);
	}
	
	public static void Error(String msg) {
		
	}
	
	public static void Error(String msg, Throwable e) {
		tracer.error(msg, e);
	}
}
