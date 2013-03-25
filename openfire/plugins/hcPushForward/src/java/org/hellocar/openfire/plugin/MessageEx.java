package org.hellocar.openfire.plugin;

import org.xmpp.packet.Message;

class MessageEx {
	public Message message;
	public long id;
	public MessageStatus status;
	public String statusMessage;
	public MessageType type;
	public long lastModified;
	
	public MessageEx(Message message, MessageType type, MessageStatus status) {
		this.message = message;
		this.status = status;
		this.type = type;
		this.id = 0;
		this.statusMessage = null;
		this.lastModified = Utils.getNow();
	}
	
	public MessageEx() {
	}
}

enum MessageStatus { 
	QUEUE(1), READY(2), FAIL(9), SUCCEED(10);
	
	private final int idx;
	
	MessageStatus(int index) {
		this.idx = index;
	}
	
	public int toInt(){
		return this.idx;
    }
	
	public static MessageStatus parse(int index) {
		switch (index) {
		case 1:
			return MessageStatus.QUEUE;
		case 2:
			return MessageStatus.READY;
		case 9:
			return MessageStatus.FAIL;
		case 10:
			return MessageStatus.SUCCEED;
		default:
			throw new IllegalArgumentException("index");
		}
	}
}
    
enum MessageType { 
	OFFLINE(1), POSTMAN(2);
	
	private int idx;
	
	MessageType(int index) {
		this.idx = index;
	}
	
	public int toInt(){
   	 return this.idx;
    }
	
	public static MessageType parse(int index) {
		switch (index) {
		case 1:
			return MessageType.OFFLINE;
		case 2:
			return MessageType.POSTMAN;
		default:
			throw new IllegalArgumentException("index");
		}
	}
}