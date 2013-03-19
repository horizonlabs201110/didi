package org.hellocar.openfire.plugin;

import java.sql.*;
import java.sql.Connection;
import java.util.*;

import org.jivesoftware.openfire.*;
import org.jivesoftware.database.*;
import org.jivesoftware.openfire.user.*;

import org.slf4j.*;

public class DBMapper {
	private static final String SQL_INSERT_MESSAGE =
            "INSERT INTO ofHCMessages (type,toUserName,fromUserName,status,stanza,lastModified) VALUES(?,?,?,?,?,?)";

	public static void cleanMessage(long retentionInSeconds) {
    }
    
	public static void addMessage(MessageEx msg) {
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
    
    public static int checkMessageForUser(User usr) {
    	return 0;
    }
    
    public static void updateMessageStatus(long id, MessageStatus status, String statusMessage) {    
    }
    
    public static ArrayList<MessageEx> getAllMessages(MessageType type, MessageStatus status) {
    	return null;
    }
}