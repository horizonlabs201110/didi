package org.hellocar.openfire.plugin;

import java.util.ArrayList;

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
						for(MessageEx mx : mxs) {
							try {
								pusher.push(mx.message);
								mx.status = MessageStatus.SUCCEED;
								mx.statusMessage = "";
								Utils.debug(String.format("Push message, %1$s", mx.message.toString()));
							}
							catch (Exception ex) {
								mx.status = MessageStatus.FAIL;
								mx.statusMessage = ex.getMessage();
								Utils.error(String.format("Fail to push message, %1$s", mx.message.toString()), ex);
							}
							DBMapper.updateMessageStatus(mx.id, mx.status, mx.statusMessage);
							if (terminated) { break; }
						}
					}
					if (terminated) { break; }
				} while (done);
			}
			catch (Exception ex) {
				Utils.error("Unexpected error occurs in message pushing", ex);
			}
			if (!terminated) { mainEvent.doWait(Configuration.pushIntervalInSeconds * 1000); }
		}
	}
}