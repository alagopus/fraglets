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

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 */
public class AttributeFactory {
    private ConnectionFactory cf;
    
    public AttributeFactory(ConnectionFactory cf) {
        this.cf = cf;
    }
    
    public int getAttribute(int nm, int value) throws SQLException {
        try {
            return findAttribute(nm, value);
        } catch (NoSuchElementException ex) {
            return createAttribute(nm, value);
        }
    }
    
    public int getName(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select nm from at where id=?");
        
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
    
    public int getValue(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select value from at where id=?");
        
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
    
    private int findAttribute(int nm, int value) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select id from at where nm=? and value=?");
        
        ps.setInt(1, nm);
        ps.setInt(2, value);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException("attribute");
            }
        } finally {
            rs.close();
        }
    }
    
    private int createAttribute(int nm, int value) throws SQLException {
        if (nm == 0) {
            throw new IllegalArgumentException("name undefined");
        } else if (value == 0) {
            throw new IllegalArgumentException("value undefined");
        }
        
        PreparedStatement ps = cf
            .prepareStatement("insert into at (nm,value) values (?,?)");
        
        ps.setInt(1, nm);
        ps.setInt(2, value);
        return cf.executeInsert(ps, 1);
    }

}
