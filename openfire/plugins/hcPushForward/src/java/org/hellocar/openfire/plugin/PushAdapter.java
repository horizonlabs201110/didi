package org.hellocar.openfire.plugin;

import org.json.JSONException;

import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.transmission.PushQueue;
import javapns.Push;

public class PushAdapter implements IPushAdapter {
	public static IPushAdapter createInstance() {
		return new PushAdapter();
	}
	
	private IPushAdapter iosPusher = null;
	
	public PushAdapter() {
		iosPusher = new IOSPushAdapter();
	}
	
	public void push(MessageEx msg, String token) throws Exception {
		iosPusher.push(msg, token);
	}
}

class IOSPushAdapter implements IPushAdapter {
	private PushQueue queue = null;
	private Object queueLock = new Object();
	
	public IOSPushAdapter() {
	}
	
	public void push(MessageEx msg, String token) throws Exception {
		try {
			GetPushQueue().add(GeneratePayload(msg), token);
		}
		catch (Exception ex) {
			throw new Exception(String.format("Fail to push notification to ios, %1$s", ex.getMessage()), ex);
		}
	}
	
	private PushQueue GetPushQueue() throws KeystoreException {
		if (queue == null) {
			synchronized(queueLock) {
				if (queue == null) {
					queue = Push.queue(Configuration.iosPushKeyStore, Configuration.iosPushPassword, Configuration.iosPushProduction, Configuration.iosPushThread);
					queue.start();
				}
			}
		}
		return queue;
	}
	
	private PushNotificationPayload GeneratePayload(MessageEx msg) throws JSONException{
		PushNotificationPayload payload = PushNotificationPayload.complex();
        return payload;
	}
}