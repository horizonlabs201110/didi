package org.hellocar.openfire.plugin;

import org.xmpp.packet.Message;

public interface IPushAdapter {
	void push(Message om, int sn, String token) throws Exception;
}
