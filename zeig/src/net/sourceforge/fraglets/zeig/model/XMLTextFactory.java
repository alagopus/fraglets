/*
 * PlainTextFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 6, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import net.sourceforge.fraglets.codec.OTPHash;
import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 */
public class XMLTextFactory {
    private ConnectionFactory cf;
    
    public XMLTextFactory(ConnectionFactory cf) {
        this.cf = cf;
    }
    
    public int getXMLText(int value, int atts, int node[]) throws SQLException {
        int hc = OTPHash.chain(value, OTPHash.chain(atts, OTPHash.hash(node)));
        try {
            return findXMLText(value, atts, node, hc);
        } catch (NoSuchElementException ex) {
            return createXMLText(value, atts, node, hc);
        }
    }
    
    public int getValue(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select value from xt where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException
                    ("attribute with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    public int[] getNodes(int id) throws SQLException {
        if (id == 0) {
            return NodeBuffer.MT;
        }
        
        PreparedStatement ps = cf
            .prepareStatement("select node from xn where xt=? order by pos");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            NodeBuffer buffer = new NodeBuffer();
            while (rs.next()) {
                buffer.append(rs.getInt(1));
            }
            return buffer.toIntArray();
        } finally {
            rs.close();
        }
    }
    
    public int[] getAttributes(int id) throws SQLException {
        if (id == 0) {
            return NodeBuffer.MT;
        }
        
        PreparedStatement ps = cf
            .prepareStatement("select at from xa where xt=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            NodeBuffer buffer = new NodeBuffer();
            while (rs.next()) {
                buffer.append(rs.getInt(1));
            }
            return buffer.toIntArray();
        } finally {
            rs.close();
        }
    }
    
    private int findXMLText(int value, int atts, int node[], int hc) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select id from xt where hc=? and value=?");
        
        ps.setInt(1, hc);
        ps.setInt(2, value);
        
        int id;
        NodeBuffer buffer = null;
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                id = rs.getInt(1);
                if (rs.next()) {
                    buffer = new NodeBuffer().append(id);
                    while (rs.next()) {
                        buffer.append(rs.getInt(1)); 
                    }
                }
            } else {
                throw new NoSuchElementException("xml text");
            }
        } finally {
            rs.close();
        }
        
        if (buffer == null) {
            if (equals(getAttributes(id), node, 0, atts) &&
                equals(getNodes(id), node, atts, node.length - atts)) {
                return id;
            }
        } else {
            int[] ids = buffer.toIntArray();
            int scan = ids.length;
            while (--scan >= 0) {
                if (!equals(getAttributes(ids[scan]), node, 0, atts)) {
                    continue;
                }
                if (!equals(getNodes(ids[scan]), node, atts, node.length - atts)) {
                    continue;
                }
                return ids[scan];
            }
        }
        
        throw new NoSuchElementException("xml text");
    }
    
    public static boolean equals(int list1[], int list2[], int off, int len) {
        try {
            if (list1.length != len) {
                return false;
            } else {
                int scan = list1.length;
                while (--scan >= 0) {
                    if (list1[scan] != list2[off + scan]) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }
    
    private int createXMLText(int value, int atts, int node[], int hc) throws SQLException {
        if (value == 0) {
            throw new IllegalArgumentException("value undefined");
        }
        
        PreparedStatement ps;
        
        ps = cf.prepareStatement("insert into xt (hc,value) values (?,?)");
        ps.setInt(1, hc);
        ps.setInt(2, value);
        int id = cf.executeInsert(ps, 1);
        
        int i = 0;
        
        if (i < atts) {
            ps = cf.prepareStatement("insert into xa (xt,at) values (?,?)");
            for (; i < atts; i++) {
                ps.setInt(1, id);
                ps.setInt(2, node[i]);
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException("node insert updated != 1");
                }
            }
        }
        
        if (i < node.length) {
            ps = cf.prepareStatement("insert into xn (xt,pos,node) values (?,?,?)");
            for (;i < node.length; i++) {
                ps.setInt(1, id);
                ps.setInt(2, i);
                ps.setInt(3, node[i]);
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException("node insert updated != 1");
                }
            }
        }
        
        return id;
    }

}
