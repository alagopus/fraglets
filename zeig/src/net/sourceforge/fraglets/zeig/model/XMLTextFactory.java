/*
 * XMLTextFactory -
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

import net.sourceforge.fraglets.codec.OTPHash;
import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.5 $
 */
public class XMLTextFactory {
    private ConnectionFactory cf;
    
    public XMLTextFactory(ConnectionFactory cf) {
        this.cf = cf;
    }
    
    public int getXMLText(int nm, int node[]) throws SQLException {
        int hc = OTPHash.chain(nm, OTPHash.hash(node));
        try {
            return findXMLText(nm, node, hc);
        } catch (NoSuchElementException ex) {
            return createXMLText(nm, node, hc);
        }
    }
    
    public int getName(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select nm from xt where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new NoSuchElementException
                    ("xml text with id: "+id);
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
            .prepareStatement("select nm,v from xn where xt=? order by pos");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            NodeBuffer buffer = new NodeBuffer();
            while (rs.next()) {
                buffer.append(rs.getInt(1));
                buffer.append(rs.getInt(2));
            }
            return buffer.toIntArray();
        } finally {
            rs.close();
        }
    }
    
    private int findXMLText(int nm, int node[], int hc) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select id from xt where hc=? and nm=?");
        
        ps.setInt(1, hc);
        ps.setInt(2, nm);
        
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
            if (equals(getNodes(id), node)) {
                return id;
            }
        } else {
            int[] ids = buffer.toIntArray();
            int scan = ids.length;
            while (--scan >= 0) {
                if (!equals(getNodes(ids[scan]), node)) {
                    continue;
                }
                return ids[scan];
            }
        }
        
        throw new NoSuchElementException("xml text");
    }
    
    public static boolean equals(int list1[], int list2[]) {
        try {
            if (list1.length != list2.length) {
                return false;
            } else {
                int scan = list1.length;
                while (--scan >= 0) {
                    if (list1[scan] != list2[scan]) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }
    
    private int createXMLText(int nm, int node[], int hc) throws SQLException {
        if (nm == 0) {
            throw new IllegalArgumentException("name undefined");
        }
        if ((node.length & 1) != 0) {
            throw new IllegalArgumentException("uneven node list");
        }
        
        PreparedStatement ps;
        
        ps = cf.prepareStatement("insert into xt (hc,nm) values (?,?)");
        ps.setInt(1, hc);
        ps.setInt(2, nm);
        int id = cf.executeInsert(ps, 1);
        
        if (node.length > 0) {
            ps = cf.prepareStatement("insert into xn (xt,pos,nm,v) values (?,?,?,?)");
            for (int i = 0;i < node.length;) {
                ps.setInt(1, id);
                ps.setInt(2, i >> 1);
                ps.setInt(3, node[i++]);
                ps.setInt(4, node[i++]);
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException("node insert updated != 1");
                }
            }
        }
        
        return id;
    }

}
