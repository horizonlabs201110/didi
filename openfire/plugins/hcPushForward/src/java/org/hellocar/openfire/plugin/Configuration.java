package org.hellocar.openfire.plugin;

import org.jivesoftware.util.JiveGlobals;

public class Configuration {
	public static String PostmanName;
	
	public static void Load() {
		PostmanName = JiveGlobals.getProperty("plugin.hcPushForward.postman", "postman");
	}
}
