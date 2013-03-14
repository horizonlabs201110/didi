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

public class PushForwardPlugin implements Plugin, PacketInterceptor, OfflineMessageListener, UserEventListener, IPushAdapter, IForwardAdapter {
	private PluginManager pluginManager = null;
	private InterceptorManager interceptorManager = null;
    private IPushForwardManager pushForwardManager = null;
    private boolean initialized = false;
	private Object initLock = new Object();
	
    public PushForwardPlugin() {
    	interceptorManager = InterceptorManager.getInstance();
    	pushForwardManager = PushManager.CreateInstance(this, this);
    	Utils.Debug("PushForwardPlugin created");
    }

    public void initializePlugin(PluginManager manager, File pluginDirectory) {
    	if (!initialized) {
			synchronized(initLock) {
				if (!initialized) {
					try {
						pluginManager = manager;
						Configuration.Load();
						init();
						
				        initialized = true;
				        Utils.Info("PushForwardPlugin loaded");
					}
			    	catch (Exception ex) {
			    		terminate();
			    		Utils.Error("Fail to load PushForwardPlugin", ex);
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
			    		pluginManager = null;
			    		terminate();
			    		Utils.Info("PushForwardPlugin destroyed");
			    	}
			    	catch (Exception ex) {
			    		Utils.Error("Failed to destroy PushForwardPlugin", ex);
			    	}
				}
			}
    	}
    }
    
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
    	try {
	    	if (packet instanceof Message) {
	            Message message = (Message)packet;
	            if (message.getTo().getNode().equalsIgnoreCase(Configuration.PostmanName)) {
	            	DBMapper.AddPostmanMessage(message);
	            	
	            	Utils.Debug(String.format("Postman message intercepted, %1$s", message.toString()));
	            }
	        }
    	} catch (Exception ex) {
    		Utils.Error("Fail to intercept postman message", ex);
    	}
    }
    
    public void messageStored(Message message) {
    	try {
    		DBMapper.AddOfflineMessage(message);
    		Utils.Debug(String.format("Offline message intercepted, %1$s", message.toString()));
    	} catch (Exception ex) {
    		Utils.Error("Fail to intercept offline message", ex);
    	}
    }
    
    public void messageBounced(Message message) {
    }
    
    public void userCreated(User user, Map<String, Object> params) {
    	try {
    		int count = DBMapper.CheckMessageForUser(user);
    		Utils.Debug(String.format("Prepare message for user %1$s, count %2$d", user.getUsername(), count));
    	}
    	catch (Exception ex) {
    		Utils.Error(String.format("Failed to prepare message for user %1$s", user.getUsername()), ex);
    	}
    }

    public void userDeleting(User user, Map<String, Object> params) {
    }

    public void userModified(User user, Map<String, Object> params) {
    }
    
    public void push(Message message) {
    }
    
    public void forward(Message message) {
    	
    }
    
    private void init() {	
        OfflineMessageStrategy.addListener(this);
        interceptorManager.addInterceptor(this);
        pushForwardManager.Init();
    }
    
    private void terminate() {
    	interceptorManager.removeInterceptor(this);
		OfflineMessageStrategy.removeListener(this);
		pushForwardManager.Terminate();
    }
}