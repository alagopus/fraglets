/*
 * NameFactory -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.5 $
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
