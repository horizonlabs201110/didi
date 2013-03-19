package org.hellocar.openfire.plugin;

import org.xmpp.packet.Message;

class MessageEx {
	public Message message;
	public long id;
	public MessageStatus status;
	public String statusMessage;
	public MessageType type;
	public long lastModified;
}

enum MessageStatus { 
	QUEUE(1), READY(2), FAIL(9), SUCCEED(10);
	
	private int value;
	
	private int getValue(){
   	 return this.value;
    }
	
	private MessageStatus(int value) {
		this.value = value;
	}
}

enum MessageType { 
	OFFLINE(1), POSTMAN(2);
	
	private int value;
	
	private int getValue(){
   	 return this.value;
    }
	
	private MessageType(int value) {
		this.value = value;
	}
}