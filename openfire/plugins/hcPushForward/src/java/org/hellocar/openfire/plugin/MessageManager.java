package org.hellocar.openfire.plugin;

import java.util.ArrayList;

public class MessageManager implements IManager, Runnable {
	public static IManager createInstance() {
		return new MessageManager();
	}
		
	private ManualEvent mainEvent = null;
	private boolean terminated = false;
	private Thread mainThread = null;
		
	public MessageManager() {
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
				mainThread.join(Utils.THREAD_MESSAGE_JOIN_MILLISECONDS);
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
			try {
				Configuration.load();
				DBMapper.cleanMessage(Configuration.messageRetentionInSeconds);
				Utils.debug("Clean up messages");
			}
			catch (Exception ex) {
				Utils.error("Unexpected error occurs in message cleaning up", ex);
			}
			if (!terminated) { mainEvent.doWait(Configuration.messageIntervalInSeconds * 1000); }
		}
	}
}