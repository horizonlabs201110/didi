package org.hellocar.openfire.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.openfire.*;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.cluster.ClusterManager;
import org.jivesoftware.openfire.plugin.cluster.RulesUpdatedEvent;
import org.jivesoftware.util.cache.CacheFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xmpp.packet.*;

public class PushForwardDBMapper {
	private static final Logger Log = LoggerFactory.getLogger(PushForwardDBMapper.class);
	
    private static final String SQL_INSERT_MESSAGE =
            "INSERT INTO ofHCMessages (type,toUserName,fromUserName,status,stanza,lastModified) VALUES(?,?,?,?,?,?)";

    public void AddOfflineMessage(Message msg) {
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
    public void AddPostmanMessage(Message msg) {
    	return;
    }
}