package org.hellocar.openfire.plugin;

import java.io.File;
import java.util.Map;
import org.jivesoftware.openfire.*;
import org.jivesoftware.openfire.container.*;
import org.jivesoftware.openfire.interceptor.*;
import org.jivesoftware.openfire.event.*;
import org.jivesoftware.openfire.session.*;
import org.jivesoftware.openfire.user.*;
import org.xmpp.packet.*;

public class PushForwardPlugin implements Plugin, PacketInterceptor, OfflineMessageListener, UserEventListener, IForwardAdapter {
	private XMPPServer server = null;
	private MessageRouter router = null;
	private InterceptorManager interceptorManager = null;
	private IManager messageManager = null;
	private IManager pushManager = null;
    private IManager forwardManager = null;
    private boolean initialized = false;
	private Object initLock = new Object();
	
    public PushForwardPlugin() {
    	server = XMPPServer.getInstance();
    	router = server.getMessageRouter();
    	interceptorManager = InterceptorManager.getInstance();
    	messageManager = MessageManager.createInstance();
    	pushManager = PushManager.createInstance(PushAdapter.createInstance());
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
			    		Utils.error("Fail to load PushForwardPlugin", ex);
			    		throw ex;
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
			    		Utils.error("Failed to destroy PushForwardPlugin", ex);
			    	}
				}
			}
    	}
    }
    
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
    	if (packet != null && packet instanceof Message) {
    		Message message = (Message)packet;
	        if (message.getTo().getNode().equalsIgnoreCase(Configuration.postmanName)) {
	        	try {
	        		DBMapper.addMessage(Utils.CreateMessage(message, MessageType.POSTMAN, MessageStatus.QUEUE));
	            	Utils.debug(String.format("Postman message received, %1$s", message.toString()));
	        	}
	        	catch (Exception ex) {
            		Utils.error("Fail to receive postman message", ex);
	            }
	        }
    	}
    }
    
    public void messageStored(Message message) {
    	try {
    		DBMapper.addMessage(Utils.CreateMessage(message, MessageType.OFFLINE, MessageStatus.READY));
    		pushManager.keepalive();
    		Utils.debug(String.format("Offline message received, %1$s", message.toString()));
    	} catch (Exception ex) {
    		Utils.error("Fail to receive offline message", ex);
    	}
    }
    
    public void messageBounced(Message message) {
    }
    
    public void userCreated(User user, Map<String, Object> params) {
    	try {
    		int count = DBMapper.prepareMessageForUser(user);
    		if (count > 0) { forwardManager.keepalive(); }
    		Utils.debug(String.format("Check message for user %1$s, count %2$d", user.getUsername(), count));
    	}
    	catch (Exception ex) {
    		Utils.error(String.format("Fail to check message for user %1$s", user.getUsername()), ex);
    	}
    }

    public void userDeleting(User user, Map<String, Object> params) {
    }

    public void userModified(User user, Map<String, Object> params) {
    }
    
    public void forward(Message message) {
    	router.route(message);
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
}