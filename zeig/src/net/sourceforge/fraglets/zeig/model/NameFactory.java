/*
 * NameFactory -
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
 * @version $Revision: 1.4 $
 */
public class NameFactory {
    private ConnectionFactory cf;
    
    public NameFactory(ConnectionFactory cf) {
        this.cf = cf;
    }
    
    public int getName(int ns, int value) throws SQLException {
        try {
            return findName(ns, value);
        } catch (NoSuchElementException ex) {
            return createName(ns, value);
        }
    }
    
    public int getNamespace(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select ns from nm where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException
                    ("name with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    public int getValue(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select v from nm where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException
                    ("name with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    private int findName(int ns, int value) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select id from nm where ns=? and v=?");
        
        ps.setInt(1, ns);
        ps.setInt(2, value);
        
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
    
    private int createName(int ns, int value) throws SQLException {
        if (ns == 0) {
            throw new IllegalArgumentException("namespace undefined");
        } else if (value == 0) {
            throw new IllegalArgumentException("value undefined");
        }
        
        PreparedStatement ps = cf
            .prepareStatement("insert into nm (ns,v) values (?,?)");
        
        ps.setInt(1, ns);
        ps.setInt(2, value);
        return cf.executeInsert(ps, 1);
    }

}
