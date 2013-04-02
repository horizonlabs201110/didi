package org.hellocar.openfire.plugin;

import java.util.ArrayList;

public class ForwardManager implements IManager, Runnable {
	public static IManager createInstance(IForwardAdapter forward) {
		return new ForwardManager(forward);
	}
		
	private ManualEvent mainEvent = null;
	private boolean terminated = false;
	private Thread mainThread = null;
	private IForwardAdapter forwarder = null;
		
	public ForwardManager(IForwardAdapter forward) {
		if (forward == null) {
			throw new IllegalArgumentException("forward");
		}
		forwarder = forward;
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
				mainThread.join(Utils.THREAD_FORWARD_JOIN_MILLISECONDS);
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
					mxs = DBMapper.getAllMessages(MessageType.POSTMAN, MessageStatus.READY);
					count = mxs == null ? 0 : mxs.size();
					done = count > 0 ? false : true;
					Utils.debug(String.format("Get %1$d postman message ready to forward", count));
					
					if (!done) {
						for(MessageEx mx : mxs) {
							try {
								forwarder.forward(mx.message);
								mx.status = MessageStatus.SUCCEED;
								mx.statusMessage = "";
								Utils.debug(String.format("Forward message, %1$s", mx.message.toXML()));
							}
							catch (Exception ex) {
								mx.status = MessageStatus.FAIL;
								mx.statusMessage = ex.getMessage();
								Utils.error(String.format("Fail to forward message, %1$s, %2$s", ex.getMessage(), mx.message.toXML()), ex);
							}
							DBMapper.updateMessageStatus(mx.id, mx.status, mx.statusMessage);
							if (terminated) { break; }
						}
					}
					if (terminated) { break; }
				} while (done);
			}
			catch (Exception ex) {
				Utils.error(String.format("Unexpected error occurs in message forwarding, %1$s", ex.getMessage()), ex);
			}
			if (!terminated) { mainEvent.doWait(Configuration.forwardIntervalInSeconds * 1000); }
		}
	}
}