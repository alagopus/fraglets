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
public class PlainTextFactory {
    private ConnectionFactory cf;
    
    private String cacheKey[];
    private int cacheValue[];
    
    public PlainTextFactory(ConnectionFactory cf) {
        this.cf = cf;
        cacheKey = new String[256];
        cacheValue = new int[256];
    }
    
    public int getPlainText(String text) throws SQLException {
        int hc = OTPHash.hash(text);
        int index = hc & 0xff;
        String ck = cacheKey[index];
        if (ck != null && ck.equals(text)) {
            return cacheValue[index]; 
        } else {
            return cacheValue[index] = getPlainText(text, hc);
        }
    }
    
    protected int getPlainText(String text, int hc) throws SQLException {
        try {
            return findPlainText(text, hc);
        } catch (NoSuchElementException ex) {
            return createPlainText(text, hc);
        }
    }
    
    public String getPlainText(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select value from pt where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new NoSuchElementException
                    ("plain text with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    private int findPlainText(String text, int hc) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select id,value from pt where hc=? and value=?");
        
        ps.setInt(1, hc);
        ps.setString(2, text);
        
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                int id = rs.getInt(1);
                String value = rs.getString(2);
                if (value.equals(text)) {
                    return id;
                }
            }
            throw new NoSuchElementException(text);
        } finally {
            rs.close();
        }
    }
    
    private int createPlainText(String text, int hc) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("insert into pt (hc,value) values (?,?)");
        
        ps.setLong(1, hc);
        ps.setString(2, text);
        return cf.executeInsert(ps, 1);
    }
}
