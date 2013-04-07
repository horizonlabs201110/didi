package org.hellocar.openfire.plugin;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.jivesoftware.openfire.*;
import org.jivesoftware.openfire.container.*;
import org.jivesoftware.openfire.interceptor.*;
import org.jivesoftware.openfire.event.*;
import org.jivesoftware.openfire.session.*;
import org.jivesoftware.openfire.user.*;

import org.xmpp.packet.*;

public class PushForwardPlugin implements Plugin, PacketInterceptor, OfflineMessageListener, UserEventListener, IForwardAdapter, IOfflineMessageAccessor, IThreadPoolTaskListener {
	private XMPPServer server = null;
	private MessageRouter router = null;
	private InterceptorManager interceptorManager = null;
	private UserManager userManager = null;
	private OfflineMessageStore omStore = null;
	private IManager messageManager = null;
	private IManager pushManager = null;
    private IManager forwardManager = null;
    private boolean initialized = false;
	private Object initLock = new Object();
	
    public PushForwardPlugin() {
    	server = XMPPServer.getInstance();
    	router = server.getMessageRouter();
    	userManager = server.getUserManager();
    	interceptorManager = InterceptorManager.getInstance();
    	omStore = OfflineMessageStore.getInstance();
    	messageManager = MessageManager.createInstance();
    	pushManager = PushManager.createInstance(PushAdapter.createInstance(), this);
    	forwardManager = ForwardManager.createInstance(this);
    }

    public void initializePlugin(PluginManager manager, File pluginDirectory) {
    	if (!initialized) {
			synchronized(initLock) {
				if (!initialized) {
					try {
						init();
						initialized = true;
				        Utils.info("PushForwardPlugin loaded");
					}
			    	catch (Exception ex) {
			    		terminate();
			    		Utils.error(String.format("Fail to load PushForwardPlugin, %1$s", ex.getMessage()), ex);
			    	}
				}
	   		}   	
    	}
    }
    
    public void destroyPlugin() {
    	if (initialized) {
			synchronized(initLock) {
				if (initialized) {
			    	try {
			    		initialized = false;
			    		terminate();
			    		Utils.info("PushForwardPlugin destroyed");
			    	}
			    	catch (Exception ex) {
			    		Utils.error(String.format("PushForwardPlugin destroyed, unexpected error occurs, %1$s", ex.getMessage()), ex);
			    	}
				}
			}
    	}
    }
    
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
    	if (!processed && packet != null && packet instanceof Message) {
    		if (Configuration.switchPostmanMessage) {
    			String target = packet.getTo().getNode();
    			if (!userManager.isRegisteredUser(target) || target.equalsIgnoreCase(Configuration.userPostman)) {
        			Message message = (Message)packet;
    	        	try {
    	        		IMessageHandler handler = MessageHandler.getHandler(MessageType.POSTMAN);
    	        		if (!handler.validate(message)) {
    	        			Utils.debug(String.format("Invalid postman message detected, %1$s", message.toXML()));
    	        			return;
    	        		}
    	        		handler.process(message);
    	            	Utils.debug(String.format("Postman message processed, %1$s", message.toXML()));
    	        	}
    	        	catch (Exception ex) {
                		Utils.error(String.format("Fail to process postman message, %1$s, %2$s", ex.getMessage(), message.toXML()), ex);
    	            }
    	        }
    		}
    	}
    }
    
    public void messageStored(Message message) {
    	if (Configuration.switchOfflineMessage) {
    		if (!message.getTo().getNode().equalsIgnoreCase(Configuration.userPostman)) {
		    	try {
		    		IMessageHandler handler = MessageHandler.getHandler(MessageType.OFFLINE);
		    		if (!handler.validate(message)) {
		    			Utils.debug(String.format("Invalid offline message detected, %1$s", message.toXML()));
		    			return;
		    		}
		    		handler.process(message);
		    		wakeUpPushManager();
		    		Utils.debug(String.format("Offline message processed, %1$s", message.toXML()));
		    	} 
		    	catch (Exception ex) {
		    		Utils.error(String.format("Fail to process offline message, %1$s, %2$s", ex.getMessage(), message.toXML()), ex);
		    	}
    		}
    	}
    }
    
    public void messageBounced(Message message) {
    }
    
    public void userCreated(User user, Map<String, Object> params) {
    	try {
    		int count = DBMapper.prepareMessageForUser(user);
    		if (count > 0) { 
    			wakeUpForwardManager(); 
    		}
    		Utils.debug(String.format("Prepare message for user %1$s, total %2$d", user.getUsername(), count));
    	}
    	catch (Exception ex) {
    		Utils.error(String.format("Fail to prepare message for user %1$s, $2$s", user.getUsername(), ex.getMessage()), ex);
    	}
    }

    public void userDeleting(User user, Map<String, Object> params) {
    }

    public void userModified(User user, Map<String, Object> params) {
    }
    
    public void forward(Message message) {
    	if (message != null) {
    		router.route(message);
    	}
    }
    
    public int getMessageCount(String userName) {
    	Collection<OfflineMessage> om  = omStore.getMessages(userName, false);
    	if (om != null) {
    		return om.size();
    	}
    	else {
    		return 0;
    	}
    }
    
    private void init() {
    	Configuration.load();
    	messageManager.init();
    	pushManager.init();
        forwardManager.init();
    	interceptorManager.addInterceptor(this);
    	OfflineMessageStrategy.addListener(this);
        UserEventDispatcher.addListener(this);
    }
    
    private void terminate() {
    	UserEventDispatcher.removeListener(this);
    	OfflineMessageStrategy.removeListener(this);
    	interceptorManager.removeInterceptor(this);
    	forwardManager.terminate();
		pushManager.terminate();
		messageManager.terminate();
    }
    
    private void wakeUpForwardManager() {
    	Utils.executeTask(this, null);
    }
    
    public void wakeUpPushManager(){
    	pushManager.keepalive();
    }
    
    public void taskrun(Object state) {
    	try {
	    	Thread.sleep(Configuration.forwardDelayInSeconds * 1000);
	    	forwardManager.keepalive();
    	}
    	catch (Exception ex) {
    		Utils.error(String.format("Fail to wake up forward manager, %1$s", ex.getMessage()), ex);
    	}
    }
}