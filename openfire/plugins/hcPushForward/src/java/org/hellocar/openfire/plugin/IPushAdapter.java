package org.hellocar.openfire.plugin;

import org.jivesoftware.openfire.OfflineMessage;

public interface IPushAdapter {
	void push(OfflineMessage om, int sn, String token) throws Exception;
}
