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
 */
public class AttributeFactory {
    private ConnectionFactory cf;
    private SimpleCache idCache;
    private SimpleCache valueCache;

    public AttributeFactory(ConnectionFactory cf) {
        this.cf = cf;
        this.idCache = new SensorCache("attribute.id");
        this.valueCache = new SensorCache("attribute.value");
    }
    
    public int getAttribute(int nm, int value) throws SQLException {
        try {
            return findAttribute(nm, value);
        } catch (NoSuchElementException ex) {
            return createAttribute(nm, value);
        }
    }
    
    public int getName(int id) throws SQLException {
        return getEntry(id)[0];
    }
    
    public int getValue(int id) throws SQLException {
        return getEntry(id)[1];
    }
    
    private int[] getEntry(int id) throws SQLException {
        int hc = OTPHash.chain(0, id);
        CacheEntry entry = valueCache.get(hc, id);
        if (entry != null) {
            return ((AttributeCacheEntry)entry).getValue();
        }
        
        PreparedStatement ps = cf
            .prepareStatement("select nm,value from at where id=?");
        
        ps.setInt(1, id);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                int value[] = new int[2];
                value[0] = rs.getInt(1);
                value[1] = rs.getInt(2);
                valueCache.put(new AttributeCacheEntry(id, hc, value));
                return value;
            } else {
                throw new NoSuchElementException
                    ("attribute with id: "+id);
            }
        } finally {
            rs.close();
        }
    }
    
    private int findAttribute(int nm, int value) throws SQLException {
        int entryValue[] = new int[] { nm, value };
        int hc = OTPHash.hash(entryValue);
        CacheEntry entry = idCache.get(hc, entryValue);
        if (entry != null) {
            return ((AttributeCacheEntry)entry).getKey();
        }
        
        PreparedStatement ps = cf
            .prepareStatement("select id from at where nm=? and value=?");
        
        ps.setInt(1, nm);
        ps.setInt(2, value);
        
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                int id = rs.getInt(1);
                idCache.put(new AttributeCacheEntry(id, hc, entryValue));
                return id;
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
        int id = cf.executeInsert(ps, 1);
        int entryValue[] = new int[] { nm, value };
        valueCache.put(new AttributeCacheEntry
            (id, OTPHash.chain(0, id), entryValue));
        idCache.put(new AttributeCacheEntry
            (id, OTPHash.hash(entryValue), entryValue));
        return id;
    }

    public static class AttributeCacheEntry extends CacheEntry {
        private int key;
        private int value[];
        
        public AttributeCacheEntry(int key, int hash, int value[]) {
            super(hash);
            if (value.length != 2) {
                throw new IllegalArgumentException
                    ("invalid attribute cache value");
            }
            this.key = key;
            this.value = value;
        }
        
        /**
         * @see net.sourceforge.fraglets.zeig.cache.CacheEntry#equals(net.sourceforge.fraglets.zeig.cache.CacheEntry)
         */
        public final boolean equals(Object other) {
            if (other instanceof AttributeCacheEntry) {
                return equals(((AttributeCacheEntry)other).getValue());
            } else if (other instanceof int[]) {
                int otherValue[] = (int[])other;
                return this.value[0] == otherValue[0]
                    && this.value[1] == otherValue[1];
            } else {
                return false;
            }
        }
    
        public final int getKey() {
            return key;
        }
        
        public final int[] getValue() {
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
