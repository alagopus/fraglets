/*
 * SimpleCache.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 13, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.cache;

/**
 * @author marion@users.sourceforge.net
 */
public interface SimpleCache {
    public CacheEntry get(int hash, int key);
    public CacheEntry get(int hash, Object key);
    public void put(CacheEntry entry);
    public void clear();
    
    public int getSize();
    public int getFill();
    public int getDrop();
}
