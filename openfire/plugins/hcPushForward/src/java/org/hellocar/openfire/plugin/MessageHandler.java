package org.hellocar.openfire.plugin;

import java.sql.SQLException;
import java.util.Hashtable;

import org.dom4j.Element;

import org.xmpp.packet.*;

public class MessageHandler {
	private static Hashtable<MessageType, IMessageHandler> handlerTable = new Hashtable<MessageType, IMessageHandler>(); 
	private static Object handlerLock = new Object();
	
	public static IMessageHandler GetHandler(MessageType type) {
		if (!handlerTable.containsKey(type)) {
			synchronized(handlerLock) {
				if (!handlerTable.containsKey(type)) {
					if (type == MessageType.OFFLINE) {
						handlerTable.put(type, new OfflineMessageHandler());
					}
					else if (type == MessageType.POSTMAN) {
						handlerTable.put(type, new PostmanMessageHandler());
					}
					else {
						throw new IllegalArgumentException("type");
					}
				}
			}
		}
		return handlerTable.get(type);
	}
}

class PostmanMessageHandler implements IMessageHandler {
	public PostmanMessageHandler() {
	}
	
	public void Process(Message message) throws SQLException {
		if (message == null) {
			throw new IllegalArgumentException("message");
		}
		
		Message msg = message.createCopy();
		Element emsg = msg.getElement();
		String iostoken = null;
		boolean iospush = true;
		Element eiostoken = emsg.element(Configuration.messageIOSToken);
		if (eiostoken != null) {
			iostoken = eiostoken.getTextTrim();
			eiostoken.detach();
		}
		Element eiospush = emsg.element(Configuration.messageIOSPush);
		if (eiospush != null) {
			iospush = Boolean.parseBoolean(eiospush.getTextTrim());
			eiospush.detach();
		}
		Element eforward = emsg.element(Configuration.messageForward);
		if (eforward != null) {
			msg.setTo(new JID(eforward.getTextTrim()));
			eforward.detach();
			DBMapper.addMessage(new MessageEx(msg, MessageType.POSTMAN, MessageStatus.QUEUE));
		}
		if (eiospush != null || eiostoken != null) { 
			UserExtra ue = new UserExtra(msg.getFrom().getNode(), iospush, iostoken);
			if (eiospush != null && eiostoken != null) {
				DBMapper.updateUserExtra(ue);
			}
			else if (eiospush != null) {
				DBMapper.updateUserPush(ue);
			}
			else {
				DBMapper.updateUserToken(ue);
			}
		}
	}
}

class OfflineMessageHandler implements IMessageHandler {
	public OfflineMessageHandler() {
	}
	
	public void Process(Message message) throws SQLException {
		if (message == null) {
			throw new IllegalArgumentException("message");
		}
		
		Message msg = message.createCopy();
		DBMapper.addMessage(new MessageEx(msg, MessageType.OFFLINE, MessageStatus.READY));
	}
}