/*
 * CacheEntry.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 13, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.cache;

/**
 * @author marion@users.sourceforge.net
 */
public abstract class CacheEntry {
    private int hash;
    
    public CacheEntry (int hash) {
        this.hash = hash;
    }
        
    /**
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return hash;
    }
    
    public abstract boolean equals(Object other);
    
    public abstract boolean equals(int other);
}
