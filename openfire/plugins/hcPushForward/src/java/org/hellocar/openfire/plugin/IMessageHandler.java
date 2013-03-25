package org.hellocar.openfire.plugin;

import java.sql.SQLException;

import org.xmpp.packet.Message;

public interface IMessageHandler {
	void Process(Message message) throws SQLException;
}