package org.hellocar.openfire.plugin;

import org.jivesoftware.util.JiveGlobals;

public class Configuration {
	public static String postmanName = "postman";
	public static String forwardElementName = "forward";
	public static long pushIntervalInSeconds  = 10;	//10 Seconds
	public static long forwardIntervalInSeconds = 600;	//10 Minutes
	public static long messageIntervalInSeconds = 3600; //1 Hour
	public static long messageRetentionInSeconds = 86400; //1 Day
		
	public static void load() {
		postmanName = JiveGlobals.getProperty("plugin.hcPushForward.postman", "postman");
		forwardElementName = JiveGlobals.getProperty("plugin.hcPushForward.forwardElementName", "forward");
		pushIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.pushIntervalInSeconds", 10);
		forwardIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.forwardIntervalInSeconds", 600);
		messageIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.messageIntervalInSeconds", 3600);
		messageRetentionInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.messageRetentionInSeconds", 86400);
	}
}