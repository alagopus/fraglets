/*
 * MediaFactory.java -
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

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.NoSuchElementException;

import net.sourceforge.fraglets.codec.OTPHash;
import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.3 $
 */
public class MediaFactory {
    private ConnectionFactory cf;
    
    public MediaFactory(ConnectionFactory cf) {
        this.cf = cf;
    }
    
    protected int getMedia(RetryInputException tale) throws IOException, SQLException {
        if (tale.getCause() instanceof NoSuchElementException) {
            return createMedia(tale);
        } else {
            try {
                return findMedia(tale);
            } catch (NoSuchElementException ex) {
                throw tale.rethrow(ex);
            }
        }
    }
    
    public int getMedia(InputStream in, String type) throws IOException {
        OTPHash.Output sink = new OTPHash.Output();
        byte buffer[] = new byte[8192];
        sink.write(type.getBytes("UTF-8"));
        int n;
        int length = 0;
        while ((n = in.read(buffer)) > 0) {
            sink.write(buffer, 0, n);
            length += n;
        }
        throw new RetryInputException("retry input", in, type, length, sink.getHash());
    }
    
    public InputStream getMedia(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select v from me where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBinaryStream(1);
            // we cannot close the result set yet
        } else {
            throw new NoSuchElementException
                ("media with id: "+id);
        }
    }
    
    public String getType(int id) throws SQLException {
        PreparedStatement ps = cf
            .prepareStatement("select t from me where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new NoSuchElementException
                    ("media with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    private int findMedia(RetryInputException tale) throws IOException, SQLException {
        int hc = tale.getHashCode();
        
        PreparedStatement ps = cf
            .prepareStatement("select id,v from me where hc=?");
        
        ps.setInt(1, hc);
        
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                int id = rs.getInt(1);
                if (tale.containsId(id)) {
                    continue;
                }
                if (equals(tale.getInputStream(), rs.getBinaryStream(2), tale.getLength())) {
                    return id;
                }
                throw tale.rethrow(id);
            }
            throw new NoSuchElementException();
        } finally {
            rs.close();
        }
    }
    
    private int createMedia(RetryInputException tale) throws SQLException {
        int hc = tale.getHashCode();
        
        PreparedStatement ps = cf
            .prepareStatement("insert into me (hc,t,v) values (?,?,?)");
        
        ps.setLong(1, hc);
        ps.setString(2, tale.getType());
        ps.setBinaryStream(3, tale.getInputStream(), tale.getLength());
        return cf.executeInsert(ps, 1);
    }
    
    public static boolean equals(InputStream in1, InputStream in2, int length) throws IOException {
        byte b1[] = new byte[8192];
        byte b2[] = new byte[8192];
        int n1 = 0, n2 = 0;
        int o1 = 0, o2 = 0;
        for(;;) {
            if ((n1 = in1.read(b1)) <= 0) {
                break;
            } else {
                length -= n1;
                o1 = 0;
            }
            while (o1 < n1) {
                if (o2 < n2) {
                    if (b1[o1++] != b2[o2++]) {
                        return false;
                    }
                } else {
                    if ((n2 = in2.read(b2)) <= 0) {
                        return false;
                    } else {
                        o2 = 0;
                    }
                }
            }
        }
        if (o2 < n2) {
            return false;
        } else {
            if (in2.read(b2) > 0) {
                return false;
            } else {
                if (length != 0) {
                    throw new IllegalStateException("length difference: "+length);
                }
                return true;
            }
        }
    }
    
    public class RetryInputException extends IOException {
        private Throwable cause;
        private InputStream in;
        private HashSet idSet;
        private String type;
        private int length;
        private int hc;
        /**
         * @param s
         */
        public RetryInputException(String s, InputStream in, String type, int length, int hc) {
            super(s);
            this.in = in;
            this.hc = hc;
            this.type = type;
            this.length = length;
            this.idSet = new HashSet();
        }
        
        public int retry(InputStream in) throws IOException, SQLException {
            if (this.in == in) {
                throw new IllegalArgumentException("cannot reuse input stream");
            }
            this.in = in;
            return getMedia(this);
        }
        
        public Throwable getCause() {
            return cause;
        }
        
        public String getType() {
            return this.type;
        }
        
        public int getLength() {
            return this.length;
        }
        
        public InputStream getInputStream() {
            return this.in;
        }
        
        public int getHashCode() {
            return hc;
        }
        
        public void addId(int id) {
            idSet.add(new Integer(id));
        }
        
        public boolean containsId(int id) {
            return idSet.contains(new Integer(id));
        }
        
        public boolean isType(String type) {
            try {
                return this.type == type || this.type.equals(type);
            } catch (NullPointerException ex) {
                return false;
            }
        }
        
        public RetryInputException rethrow(int id) {
            if (!isType(type)) {
                throw new IllegalArgumentException(type+"!="+this.type);
            }
            addId(id);
            return (RetryInputException)fillInStackTrace();
        }
        
        public RetryInputException rethrow(Throwable cause) {
            if (!isType(type)) {
                throw new IllegalArgumentException(type+"!="+this.type);
            }
            this.cause = cause;
            return (RetryInputException)fillInStackTrace();
        }
    }
}
