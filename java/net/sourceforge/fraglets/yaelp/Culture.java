/*
 * Culture.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 1. Mai 2001, 23:28
 */

package de.rennecke.yaelp;

import java.util.HashMap;

/** This class implements the singleton object model of a culture.
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
public class Culture {

    /** Holds value of property name. */
    private String name;
    /** The map to register shared cultures. */
    protected static HashMap shared = new HashMap();
    
    /** Creates new Culture
     * @param name name of the culture to create
     */
    protected Culture(String name) {
        this.name = name;
    }
    
    /** Create or reference the culture with the specified name.
     * @param name name of the culture
     * @return the created or already existing culture
     */    
    public static Culture create(String name) {
        Culture result = (Culture)shared.get(name);
        if (result == null) {
            result = new Culture(name);
            shared.put(name, result);
        }
        return result;
    }

    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    
    /** Convert to external string representation.
     * @return a string representing this culture
     */    
    public String toString() {
        return name;
    }
    
    /** compare to another culture instance
     * @param other the other instance to compare to
     * @return true iff the other culture is identical to this
     */    
    public boolean equals(Object other) {
        return other == this;
    }
    
    /** Compute a hash code for this culture.
     * @return the computed hash code
     */    
    public int hashCode() {
        return name.hashCode();
    }
}
