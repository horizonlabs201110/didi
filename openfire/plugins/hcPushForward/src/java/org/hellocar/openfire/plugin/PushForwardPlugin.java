package org.hellocar.openfire.plugin;

import java.io.File;

import org.jivesoftware.openfire.*;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.plugin.rules.Rule;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.JiveGlobals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xmpp.packet.*;

public class PushForwardPlugin implements Plugin, PacketInterceptor, OfflineMessageListener {
	
	private static final Logger tracer = LoggerFactory.getLogger(PushForwardPlugin.class);
	private static PluginManager pluginManager;
    private InterceptorManager interceptorManager;
    private OfflineMessageStrategy offlineMssageStrategy;
    private String pmName;
    private PushForwardDBMapper dbMapper;
    
    public PushForwardPlugin() {
    	interceptorManager = InterceptorManager.getInstance();
    	offlineMssageStrategy = XMPPServer.getInstance().getOfflineMessageStrategy();
    	pmName = JiveGlobals.getProperty("plugin.hcPushForward.postman", "postman");
    	dbMapper = new PushForwardDBMapper();
    }

    public void initializePlugin(PluginManager manager, File pluginDirectory) {
    	tracer.info("HelloCar Push Forward Plugin loaded...");
        pluginManager = manager;    
        interceptorManager.addInterceptor(this);
        offlineMssageStrategy.addListener(this);
    }

    public void destroyPlugin() {
    	tracer.info("HelloCar Push Forward Plugin destroyed ...");
    	interceptorManager.removeInterceptor(this);
    	offlineMssageStrategy.removeListener(this);
    }
    
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
    	try
    	{
	    	if (packet instanceof Message) {
	            Message message = (Message)packet;
	            String toNode = message.getTo().getNode();
	            if (toNode.equalsIgnoreCase(this.pmName))
	            {
	            	dbMapper.AddPostmanMessage(message);
	            }
	        }
    	} catch (Exception ex) {
    		tracer.error(ex.getMessage(), ex);
    	}
    	return;
    }
    
    public void messageBounced(Message message) {
    	return;
    }
    
    public void messageStored(Message message) {
    	try
    	{
    		dbMapper.AddOfflineMessage(message);
    	} catch (Exception ex) {
    		tracer.error(ex.getMessage(), ex);
    	}
    	return;
    }
}       
