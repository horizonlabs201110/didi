package org.hellocar.openfire.plugin;

import org.xmpp.packet.Message;

public interface IMessageHandler {
	boolean validate(Message message);
	void process(Message message) throws Exception;
}