/*
 * Avatar.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.IOException;
import java.util.Iterator;

/** This is the object model for the avatar of a player in
 * the game, a "character" in RPG speak.
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
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.7 $
 */
public class Avatar {
    public static final PropertyChangeSupport CHANGE =
        new PropertyChangeSupport(Avatar.class);

    /** Holds value of property level. */
    private int level;
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property clazz. */
    private Class clazz;
    
    /** Holds value of property guild. */
    private Guild guild;
    
    /** Holds value of property zone. */
    private Zone zone;
    
    /** Holds value of property culture. */
    private Culture culture;
    
    /** Time last seen in logs. */
    private long timestamp;
    
    /** Optional properties. */
    private HashMap properties;
    
    /** Creates new Avatar
     * @param timestamp time stamp of log line where the character was seen.
     */
    public Avatar(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getProperty(String name) {
        if (name.equals("class")) {
            return clazz.getName();
        } else if (name.equals("culture")) {
            return culture.getName();
        } else if (name.equals("guild")) {
            return guild.getName();
        } else if (name.equals("level")) {
            return String.valueOf(level);
        } else if (name.equals("name")) {
            return name;
        } else if (name.equals("timestamp")) {
            return new java.sql.Date(timestamp).toString();
        } else if (name.equals("zone")) {
            return zone.getName();
        } else if (properties != null) {
            return properties.get(name).toString();
        } else {
            return null;
        }
    }
    
    public void setProperty(String name, String value, long timestamp) {
        if (name.equals("class")) {
            if (timestamp >= this.timestamp) {
                setClazz(Avatar.Class.create(value));
                this.timestamp = timestamp;
            }
        } else if (name.equals("culture")) {
            if (timestamp >= this.timestamp) {
                setCulture(Avatar.Culture.create(value));
                this.timestamp = timestamp;
            }
        } else if (name.equals("guild")) {
            if (timestamp >= this.timestamp) {
                setGuild(Avatar.Guild.create(value));
                this.timestamp = timestamp;
            }
        } else if (name.equals("level")) {
            if (timestamp >= this.timestamp) {
                setLevel(Integer.parseInt(value));
                this.timestamp = timestamp;
            }
        } else if (name.equals("name")) {
            throw new IllegalStateException("cannot reset avatar name");
        } else if (name.equals("timestamp")) {
            throw new IllegalStateException("cannot reset avatar timestamp");
        } else if (name.equals("zone")) {
            if (timestamp >= this.timestamp) {
                setZone(Avatar.Zone.create(value));
                this.timestamp = timestamp;
            }
        } else {
            synchronized (this) {
                if (properties == null) {
                    properties = new HashMap();
                }
                TimestampEntry entry = (TimestampEntry)properties.get(name);
                if (entry == null) {
                    entry = new TimestampEntry(value, timestamp);
                    properties.put(name, entry);
                } else if (timestamp >= entry.timestamp) {
                    entry.value = value;
                    entry.timestamp = timestamp;
                }
            }
        }
    }
    
    public Iterator getProperties() {
        return properties == null ? null :
            properties.entrySet().iterator();
    }
    
    public int getPropertyCount() {
        return properties == null ? 0 :
            properties.size();
    }

    /** Getter for property level.
     * @return Value of property level.
     */
    public int getLevel() {
        return level;
    }
    
    /** Setter for property level.
     * @param level New value of property level.
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Getter for property clazz.
     * @return Value of property clazz.
     */
    public Class getClazz() {
        return clazz;
    }
    
    /** Setter for property clazz.
     * @param clazz New value of property clazz.
     */
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
    
    /** Getter for property guild.
     * @return Value of property guild.
     */
    public Guild getGuild() {
        return guild;
    }
    
    /** Setter for property guild.
     * @param guild New value of property guild.
     */
    public void setGuild(Guild guild) {
        this.guild = guild;
    }
    
    /** Getter for property zone.
     * @return Value of property zone.
     */
    public Zone getZone() {
        return zone;
    }
    
    /** Setter for property zone.
     * @param zone New value of property zone.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }
    
    /** Getter for property culture.
     * @return Value of property culture.
     */
    public Culture getCulture() {
        return culture;
    }
    
    /** Setter for property culture.
     * @param culture New value of property culture.
     */
    public void setCulture(Culture culture) {
        this.culture = culture;
    }
    
    /** Fire a property change event signaling that a new instance was created. */
    public static void fireNewInstance(Object instance) {
        CHANGE.firePropertyChange(instance.getClass().getName(), null, instance);
    }
    
    /** Fire a property change event signaling that a new property was introduced. */
    public static void fireNewProperty(Object instance, String name) {
        CHANGE.firePropertyChange("Avatar.property", instance, name);
    }
    
    /** Convert this Avatar to a user-presentable string representation.
     * @return the string prepresentation of this Avatar
     */    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append('"').append(name)
            .append("\",\"").append(culture)
            .append("\",\"").append(clazz)
            .append("\",\"").append(level)
            .append("\",\"").append(guild)
            .append("\",\"").append(zone)
            .append('"');
        return buf.toString();
    }
    
