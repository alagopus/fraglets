/*
 * NamespaceFactory -
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
public class NamespaceFactory {
    private ConnectionFactory connectionFactory;
    
    public NamespaceFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    public int getNamespace(int uri, int value) throws SQLException {
        try {
            return findNamespace(uri, value);
        } catch (NoSuchElementException ex) {
            return createNamespace(uri, value);
        }
    }
    
    public int getNamespace(int uri) throws SQLException {
        return findNamespace(uri, 0);
    }
    
    public int getUri(int id) throws SQLException {
        PreparedStatement ps = connectionFactory
            .prepareStatement("select uri from ns where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException
                    ("namespace with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    public int getValue(int id) throws SQLException {
        PreparedStatement ps = connectionFactory
            .prepareStatement("select v from ns where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException
                    ("namespace with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    private int findNamespace(int uri, int value) throws SQLException {
        PreparedStatement ps;
        
        if (uri != 0) {
            ps = connectionFactory
                .prepareStatement("select id from ns where uri=?");
            ps.setInt(1, uri);
        } else if (value != 0) {
            ps = connectionFactory
                .prepareStatement("select id from ns where v=?");
            ps.setInt(1, value);
        } else {
            throw new IllegalArgumentException("neither uri nor value");
        }
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException("namespace");
            }
        } finally {
            rs.close();
        }
    }
    
    private int createNamespace(int uri, int value) throws SQLException {
        if (uri == 0) {
            throw new IllegalArgumentException("uri undefined");
        } else if (value == 0) {
            throw new IllegalArgumentException("value undefined");
        }
        
        PreparedStatement ps = connectionFactory
            .prepareStatement("insert into ns (uri,v) values (?,?)");
        
        ps.setInt(1, uri);
        ps.setInt(2, value);
        return connectionFactory.executeInsert(ps, 1);
    }

}
