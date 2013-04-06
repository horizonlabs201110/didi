package org.hellocar.openfire.plugin;

import org.jivesoftware.util.JiveGlobals;

public class Configuration {
	public static String userPostman = "postman";
	public static String messageForward = "forward";
	public static String messageIOSToken = "iostoken";
	public static String messageIOSPush = "iospush";
	public static boolean switchPostmanMessage = true;
	public static boolean switchOfflineMessage = true;
	public static long pushIntervalInSeconds  = 600;	//10 Minutes
	public static long forwardIntervalInSeconds = 600;	//10 Minutes
	public static long messageIntervalInSeconds = 86400; //1 Day
	public static long messageRetentionInSeconds = 86400; //1 Day
	public static boolean iosPushEnabled = false;
	public static boolean iosPushProduction = false;
	public static String iosPushKeyStore = null;
	public static String iosPushPassword = null;
	public static int iosPushThread = 2;
	public static int iosPushAlertMaxLen = 32;
	public static String iosPushAlertSuffix = "...";
	
	public static void load() {
		switchPostmanMessage = JiveGlobals.getBooleanProperty("plugin.hcPushForward.switchPostmanMessage", true);
		switchOfflineMessage = JiveGlobals.getBooleanProperty("plugin.hcPushForward.switchOfflineMessage", true);
		pushIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.pushIntervalInSeconds", 600);
		forwardIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.forwardIntervalInSeconds", 600);
		messageIntervalInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.messageIntervalInSeconds", 86400);
		messageRetentionInSeconds = JiveGlobals.getLongProperty("plugin.hcPushForward.messageRetentionInSeconds", 86400);
		iosPushEnabled = JiveGlobals.getBooleanProperty("plugin.hcPushForward.iosPushEnabled", false);
		iosPushProduction = JiveGlobals.getBooleanProperty("plugin.hcPushForward.iosPushProduction", false);
		iosPushKeyStore = JiveGlobals.getProperty("plugin.hcPushForward.iosPushKeyStore", null);
		iosPushPassword = JiveGlobals.getProperty("plugin.hcPushForward.iosPushPassword", null);
		iosPushThread = JiveGlobals.getIntProperty("plugin.hcPushForward.iosPushThread", 2);
		iosPushAlertMaxLen = JiveGlobals.getIntProperty("plugin.hcPushForward.iosPushAlertMaxLen", 32);
		iosPushAlertSuffix = JiveGlobals.getProperty("plugin.hcPushForward.iosPushAlertSuffix", "...");
	}
}