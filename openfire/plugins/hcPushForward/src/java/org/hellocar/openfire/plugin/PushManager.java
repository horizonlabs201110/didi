package org.hellocar.openfire.plugin;

import java.util.*;
import java.util.Map.Entry;

public class PushManager implements IManager, Runnable {
	public static IManager createInstance(IPushAdapter push) {
		return new PushManager(push);
	}
		
	private ManualEvent mainEvent = null;
	private boolean terminated = false;
	private Thread mainThread = null;
	private IPushAdapter pusher = null;
		
	public PushManager(IPushAdapter push) {
		if (push == null) {
			throw new IllegalArgumentException("push");
		}
		
		pusher = push;
	}
	
	public void init() {
		terminated = false;
		mainEvent = new ManualEvent();
		mainThread = new Thread(this);
		mainThread.start();
	}
	
	public void terminate() {
		terminated = true;
		if (mainEvent != null) {
			mainEvent.doNotify();
		}
		if (mainThread != null) {
			try {
				mainThread.join(Utils.THREAD_PUSH_JOIN_MILLISECONDS);
			}
			catch (InterruptedException ex) {
				//nothing to do
			}
			catch (Exception ex) {
				//nothing to do
			}
		}
		mainThread = null;
		mainEvent = null;
	}
	
	public void keepalive() {
		if (mainEvent != null) {
			mainEvent.doNotify();
		}		
	}
	
	public void run() {
		while (!terminated) {
			boolean done = false;
			int count = 0;
			ArrayList<MessageEx> mxs = null;
			try {
				Configuration.load();
				done = false;
				do {
					mxs = DBMapper.getAllMessages(MessageType.OFFLINE, MessageStatus.READY);
					count = mxs.size();
					done = count > 0 ? false : true;
					Utils.debug(String.format("Get %1$d offline message ready to push", count));
					
					if (!done) {
						Hashtable<String, UserExtra> userTable = new Hashtable<String, UserExtra>();
						for(MessageEx mx : mxs) {
							String user = mx.message.getTo().getNode();
							if (!userTable.containsKey(user)) {
								userTable.put(user, DBMapper.getUserExtra(user));
							}
						}
						
						for (MessageEx mx : mxs) {
							String user = mx.message.getTo().getNode();
							UserExtra extra = userTable.get(user);
							if (extra != null && extra.iosPush && !extra.iosToken.isEmpty()) {
								try {
									pusher.push(mx, extra.iosToken);
									mx.status = MessageStatus.SUCCEED;
									mx.statusMessage = "";
									Utils.debug(String.format("Push message to user %1$s, %2$s", user, mx.message.toXML()));
								}
								catch (Exception ex) {
									mx.status = MessageStatus.FAIL;
									mx.statusMessage = ex.getMessage();
									Utils.error(String.format("Fail to push message to user %1$s, %2$s, %3$s", user, ex.getMessage(), mx.message.toXML()), ex);
								}
							}
							DBMapper.updateMessageStatus(mx.id, mx.status, mx.statusMessage);
							if (terminated) { break; }
						}
					}
					if (terminated) { break; }
				} while (done);
			}
			catch (Exception ex) {
				Utils.error(String.format("Unexpected error occurs in message pushing, %1$s", ex.getMessage()), ex);
			}
			if (!terminated) { mainEvent.doWait(Configuration.pushIntervalInSeconds * 1000); }
		}
	}
}