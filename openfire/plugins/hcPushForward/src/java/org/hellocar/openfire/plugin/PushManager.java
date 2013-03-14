package org.hellocar.openfire.plugin;

import java.util.ArrayList;

import org.xmpp.packet.*;

public class PushManager implements IPushForwardManager, Runnable {
	public static IPushForwardManager CreateInstance(IPushAdapter push, IForwardAdapter forward) {
		return new PushManager(push, forward);
	}
		
	private ManualEvent mainEvent = null;
	private boolean terminated = false;
	private Thread mainThread = null;
	private IPushAdapter pusher = null;
	private IForwardAdapter forwarder = null;
		
	public PushManager(IPushAdapter push, IForwardAdapter forward) {
		if (push == null) {
			throw new IllegalArgumentException("push");
		}
		if (forward == null) {
			throw new IllegalArgumentException("forward");
		}
		
		pusher = push;
		forwarder = forward;
	}
	
	public void Init() {
		terminated = false;
		mainEvent = new ManualEvent();
		mainThread = new Thread(this);
		mainThread.start();
	}
	
	public void Terminate() {
		terminated = true;
		if (mainEvent != null) {
			mainEvent.doNotifyAll();
			mainEvent = null;
		}
		if (mainThread != null) {
			try {
				mainThread.join(Utils.THREAD_MAIN_JOIN_MILLISECONDS);
			}
			catch (InterruptedException ex) {
				//nothing to do
			}
			catch (Exception ex) {
				//nothing to do
			}
			finally {
				mainThread = null;
			}
		}
	}
	
	public void run() {
		try {
			while (!terminated) {
				Configuration.load();
				
				ArrayList<MessageEx> offlineMessages = DBMapper.getAllOfflineMessages();
				for(MessageEx mex : offlineMessages) {
					try {
						pusher.push(mex.message);
						mex.Status = MessageStatus.SUCCEED;
						mex.StatusMessage = "";
					}
					catch (Exception ex) {
						mex.Status = MessageStatus.FAIL;
						mex.StatusMessage = ex.toString();
					}
					DBMapper.updateMessage(mex);
					
					if (terminated) { break; }
				}
				
				if (terminated) { continue; }
				
				ArrayList<MessageEx> forwardMessages = DBMapper.getAllForwardMessages();
				for(MessageEx mex : forwardMessages) {
					try {
						forwarder.forward(mex.message);
						mex.Status = MessageStatus.SUCCEED;
						mex.StatusMessage = "";
					}
					catch (Exception ex) {
						mex.Status = MessageStatus.FAIL;
						mex.StatusMessage = ex.toString();
					}
					DBMapper.updateMessage(mex);
					
					if (terminated) { break; }
				}
				
				if (terminated) { continue; }
				
				mainEvent.wait(Utils.THREAD_MAIN_LOOP_MILLISECONDS);
			}
		}
		catch (Exception ex) {
			Utils.Error("Unexpected error occur", ex);
		}
	}
}