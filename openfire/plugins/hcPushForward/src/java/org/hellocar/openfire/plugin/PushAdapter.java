package org.hellocar.openfire.plugin;

import org.json.JSONException;
import org.xmpp.packet.Message;

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
	
	public void push(Message om, int sn, String token) throws Exception {
		iosPusher.push(om, sn, token);
	}
}

class IOSPushAdapter implements IPushAdapter {
	private PushQueue queue = null;
	private Object queueLock = new Object();
	
	public IOSPushAdapter() {
	}
	
	public void push(Message om, int sn, String token) throws Exception {
		if (Configuration.iosPushEnabled) {
			getPushQueue().add(generatePayload(om, sn), token);
		}
	}
	
	private PushQueue getPushQueue() throws KeystoreException {
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
	
	private PushNotificationPayload generatePayload(Message om, int sn) throws JSONException{
		PushNotificationPayload payload = PushNotificationPayload.complex();
		
		String alert = om.getBody();
		if (alert.length() > Configuration.iosPushAlertMaxLen) {
			alert = alert.substring(0, Configuration.iosPushAlertMaxLen - 1);
		}
		payload.addAlert(String.format("%1$s:%2$s", om.getFrom().getNode(), alert));
		payload.addSound("default");
		payload.addBadge(sn);
		payload.addCustomDictionary("from", om.getFrom().toString());
		
        return payload;
	}
}