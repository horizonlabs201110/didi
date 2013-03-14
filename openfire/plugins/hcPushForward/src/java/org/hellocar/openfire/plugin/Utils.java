package org.hellocar.openfire.plugin;

import org.slf4j.*;

public class Utils {
	public static final String PLUGIN_NAME = "hcPushForwardPlugin";
	public static final long THREAD_MAIN_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_MAIN_LOOP_MILLISECONDS = 3000;
	
	private static Logger tracer = LoggerFactory.getLogger(PLUGIN_NAME);
	
	public static void Debug(String msg) {
		tracer.debug(msg);
	}
	
	public static void Info(String msg) {
		tracer.info(msg);
	}
	
	public static void Error(String msg) {
		tracer.error(msg);
	}
	
	public static void Error(String msg, Throwable e) {
		tracer.error(msg, e);
	}
}
