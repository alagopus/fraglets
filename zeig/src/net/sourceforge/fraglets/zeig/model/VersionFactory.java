/*
 * VersionFactory -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 6, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.3 $
 */
public class VersionFactory {
    private ConnectionFactory cf;
    
    public VersionFactory(ConnectionFactory cf) {
        this.cf = cf;
    }
    
    private static VersionFactory instance;
    
    public int createVersion(int value, int comment) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("insert into ve (v,co) values (?,?)");
        
        ps.setInt(1, value);
        ps.setInt(2, comment);
        return cf.executeInsert(ps, 1);
    }
    
    public void addVersion(int id, int value, int comment) throws SQLException {
        int next = getGeneration(id) + 1;
        PreparedStatement ps =
            cf.prepareStatement("insert into ve (id,g,v,co) values (?,?,?,?)");

        ps.setInt(1, id);
        ps.setInt(2, next);
        ps.setInt(3, value);
        ps.setInt(4, comment);
        if (ps.executeUpdate() != 1) {
            throw new RuntimeException("version insert updated != 1");
        }
    }
    
    public int[] getVersions(int value) throws SQLException {
        PreparedStatement ps =
            cf.prepareStatement("select count(*) from ve where v=? and g=0");
        
        ps.setInt(1, value);
        
        int count;
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                count = rs.getInt(1);
            } else {
                throw new NoSuchElementException();
            }
        } finally {
            rs.close();
        }
        
        int result[] = new int[count];
        ps = cf.prepareStatement("select id from ve where v=? and g=0 order by ts desc");
        
        ps.setInt(1, value);
        
        rs = ps.executeQuery();
        try {
            while (rs.next()) {
                result[--count] = rs.getInt(1);
            }
            if (count != 0) {
                throw new IllegalStateException();
            }
        } finally {
            rs.close();
        }
        
        return result;
    }
    
    public int getValue(int id) throws SQLException {
        PreparedStatement ps =
            cf.prepareStatement("select v from ve where id=? order by g desc limit 1");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException();
            }
        } finally {
            rs.close();
        }
    }
    
    public String getInfo(int id, PlainTextFactory pt) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        PreparedStatement ps =
            cf.prepareStatement("select g,co,ts from ve where id=? order by g");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                int g = rs.getInt(1);
                String co = pt.getPlainText(rs.getInt(2));
                Timestamp ts = rs.getTimestamp(3);
                buffer.append("v."+g+" "+ts+" \""+co+"\"\n");
            }
        } finally {
            rs.close();
        }
        return buffer.toString();
    }
    
    protected int getGeneration(int id) throws SQLException {
        PreparedStatement ps =
            cf.prepareStatement("select max(g) from ve where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException();
            }
        } finally {
            rs.close();
        }
    }
    
}
