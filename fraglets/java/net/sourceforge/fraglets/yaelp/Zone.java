/*
 * Zone.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 1. Mai 2001, 23:28
 */

package de.rennecke.yaelp;

import java.util.HashMap;

/** This class implements the singleton object model of a zone.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * @author  Klaus Rennecke
 * @version $Revision: 1.2 $
 */
public class Zone {
    /** Holds value of property name. */
    private String name;
    /** The map to register shared zones. */
    protected static HashMap shared = new HashMap();
    /** Creates new Zone
     * @param name name of the zone to create
     */
    protected Zone(String name) {
        this.name = name;
    }
    /** Create or reference the zone with the specified name.
     * @param name name of the zone
     * @return the created or already existing zone
     */    
    public static Zone create(String name) {
        Zone result = (Zone)shared.get(name);
        if (result == null) {
            result = new Zone(name);
            shared.put(name, result);
        }
        return result;
    }
    /** Get all known values. */
    public static java.util.Collection getValues() {
        return shared.values();
    }
    /** Get a suitable comparator for sorting. */
    public static java.util.Comparator getComparator() {
        return new java.util.Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Zone)o1).getName().compareTo(((Zone)o2).getName());
            }
            public boolean equals(Object other) {
                return getClass() == other.getClass();
            }
        };
    }
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    /** Convert to external string representation.
     * @return a string representing this zone
     */    
    public String toString() {
        return name;
    }
    /** compare to another zone instance
     * @param other the other instance to compare to
     * @return true iff the other zone is identical to this
     */    
    public boolean equals(Object other) {
        return other == this;
    }
    /** Compute a hash code for this zone.
     * @return the computed hash code
     */    
    public int hashCode() {
        return name.hashCode();
    }
}
