 package org.hellocar.openfire.plugin;

import java.util.*;

import org.slf4j.*;

public class Utils {
	public static final String PLUGIN_NAME = "hcPushForwardPlugin";
	public static final long THREAD_PUSH_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_FORWARD_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_MESSAGE_JOIN_MILLISECONDS = 3000;
	
	private static Logger tracer = LoggerFactory.getLogger(PLUGIN_NAME);
	
	public static long getNow() {
		return (new Date()).getTime();
	}
	
	public static void debug(String msg) {
		tracer.debug(msg);
	}
	
	public static void info(String msg) {
		tracer.info(msg);
	}
	
	public static void error(String msg) {
		tracer.error(msg);
	}
	
	public static void error(String msg, Throwable e) {
		tracer.error(msg, e);
	}
}