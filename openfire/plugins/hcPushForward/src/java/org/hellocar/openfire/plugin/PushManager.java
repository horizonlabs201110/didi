package org.hellocar.openfire.plugin;

import java.util.*;

import org.jivesoftware.openfire.*;

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
			ArrayList<MessageEx> mxs = null;
			try {
				Configuration.load();
				done = false;
				do {
					mxs = DBMapper.getAllMessages(MessageType.OFFLINE, MessageStatus.READY);
					count = mxs.size();
					done = count > 0 ? false : true;
					if (!done) {
						Hashtable<String, UserExtra> userTable = new Hashtable<String, UserExtra>();
						for(MessageEx mx : mxs) {
							String userName = mx.message.getTo().getNode();
							if (!userTable.containsKey(userName)) {
								userTable.put(userName, DBMapper.getUserExtra(userName));
							}
						}
						
						for (String userName : userTable.keySet()) {
							UserExtra userExtra = userTable.get(userName);
							if (userExtra != null && userExtra.iosPush && !userExtra.iosToken.isEmpty()) {
								Collection<OfflineMessage> oms = omaccessor.getMessages(userName, false);
								if (oms != null && oms.size() > 0) {
									Utils.debug(String.format("Get %1$d offline message ready to push to user %2$s", oms.size(), userName));
									
									int sn = 0;
									for (OfflineMessage om : oms) {
										sn ++;
										try {
											pusher.push(om, sn, userExtra.iosToken);
											Utils.debug(String.format("Push message, %1$s", om.toXML()));
										}
										catch (Exception ex) {
											Utils.error(String.format("Fail to push message to user %1$s, %2$s, %3$s", userName, ex.getMessage(), om.toXML()), ex);
										}
									}
								}
								DBMapper.updateOfflineMessageStatus(userName, MessageStatus.SUCCEED, "");
							}
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