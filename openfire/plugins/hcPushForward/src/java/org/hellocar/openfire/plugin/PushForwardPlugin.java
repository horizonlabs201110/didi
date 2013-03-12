package org.hellocar.openfire.plugin;

import java.io.File;
import java.util.Map;
import org.jivesoftware.openfire.*;
import org.jivesoftware.openfire.container.*;
import org.jivesoftware.openfire.interceptor.*;
import org.jivesoftware.openfire.event.*;
import org.jivesoftware.openfire.session.*;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.util.*;
import org.slf4j.*;
import org.xmpp.packet.*;

public class PushForwardPlugin implements Plugin, PacketInterceptor, OfflineMessageListener, UserEventListener {
	private InterceptorManager interceptorManager = null;
    private IPushForwardManager pushForwardManager = null;
        
    public PushForwardPlugin() {
    	interceptorManager = InterceptorManager.getInstance();
    	pushForwardManager = PushForwardManager.CreateInstance();
    	
    	Utils.Debug("PushForwardPlugin created");
    }

    public void initializePlugin(PluginManager manager, File pluginDirectory) {
    	try {
    		Configuration.Load();
    		OfflineMessageStrategy.addListener(this);
	        interceptorManager.addInterceptor(this);
	        pushForwardManager.Init();
	        
	        Utils.Info("PushForwardPlugin loaded");
    	}
    	catch (Exception ex) {
    		Utils.Error("Fail to load PushForwardPlugin", ex);
    		throw ex;
    	}
    }

    public void destroyPlugin() {
    	try {
    		OfflineMessageStrategy.removeListener(this);
    		interceptorManager.removeInterceptor(this);
    		pushForwardManager.Terminate();
    		
    		Utils.Info("PushForwardPlugin destroyed");
    	}
    	catch (Exception ex) {
    		Utils.Error("Failed to destroy PushForwardPlugin", ex);
    		throw ex;
    	}
    }
    
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
    	try {
	    	if (packet instanceof Message) {
	            Message message = (Message)packet;
	            if (message.getTo().getNode().equalsIgnoreCase(Configuration.PostmanName)) {
	            	DBMapper.AddPostmanMessage(message);
	            	Utils.Debug("New postman message arrived");
	            }
	        }
    	} catch (Exception ex) {
    		Utils.Error("Fail to intercept postman message", ex);
    	}
    	return;
    }
    
    public void messageStored(Message message) {
    	try {
    		DBMapper.AddOfflineMessage(message);
    		Utils.Debug("New offline message arrived");
    	} catch (Exception ex) {
    		Utils.Error("Fail to process offline message", ex);
    	}
    	return;
    }
    
    public void messageBounced(Message message) {
    	return;
    }
    
    public void userCreated(User user, Map<String, Object> params) {
    	try {
    		int count = DBMapper.CheckMessageForUser(user);
    		Utils.Debug(String.format("Checked message for user %1$s, count %2$d", user.getUsername(), count));
    	}
    	catch (Exception ex) {
    		Utils.Error(String.format("Failed to check message for user %1$s", user.getUsername()), ex);
    	}
    }

    public void userDeleting(User user, Map<String, Object> params) {
    	return;
    }

    public void userModified(User user, Map<String, Object> params) {
    	return;
    }
}