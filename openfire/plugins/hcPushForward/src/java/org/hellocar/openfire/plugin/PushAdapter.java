package org.hellocar.openfire.plugin;

import org.xmpp.packet.Message;

public class PushAdapter implements IPushAdapter {
	public static IPushAdapter createInstance() {
		return new IOSPushAdapter();
	}
	
	public void push(Message message) {
		// TODO Auto-generated method stub
	}
}

class IOSPushAdapter implements IPushAdapter {
	public IOSPushAdapter() {
	}
	
	public void push(Message message) {
		// TODO Auto-generated method stub
	}
	
}