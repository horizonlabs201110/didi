package org.hellocar.openfire.plugin;

import java.util.Hashtable;

import org.dom4j.Element;

import org.xmpp.packet.*;

public class MessageHandler {
	private static Hashtable<MessageType, IMessageHandler> handlerTable = new Hashtable<MessageType, IMessageHandler>(); 
	private static Object handlerLock = new Object();
	
	public static IMessageHandler getHandler(MessageType type) {
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
	
	public boolean validate(Message message) {
		if (message == null) {
			return false;
		}
		return true;
	}
	
	public void process(Message message) throws Exception {
		if (!validate(message)) {
			throw new Exception("invalid postman message");
		}
		
		Message msg = message.createCopy();
		String target = msg.getTo().getNode();
		Element emsg = msg.getElement();
		Element eiostoken = emsg.element(Configuration.messageIOSToken);
		Element eiospush = emsg.element(Configuration.messageIOSPush);
		Element eforward = emsg.element(Configuration.messageForward);
		
		String iostoken = null;
		if (eiostoken != null) {
			iostoken = eiostoken.getTextTrim();
			eiostoken.detach();
		}
		
		boolean iospush = true;
		if (eiospush != null) {
			iospush = Boolean.parseBoolean(eiospush.getTextTrim());
			eiospush.detach();
		}
		
		boolean forward = false;
		if (!target.equalsIgnoreCase(Configuration.userPostman)) {
			forward = true;
		}
		else if (eforward != null) {
			msg.setTo(new JID(eforward.getTextTrim()));
			eforward.detach();
			forward = true;
		}
		if (forward) {
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
	
	public boolean validate(Message message) {
		if (message == null) {
			return false;
		}
		
		String to = message.getTo().getNode();
		if (to == null || to.isEmpty()) {
			return false;
		}
		
		String from = message.getFrom().getNode();
		if (from == null || from.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	public void process(Message message) throws Exception {
		if (!validate(message)) {
			throw new Exception("invalid offline message");
		}
		
		Message msg = message.createCopy();
		DBMapper.addMessage(new MessageEx(msg, MessageType.OFFLINE, MessageStatus.READY));
	}
}