package org.hellocar.openfire.plugin;

import java.sql.*;
import java.util.*;

import org.jivesoftware.database.*;
import org.jivesoftware.openfire.user.*;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import org.xmpp.packet.Message;

public class DBMapper {
	private static final String SQL_CLEAN_MESSAGE =
			"DELETE FROM `ofhcmessage` WHERE (`status` = ? OR `status` = ?) AND `lastModified` < ?";
	private static final String SQL_INSERT_MESSAGE =
			"INSERT INTO `ofhcmessage` (`type`,`to`,`from`,`status`,`statusMessage`,`stanza`,`lastModified`) VALUES (?,?,?,?,?,?,?)";
    private static final String SQL_PREPARE_MESSAGE_FOR_USER =
    		"UPDATE `ofhcmessage` SET `status` = ?, `lastModified` = ? WHERE `type` = ? AND `to` = ? AND `status` = ?";
    private static final String SQL_UPDATE_MESSAGE_STATUS =
    		"UPDATE `ofhcmessage` SET `status` = ?, `statusMessage` = ?, `lastModified` = ? WHERE `id` = ?";
    private static final String SQL_UPDATE_OFFLINE_MESSAGE_STATUS =
    		"UPDATE `ofhcmessage` SET `status` = ?, `statusMessage` = ?, `lastModified` = ? WHERE `type` = ? AND `to` = ? AND `status` = ?";
    private static final String SQL_GET_ALL_MESSAGES =
    		"SELECT `id`,`type`,`to`,`from`,`status`,`statusMessage`,`stanza`,`lastModified` FROM `ofhcmessage` WHERE `type` = ? AND `status` = ?";
    private static final String SQL_GET_USEREXTRA =
    		"SELECT `iospush`, `iostoken` FROM `ofhcuserextra` WHERE `username` = ?";
    private static final String SQL_INSERT_USEREXTRA = 
    		"INSERT INTO `ofhcuserextra` (`username`, `iospush`, `iostoken`) values(?,?,?)";
    private static final String SQL_UPDATE_USEREXTRA = 
    		"UPDATE `ofhcuserextra` SET `iospush` = ?, `iostoken` = ? WHERE `username` = ?";
    private static final String SQL_UPDATE_USERPUSH = 
    		"UPDATE `ofhcuserextra` SET `iospush` = ? WHERE `username` = ?";
    private static final String SQL_UPDATE_USERTOKEN = 
    		"UPDATE `ofhcuserextra` SET `iostoken` = ? WHERE `username` = ?";
    
    public static void cleanMessage(long retentionInSeconds) throws SQLException {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
        	con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_CLEAN_MESSAGE);
            
            pstmt.setInt(1, MessageStatus.FAIL.toInt());
            pstmt.setInt(2, MessageStatus.SUCCEED.toInt());
            pstmt.setLong(3, Utils.getNow() -  retentionInSeconds * 1000);
            
            pstmt.execute();
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    
	public static void addMessage(MessageEx msg) throws SQLException {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_INSERT_MESSAGE);
            
            pstmt.setInt(1, msg.type.toInt());
            pstmt.setString(2, msg.message.getTo().getNode());
            pstmt.setString(3,  msg.message.getFrom().getNode());
            pstmt.setInt(4,  msg.status.toInt());
            pstmt.setString(5, msg.statusMessage);
            pstmt.setString(6, msg.message.toXML());
            pstmt.setLong(7,  Utils.getNow());
            
            pstmt.execute();
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
    
