/*
 * ResourceLoader.java
 * Copyright (C) 2002 Klaus Rennecke.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package net.sourceforge.fraglets.mtgo.trader;

import java.util.Properties;
import java.io.IOException;

/**
 * Resource loader library class.
 * @author marion@users.sourceforge.net
 */
public abstract class ResourceLoader {
    /** Properties read from Resources.properties. */
    private static Properties properties;
    
    private ResourceLoader() {}
    
    /**
     * Get a property from Resources.properties.
     * @param name name of property to fetch
     * @param fallback fallback value if property is undefined
     * @return value of the property, or fallback value
     */
    public static String getProperty(String name, String fallback) {
        return getProperties().getProperty(name, fallback);
    }
    
    /**
     * Get a property from Resources.properties.
     * @param name name of property to fetch
     * @return value of the property, or null
     */
    public static String getProperty(String name) {
        return getProperties().getProperty(name);
    }
    
    /**
     * Get the properties from Resources.properties.
     * @return the properties
     */
    public static synchronized Properties getProperties() {
        if (properties == null) {
            properties = new Properties(System.getProperties());
            try {
                properties.load(ResourceLoader.class
                    .getResource("Resources.properties").openStream());
            } catch (IOException ex) {
                throw new RuntimeException("Resources not found: "+ex);
            }
        }
        return properties;
    }
}