    /** Getter for property timestamp.
     * @return Value of property timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /** Setter for property timestamp.
     * @param timestamp New value of property timestamp.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean equals(Object other) {
        return other == this;
    }
    
    public int hashCode() {
        return name.hashCode();
    }

    /** This class implements the object model of a guild. */
    public static class Guild implements Comparable {
        /** Holds value of property name. */
        private String name;
        /** The map to register shared guilds. */
        protected static HashMap shared = new HashMap();
        /** The comparator for Guilds. */
        public static final Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Guild)o1).getName().compareTo(((Guild)o2).getName());
            }
            public boolean equals(Object other) {
                return getClass() == other.getClass();
            }
        };
        /** Creates new Guild
         * @param name name of the new guild
         */
        protected Guild(String name) {
            this.name = name;
            fireNewInstance(this);
        }
        /** Create or reference a guild.
         * @param name name of the guild to create or reference
         * @return the created or already existing guild
         */
        public static Guild create(String name) {
            Guild result = (Guild)shared.get(name);
            if (result == null) {
                result = new Guild(name);
                shared.put(name, result);
            }
            return result;
        }
        /** Get all known values. */
        public static Collection getValues() {
            return shared.values();
        }
        /** Get a suitable comparator for sorting. */
        public static Comparator getComparator() {
            return comparator;
        }
        /** Getter for property name.
         * @return Value of property name.
         */
        public String getName() {
            return name;
        }
        /** Create an external string representation of this guild.
         * @return the string representation
         */
        public String toString() {
            return name;
        }
        /** Compare to another guild
         * @param other the other guild to compare to
         * @return true iff the other guild is identical to this
         */
        public boolean equals(Object other) {
            return other == this;
        }
        /** Compute a hash code for this guild.
         * @return the computed hash code
         */
        public int hashCode() {
            return name.hashCode();
        }
        
        public int compareTo(Object obj) {
            return comparator.compare(this, obj);
        }
        
    }
    
    public static class TimestampEntry {
        public Object value;
        public long timestamp;
        
        public TimestampEntry(Object value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
        
        public String toString() {
            return value.toString();
        }
    }

    /** This class implements the object model of a class. */
    public static class Class implements Comparable {
        /** Holds value of property name. */
        private String name;
        /** The map to register shared classes. */
        protected static HashMap shared = new HashMap();
        /** The comparator for Classes. */
        public static final Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Class)o1).getName().compareTo(((Class)o2).getName());
            }
            public boolean equals(Object other) {
                return getClass() == other.getClass();
            }
        };
        /** Creates new Clazz
         * @param name name of the class to create
         */
        protected Class(String name) {
            this.name = name;
            fireNewInstance(this);
        }
        /** Create or reference the class with the specified name.
         * @param name name of the class
         * @return the created or already existing class
         */
        public static Class create(String name) {
            name = canonicalName(name);
            Class result = (Class)shared.get(name);
            if (result == null) {
                result = new Class(name);
                shared.put(name, result);
            }
            return result;
        }
        /** Get all known values. */
        public static Collection getValues() {
            return shared.values();
        }
        /** Get a suitable comparator for sorting. */
        public static Comparator getComparator() {
            return comparator;
        }
        /** Getter for property name.
         * @return Value of property name.
         */
        public String getName() {
            return name;
        }
        /** Convert to external string representation.
         * @return a string representing this class
         */
        public String toString() {
            return name;
        }
        /** compare to another class instance
         * @param other the other instance to compare to
         * @return true iff the other zone is identical to this
         */
        public boolean equals(Object other) {
            return other == this;
        }
        /** Compute a hash code for this class.
         * @return the computed hash code
         */
        public int hashCode() {
            return name.hashCode();
        }
        
        public int compareTo(Object obj) {
            return comparator.compare(this, obj);
        }
        
        private static HashMap equivalenceMap;
        
        public static String canonicalName(String name) {
            if (equivalenceMap == null) {
                synchronized(Avatar.Class.class) {
                    if (equivalenceMap == null) {
                        try {
                            equivalenceMap = new HashMap();
                            Properties loader = new Properties();
                            loader.load(Avatar.Class.class.getResourceAsStream
                                ("ClassEquivalence.properties"));
                            Iterator i = loader.entrySet().iterator();
                            while (i.hasNext()) {
                                Map.Entry entry = (Map.Entry)i.next();
                                StringTokenizer tok = new StringTokenizer
                                    ((String)entry.getValue(), " \t,");
                                String canonical = tok.nextToken();
                                equivalenceMap.put(canonical, canonical);
                                while(tok.hasMoreTokens()) {
                                    equivalenceMap.put(tok.nextToken(), canonical);
                                }
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            System.err.println
                                ("could not load class equivalence map");
                        }
                    }
                }
            }
            String equivalent = (String)equivalenceMap.get(name);
            return equivalent != null ? equivalent : name;
        }
    }
    
    /** This class implements the object model of a culture. */
    public static class Culture implements Comparable {
        /** Holds value of property name. */
        private String name;
        /** The map to register shared cultures. */
        protected static HashMap shared = new HashMap();
        /** The comparator for Cultures. */
        public static final Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Culture)o1).getName().compareTo(((Culture)o2).getName());
            }
            public boolean equals(Object other) {
                return getClass() == other.getClass();
            }
        };
        /** Creates new Culture
         * @param name name of the culture to create
         */
        protected Culture(String name) {
            this.name = name;
            fireNewInstance(this);
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
        /** Get all known values. */
        public static Collection getValues() {
            return shared.values();
        }
        /** Get a suitable comparator for sorting. */
        public static Comparator getComparator() {
            return comparator;
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
        
        public int compareTo(Object obj) {
            return comparator.compare(this, obj);
        }
        
    }
    
    /** This class implements the object model of a zone. */
    public static class Zone implements Comparable {
        /** Holds value of property name. */
        private String name;
        /** The map to register shared zones. */
        protected static HashMap shared = new HashMap();
        /** The comparator for zones. */
        public static final Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Zone)o1).getName().compareTo(((Zone)o2).getName());
            }
            public boolean equals(Object other) {
                return getClass() == other.getClass();
            }
        };
        /** Creates new Zone
         * @param name name of the zone to create
         */
        protected Zone(String name) {
            this.name = name;
            fireNewInstance(this);
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
        public static Collection getValues() {
            return shared.values();
        }
        /** Get a suitable comparator for sorting. */
        public static Comparator getComparator() {
            return comparator;
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
        
        public int compareTo(Object obj) {
            return comparator.compare(this, obj);
        }
        
    }
}
