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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.8 $
 */
public class SensorCache implements SimpleCache {
    private String name;
    
    private volatile int sensor[];
    
    private static final int
        GET_SENSOR = 0,
        HIT_SENSOR = 1,
        PUT_SENSOR = 2;
        
    private static ArrayList instances = new ArrayList();
    
    private SimpleCache delegate;
    
    public SensorCache(String name) {
        this(name, new LazyCache());
    }
    
    protected SensorCache(String name, SimpleCache delegate) {
        this.name = name;
        this.sensor = new int[3];
        this.delegate = delegate;
        synchronized (instances) {
            instances.add(new WeakReference(this));
        }
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

    /**
     * Get statistics for all caches, one string per active cache. This method
     * will clean up the instances array as a side effect.
     * 
     * @return an array of cache statistics
     */
    public static String[] getStatistics()
    {
        int copy;
        String result[];
        
        // collect information
        synchronized (instances) {
            int scan = copy = instances.size();
            result = new String[copy];
            while (--scan >= 0) {
                SensorCache c = (SensorCache)((Reference)instances.get(scan)).get();
                if (c != null) {
                    int sensor[] = c.getSensor();
                    result[--copy] = c.getName()
                        + ": size=" + c.getSize()
                        + ", fill=" + c.getFill()
                        + ", drop=" + c.getDrop()
                        + ", put=" + sensor[PUT_SENSOR]
                        + ", get=" + sensor[GET_SENSOR]
                        + ", hit=" + sensor[HIT_SENSOR];
                } else {
                    // cleanup
                    instances.remove(scan);
                }
            }
        }
        
        // check for holes
        if (copy > 0) {
            String shrink[] = new String[result.length - copy];
            System.arraycopy(result, copy, shrink, 0, shrink.length);
            return shrink;
        } else {
            return result;
        }
    }
}
