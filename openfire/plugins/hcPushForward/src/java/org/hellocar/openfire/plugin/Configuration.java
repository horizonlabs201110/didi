package org.hellocar.openfire.plugin;

import org.jivesoftware.util.JiveGlobals;

public class Configuration {
	public static String userPostman = "postman";
	public static String messageForward = "forward";
	public static String messageIOSToken = "iostoken";
	public static String messageIOSPush = "iospush";
	public static boolean switchPostmanMessage = true;
	public static boolean switchOfflineMessage = true;
	public static long pushIntervalInSeconds  = 10;	//10 Seconds
	public static long forwardIntervalInSeconds = 600;	//10 Minutes
	public static long messageIntervalInSeconds = 3600; //1 Hour
	public static long messageRetentionInSeconds = 86400; //1 Day
	public static String iosPushKeyStore = null;
	public static String iosPushPassword = null;
	public static int iosPushThread = 3;
	public static boolean iosPushProduction = true;
		
	public static void load() {
		switchPostmanMessage = JiveGlobals.getBooleanProperty("plugin.hcPushForward.switchPostmanMessage", true);
		switchOfflineMessage = JiveGlobals.getBooleanProperty("plugin.hcPushForward.switchOfflineMessage", true);
		pushIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.pushIntervalInSeconds", 10);
		forwardIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.forwardIntervalInSeconds", 600);
		messageIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.messageIntervalInSeconds", 3600);
		messageRetentionInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.messageRetentionInSeconds", 86400);
		iosPushKeyStore = JiveGlobals.getProperty("plugin.hcPushForward.iosPushKeyStore");
		iosPushPassword = JiveGlobals.getProperty("plugin.hcPushForward.iosPushPassword");
		iosPushThread = JiveGlobals.getIntProperty("plugin.hcPushForward.iosPushThread", 3);
		iosPushProduction = JiveGlobals.getBooleanProperty("plugin.hcPushForward.iosPushProduction", true);
	}
}