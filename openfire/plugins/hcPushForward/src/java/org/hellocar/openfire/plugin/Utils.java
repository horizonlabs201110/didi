 package org.hellocar.openfire.plugin;

import java.util.*;

import org.dom4j.Element;
import org.slf4j.*;
import org.xmpp.packet.*;

public class Utils {
	public static final String PLUGIN_NAME = "hcPushForwardPlugin";
	public static final long THREAD_PUSH_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_FORWARD_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_MESSAGE_JOIN_MILLISECONDS = 3000;
	
	private static Logger tracer = LoggerFactory.getLogger(PLUGIN_NAME);
	
	public static long getNow() {
		return (new Date()).getTime();
	}
	
	public static MessageEx CreateMessage(Message message, MessageType type, MessageStatus status) {
		if (message == null) {
			throw new IllegalArgumentException("message");
		}
		Message m = message.createCopy();
		
		if (type == MessageType.POSTMAN) {
			try {
				Element fe = m.getElement().element(Configuration.forwardElementName);
				m.setTo(new JID(fe.getTextTrim()));
				fe.detach();
				Utils.debug("Postman message revised");
			}
			catch (Exception ex){
				Utils.error("Fail to revise postman message", ex);
				throw ex;
			}
		}
		
		MessageEx mx = new MessageEx();
		mx.message = m;
		mx.type = type;
		mx.status = status;
		mx.statusMessage = "";
		
		return mx;
	}
	
	public static void debug(String msg) {
		tracer.debug(msg);
	}
	
	public static void info(String msg) {
		tracer.info(msg);
	}
	
	public static void error(String msg) {
		tracer.error(msg);
	}
	
	public static void error(String msg, Throwable e) {
		tracer.error(msg, e);
	}
}
