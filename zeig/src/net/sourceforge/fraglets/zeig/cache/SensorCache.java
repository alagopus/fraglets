/*
 * SensorCache.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 13, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.cache;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.3 $
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
        instances.add(this);
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
    
    public static void logStatistics(Priority priority) {
        Category category = Category.getInstance(SensorCache.class);
        for (Iterator i = instances.iterator(); i.hasNext();) {
            SensorCache element = (SensorCache)i.next();
            int[] sensor = element.getSensor();
            String name = element.getName();
            category.log(priority, name + ".get: "+sensor[GET_SENSOR]);
            category.log(priority, name + ".hit: "+sensor[HIT_SENSOR]);
            category.log(priority, name + ".put: "+sensor[PUT_SENSOR]);
            if (sensor[GET_SENSOR] > 0) {
                category.log(priority, name + " hit rate: "
                    + percent(sensor[HIT_SENSOR], sensor[GET_SENSOR]) + "%");
            }
            category.log(priority, name + ".size: " + element.getSize());
            category.log(priority, name + ".fill: " + element.getFill());
            category.log(priority, name + ".drop: " + element.getDrop());
            if (element.getSize() > 0) {
                category.log(priority, name + " fill rate: "
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

}
