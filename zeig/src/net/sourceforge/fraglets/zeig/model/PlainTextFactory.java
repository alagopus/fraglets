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
import net.sourceforge.fraglets.zeig.cache.CacheEntry;
import net.sourceforge.fraglets.zeig.cache.SensorCache;
import net.sourceforge.fraglets.zeig.cache.SimpleCache;
import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.4 $
 */
public class PlainTextFactory {
    private ConnectionFactory cf;
    
    private SimpleCache plainCache;
    private SimpleCache valueCache;
    
    public PlainTextFactory(ConnectionFactory cf) {
        this.cf = cf;
        this.plainCache = new SensorCache("plain.text");
        this.valueCache = new SensorCache("plain.value");
    }
    
    private static PlainTextFactory instance;
    
    public int getPlainText(String text) throws SQLException {
        int hc = OTPHash.hash(text);
        CacheEntry entry = plainCache.get(hc, text);
        if (entry != null) {
            return ((PlainCacheEntry)entry).getValue(); 
        } else {
            int value = getPlainText(text, hc);
            plainCache.put(new PlainCacheEntry(text, hc, value));
            return value;
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
        int hc = OTPHash.chain(0, id);
        CacheEntry entry = valueCache.get(hc, id);
        if (entry != null) {
            return ((StringCacheEntry)entry).getValue();
        }
        
        PreparedStatement ps = cf
            .prepareStatement("select v from pt where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                String value = rs.getString(1);
                valueCache.put(new StringCacheEntry(id, hc, value));
                return value;
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
            .prepareStatement("select id,v from pt where hc=?");
        
        ps.setInt(1, hc);
        
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
            .prepareStatement("insert into pt (hc,v) values (?,?)");
        
        ps.setLong(1, hc);
        ps.setString(2, text);
        return cf.executeInsert(ps, 1);
    }
    
    public static class PlainCacheEntry extends CacheEntry {
        private String key;
        private int value;
        
        public PlainCacheEntry(String key, int hash, int value) {
            super(hash);
            this.key = key;
            this.value = value;
        }
        
        /**
         * @see net.sourceforge.fraglets.zeig.cache.CacheEntry#equals(net.sourceforge.fraglets.zeig.cache.CacheEntry)
         */
        public final boolean equals(Object other) {
            if (key.equals(other)) {
                return true;
            } else if (other instanceof PlainCacheEntry) {
                return equals(((PlainCacheEntry)other).getValue());
            } else {
                return false;
            }
        }

        public final int getValue() {
            return value;
        }

        /**
         * @see net.sourceforge.fraglets.zeig.cache.CacheEntry#equals(int)
         */
        public boolean equals(int other) {
            return this.value == other;
        }
    }
    
    public static class StringCacheEntry extends CacheEntry {
        private int key;
        private String value;
        
        public StringCacheEntry(int key, int hash, String value) {
            super(hash);
            this.key = key;
            this.value = value;
        }
        
        /**
         * @see net.sourceforge.fraglets.zeig.cache.CacheEntry#equals(net.sourceforge.fraglets.zeig.cache.CacheEntry)
         */
        public final boolean equals(Object other) {
            if (other instanceof StringCacheEntry) {
                return equals(((StringCacheEntry)other).getValue());
            } else {
                return false;
            }
        }

        public final String getValue() {
            return value;
        }

        /**
         * @see net.sourceforge.fraglets.zeig.cache.CacheEntry#equals(int)
         */
        public boolean equals(int other) {
            return this.key == other;
        }
    }
}
