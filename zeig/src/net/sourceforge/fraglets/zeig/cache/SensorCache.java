/*
 * SensorCache.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 13, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.cache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.5 $
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
        Category.getInstance(name).info("cache create: "+delegate);
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
        int sizeBefore = delegate.getSize();
        int fillBefore = delegate.getFill();
        delegate.put(entry);
        int sizeAfter = delegate.getSize();
        if (sizeAfter != sizeBefore) {
            Category.getInstance(name).debug(
                "grow: fill=" + fillBefore + ", size=" + sizeBefore +
                ", more=" + sizeAfter + ", count=" + delegate.getFill());
        }
    }

    protected int[] getSensor() {
        return sensor;
    }
    
    public String getName() {
        return name;
    }
    
    public static void logStatistics(Priority priority) {
        for (Iterator i = instances.iterator(); i.hasNext();) {
            SensorCache element = (SensorCache)((WeakReference)i.next()).get();
            if (element == null) {
                continue;
            }
            int[] sensor = element.getSensor();
            String name = element.getName();
            Category category = Category.getInstance(name);
            category.log(priority, "get: "+sensor[GET_SENSOR]);
            category.log(priority, "hit: "+sensor[HIT_SENSOR]);
            category.log(priority, "put: "+sensor[PUT_SENSOR]);
            if (sensor[GET_SENSOR] > 0) {
                category.log(priority, " hit rate: "
                    + percent(sensor[HIT_SENSOR], sensor[GET_SENSOR]) + "%");
            }
            category.log(priority, "size: " + element.getSize());
            category.log(priority, "fill: " + element.getFill());
            category.log(priority, "drop: " + element.getDrop());
            if (element.getSize() > 0) {
                category.log(priority, " fill rate: "
                    + percent(element.getFill(), element.getSize()) + "%");
            }
        }
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

    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        try {
            Category.getInstance(name).info("cache finalize: "+delegate);
        } catch (Exception ex) {
            Category.getInstance(name).error("cache finalize", ex);
        }
    }

}
