/*
 * SensorCache.java -
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
package net.sourceforge.fraglets.zeig.cache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.7 $
 */
public class SensorCache implements SimpleCache {
    private String name;
    
    private volatile int sensor[];
    
    private static final int
        GET_SENSOR = 0,
        HIT_SENSOR = 1,
        PUT_SENSOR = 2;
        
    private static ArrayList instances = new ArrayList();
    
    private LazyCache delegate;
    
    public SensorCache(String name) {
        this(name, new LazyCache());
    }
    
    protected SensorCache(String name, LazyCache delegate) {
        this.name = name;
        this.sensor = new int[3];
        this.delegate = delegate;
        instances.add(new WeakReference(this));
    }
    
    /**
     * @param hash
     * @param key
     * @return
     */
    public CacheEntry get(int hash, int key) {
        sensor[GET_SENSOR]++;
        CacheEntry result = delegate.get(hash, key);
        if (result != null) { 
            sensor[HIT_SENSOR]++;
        }
        return result;
    }

    /**
     * @param hash
     * @param key
     * @return
     */
    public CacheEntry get(int hash, Object key) {
        sensor[GET_SENSOR]++;
        CacheEntry result = delegate.get(hash, key);
        if (result != null) { 
            sensor[HIT_SENSOR]++;
        }
        return result;
    }

    /**
     * @param entry
     */
    public void put(CacheEntry entry) {
        sensor[PUT_SENSOR]++;
        delegate.put(entry);
    }

    protected int[] getSensor() {
        return sensor;
    }
    
    public String getName() {
        return name;
    }
    
    public static double percent(int have, int max) {
        return (10000L * have / max) / 100.0;
    }
    
    /**
     * 
     */
    public void clear() {
        delegate.clear();
    }

    /**
     * @return
     */
    public int getFill() {
        return delegate.getFill();
    }

    /**
     * @return
     */
    public int getSize() {
        return delegate.getSize();
    }

    /**
     * @return
     */
    public int getDrop() {
        return delegate.getDrop();
    }

}
