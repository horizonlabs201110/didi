package org.hellocar.openfire.plugin;

import org.jivesoftware.util.JiveGlobals;

public class Configuration {
	public static String PostmanName = "postman";
	
	public static void load() {
		PostmanName = JiveGlobals.getProperty("plugin.hcPushForward.postman", "postman");
	}
}
