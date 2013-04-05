package org.hellocar.openfire.plugin;

import java.util.*;

public class PushManager implements IManager, Runnable {
	public static IManager createInstance(IPushAdapter push, IOfflineMessageAccessor accessor) {
		return new PushManager(push, accessor);
	}
		
	private ManualEvent mainEvent = null;
	private boolean terminated = false;
	private Thread mainThread = null;
	private IPushAdapter pusher = null;
	private IOfflineMessageAccessor omaccessor = null;
		
	public PushManager(IPushAdapter push, IOfflineMessageAccessor accessor) {
		if (push == null) {
			throw new IllegalArgumentException("push");
		}
		if (accessor == null) {
			throw new IllegalArgumentException("accessor");
		}
		
		pusher = push;
		omaccessor = accessor;
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
			ArrayList<MessagePush> mps = null;
			try {
				Configuration.load();
				done = false;
				do {
					mps = DBMapper.getAllMessagesForPush();
					count = mps == null ? 0 : mps.size();
					done = count > 0 ? false : true;
					Utils.debug(String.format("Get %1$d offline message ready to push", count));
					if (!done) {
						Hashtable<String, Integer> ut = new Hashtable<String, Integer>();
						for(MessagePush mp : mps) {
							String userName = mp.message.getTo().getNode();
							try {
								if (mp.iostoken == null || mp.iostoken.isEmpty() || mp.iostoken.length() != 64) {
									mp.status = MessageStatus.FAIL;
									mp.statusMessage = "push disabled";
									Utils.debug(String.format("Push disabled on user %1$s", userName));
								}
								else {
									if (!ut.containsKey(userName)) {
										ut.put(userName, omaccessor.getMessageCount(userName));
									}
									pusher.push(mp.message, ut.get(userName), mp.iostoken);
									mp.status = MessageStatus.SUCCEED;
									mp.statusMessage = "";
									Utils.debug(String.format("Push message to user %1$s, %2$s", userName, mp.message.toXML()));
								}
							}
							catch (Exception ex) {
								mp.status = MessageStatus.FAIL;
								mp.statusMessage = ex.getMessage();
								Utils.error(String.format("Fail to push message to user %1$s, %2$s, %3$s", userName, ex.getMessage(), mp.message.toXML()), ex);
							}
							DBMapper.updateMessageStatus(mp.id, mp.status, mp.statusMessage);
							if (terminated) { break; }
						}
					}
					if (terminated) { break; }
				} while (!done);
			}
			catch (Exception ex) {
				Utils.error(String.format("Unexpected error occurs in message pushing, %1$s", ex.getMessage()), ex);
			}
			if (!terminated) { mainEvent.doWait(Configuration.pushIntervalInSeconds * 1000); }
		}
	}
}