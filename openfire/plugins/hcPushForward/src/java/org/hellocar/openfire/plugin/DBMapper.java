package org.hellocar.openfire.plugin;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import org.jivesoftware.openfire.*;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.cluster.ClusterManager;
import org.jivesoftware.openfire.plugin.cluster.RulesUpdatedEvent;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.util.cache.CacheFactory;

import org.slf4j.*;

import org.xmpp.packet.*;

public class DBMapper {
	private static final Logger Log = LoggerFactory.getLogger(PushForwardDBMapper.class);
	
    private static final String SQL_INSERT_MESSAGE =
            "INSERT INTO ofHCMessages (type,toUserName,fromUserName,status,stanza,lastModified) VALUES(?,?,?,?,?,?)";

    public static void AddOfflineMessage(Message msg) {
    	Connection con = null;
        PreparedStatement pstmt = null;
        long now = new Date().getTime();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_INSERT_MESSAGE);
            pstmt.setInt(1, 1);
            pstmt.setString(2, msg.getTo().getNode());
            pstmt.setString(3, msg.getFrom().getNode());
            pstmt.setInt(4, 2);
            String test = msg.toXML();
            test = msg.toString();
            pstmt.setString(5, msg.toXML());
            
            test = String.valueOf(now);
            pstmt.setString(6, String.valueOf(now));
            
            pstmt.execute();

        } catch (SQLException ex) {
            Log.error(ex.getMessage(), ex);
            //throw ex;
        }
        catch (Exception ex) {
            Log.error(ex.getMessage(), ex);
            throw ex;
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    public static void AddPostmanMessage(Message msg) {
    	return;
    }
    
    public static int CheckMessageForUser(User usr) {
    	return 0;
    }
    
    public static ArrayList<MessageEx> getAllOfflineMessages() {
    	return null;
    }
    
    public static ArrayList<MessageEx> getAllForwardMessages() {
    	return null;
    }
    
    public static void updateMessages(ArrayList<MessageEx> messages) {
    }
    
    public static void updateMessage(MessageEx message) {
    
    }
}