    public static int prepareMessageForUser(User usr) throws SQLException {
    	Connection con = null;
        PreparedStatement pstmt = null;
        try {
        	con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_PREPARE_MESSAGE_FOR_USER);
            
            pstmt.setInt(1, MessageStatus.READY.toInt());
            pstmt.setLong(2, Utils.getNow());
            pstmt.setInt(3, MessageType.POSTMAN.toInt());
            pstmt.setString(4, usr.getUsername());
            pstmt.setInt(5, MessageStatus.QUEUE.toInt());
            
            return pstmt.executeUpdate();
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    
    public static void updateMessageStatus(long id, MessageStatus status, String statusMessage) throws SQLException  {
    	Connection con = null;
        PreparedStatement pstmt = null;
        try {
        	con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_UPDATE_MESSAGE_STATUS);
            
            pstmt.setInt(1, status.toInt());
            pstmt.setString(2, statusMessage);
            pstmt.setLong(3, Utils.getNow());
            pstmt.setLong(4, id);
            
            pstmt.execute();
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    
    public static void updateOfflineMessageStatus(String userName, MessageStatus status, String statusMessage) throws SQLException  {
    	Connection con = null;
        PreparedStatement pstmt = null;
        try {
        	con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_UPDATE_OFFLINE_MESSAGE_STATUS);
            
            pstmt.setInt(1, status.toInt());
            pstmt.setString(2, statusMessage);
            pstmt.setLong(3, Utils.getNow());
            pstmt.setInt(3, MessageType.OFFLINE.toInt());
            pstmt.setString(4, userName);
            pstmt.setInt(5, MessageStatus.READY.toInt());
            
            pstmt.execute();
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    
    public static ArrayList<MessageEx> getAllMessages(MessageType type, MessageStatus status) throws SQLException, DocumentException {
    	Connection con = null;
        PreparedStatement pstmt = null;
        ArrayList<MessageEx> mxs = new ArrayList<MessageEx>();
        try {
        	con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_GET_ALL_MESSAGES);
            
            pstmt.setInt(1, type.toInt());
            pstmt.setInt(2, status.toInt());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	MessageEx mx = new MessageEx();
            	mx.id = rs.getLong(1);
            	mx.type = MessageType.parse(rs.getInt(2));
            	mx.status = MessageStatus.parse(rs.getInt(5));
            	mx.statusMessage = rs.getString(6);
            	mx.message = new Message(DocumentHelper.parseText(rs.getString(7)).getRootElement());
            	mx.lastModified = rs.getLong(8);
            	
            	mxs.add(mx);
            }
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return mxs;
    }
    
    public static void addUserExtra(UserExtra extra) throws SQLException { 
    	Connection con = null;
        PreparedStatement pstmt = null;
        try {
        	con = DbConnectionManager.getConnection();
        	pstmt = con.prepareStatement(SQL_INSERT_USEREXTRA);
            pstmt.setString(1, extra.userName);
            pstmt.setBoolean(2,  extra.iosPush);
            pstmt.setString(3, extra.iosToken);
            pstmt.execute();
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    
    public static void updateUserExtra(UserExtra extra) throws SQLException {
    	if (null == getUserExtra(extra.userName)) {
    		addUserExtra(extra);
    	}
    	else {
    		Connection con = null;
            PreparedStatement pstmt = null;
            try {
            	con = DbConnectionManager.getConnection();
           		pstmt = con.prepareStatement(SQL_UPDATE_USEREXTRA);
                pstmt.setBoolean(1,  extra.iosPush);
                pstmt.setString(2, extra.iosToken);
                pstmt.setString(3, extra.userName);
                pstmt.execute();
            }
            finally {
                DbConnectionManager.closeConnection(pstmt, con);
            }
    	}
    }
    
    public static void updateUserPush(UserExtra extra) throws SQLException {
    	if (null == getUserExtra(extra.userName)) {
    		addUserExtra(extra);
    	}
    	else {
    		Connection con = null;
            PreparedStatement pstmt = null;
            try {
            	con = DbConnectionManager.getConnection();
           		pstmt = con.prepareStatement(SQL_UPDATE_USERPUSH);
                pstmt.setBoolean(1,  extra.iosPush);
                pstmt.setString(2, extra.userName);
                pstmt.execute();
            }
            finally {
                DbConnectionManager.closeConnection(pstmt, con);
            }
    	}
    }
    
    public static void updateUserToken(UserExtra extra) throws SQLException {
    	if (null == getUserExtra(extra.userName)) {
    		addUserExtra(extra);
    	}
    	else {
    		Connection con = null;
            PreparedStatement pstmt = null;
            try {
            	con = DbConnectionManager.getConnection();
           		pstmt = con.prepareStatement(SQL_UPDATE_USERTOKEN);
                pstmt.setString(1, extra.iosToken);
                pstmt.setString(2, extra.userName);
                pstmt.execute();
            }
            finally {
                DbConnectionManager.closeConnection(pstmt, con);
            }
    	}
    }
    
    public static UserExtra getUserExtra(String user) throws SQLException {
    	Connection con = null;
        PreparedStatement pstmt = null;
        UserExtra ue = null;
        try {
        	con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_GET_USEREXTRA);
            pstmt.setString(1,  user);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	ue = new UserExtra(user, rs.getBoolean(1), rs.getString(2));
            	break;
            }
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return ue;
        
    }
}