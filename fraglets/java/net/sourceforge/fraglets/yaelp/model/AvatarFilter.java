/*
 * AvatarFilter.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
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

package net.sourceforge.fraglets.yaelp.model;

/** Abstract class for a filter on the roster. 
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
 * @version $Revision: 1.1 $
 */
public abstract class AvatarFilter {
    /** Determine whether the given avatar is accepted by this filter.
     * @param avatar the avatar to examine
     * @return true iff the given avatar is accepted by this filter
     */
    public abstract boolean accept(Avatar avatar);
    
    /** Simple or-combination of avatar filters. */    
    public static class Or extends AvatarFilter {
        /** First filter.
         */        
        protected AvatarFilter f0;
        /** Second filter.
         */        
        protected AvatarFilter f1;
        /** Create an or-combination of the given filters.
         * @param f0 first filter to use
         * @param f1 second filter to use
         */        
        public Or(AvatarFilter f0, AvatarFilter f1) {
            this.f0 = f0;
            this.f1 = f1;
        }
        /** Determine whether one of the filters accepts the given avatar.
         * @param avatar the avatar to examine
         * @return true iff the first or the second filter accept the given avatar
         */        
        public boolean accept(Avatar avatar) {
            return f0.accept(avatar) || f1.accept(avatar);
        }
    }
    
    /** Simple and-combination of avatar filters. */    
    public static class And extends AvatarFilter {
        /** First filter.
         */        
        protected AvatarFilter f0;
        /** Second filter.
         */        
        protected AvatarFilter f1;
        /** Create an and-combination of the given filters.
         * @param f0 first filter to use
         * @param f1 second filter to use
         */        
        public And(AvatarFilter f0, AvatarFilter f1) {
            this.f0 = f0;
            this.f1 = f1;
        }
        /** Determine whether both filters accept the given avatar.
         * @param avatar the avatar to examine
         * @return true iff the first and the second filter accept the given avatar
         */        
        public boolean accept(Avatar avatar) {
            return f0.accept(avatar) && f1.accept(avatar);
        }
    }
    
    /** Avatar filter to invert another filter. */
    public static class Not extends AvatarFilter {
        AvatarFilter filter;
        public Not(AvatarFilter filter) {
            this.filter = filter;
        }
        public boolean accept(Avatar avatar) {
            return !filter.accept(avatar);
        }
        public AvatarFilter getFilter() {
            return filter;
        }
    }
    
    /** Avatar filter to filter a set of avatars. */
    public static class Set extends AvatarFilter {
        java.util.HashSet set = new java.util.HashSet();
        public Set() {
        }
        public void add(Avatar avatar) {
            set.add(avatar);
        }
        public void remove (Avatar avatar) {
            set.remove(avatar);
        }
        public java.util.Set getSet() {
            return set;
        }
        public boolean accept(Avatar avatar) {
            return set.contains(avatar);
        }
    }
    
    /** Avatar filter based on guild. */    
    public static class Guild extends AvatarFilter {
        /** The guild an avatar must be in to be accepted by this filter.
         */        
        protected Avatar.Guild guild;
        /** Create a new avatar filter based on the given guild.
         * @param guild the guild avatars must be in to be accepted
         */        
        public Guild(String guild) {
            this.guild = Avatar.Guild.create(guild);
        }
        /** Create a new avatar filter based on the given guild.
         * @param guild the guild avatars must be in to be accepted
         */        
        public Guild(Avatar.Guild guild) {
            this.guild = guild;
        }
        /** Determine whether the given avatar is in the guild.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is in the guild for this filter
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getGuild() == guild;
        }
    }
    
    /** Avatar filter based on level. */    
    public static class Level extends AvatarFilter {
        /** The minimum level for an avatar to be accepted. */        
        protected int minLevel;
        
        /** The maximum level for an avatar to be accepted. */        
        protected int maxLevel;
        
        /** Create a new avatar filter based on the given level.
         * @param minLevel the minimum level for an avatar to be accepted */        
        public Level(int minLevel) {
            this(minLevel, Integer.MAX_VALUE);
        }
        
        /** Create a new avatar filter based on the given level bounds.
         * @param minLevel the minimum level for an avatar to be accepted
         * @param maxLevel the maximum level for an avatar to be accepted */        
        public Level(int minLevel, int maxLevel) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }
        
        /** Determine whether the given avatar is at least minLevel.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is at least minLevel
         */        
        public boolean accept(Avatar avatar) {
            int level = avatar.getLevel();
            return level >= minLevel && level <= maxLevel;
        }
    }
    
    /** Avatar filter based on class.
     */    
    public static class Class extends AvatarFilter {
        /** Class the avatar has to be to be accepted.
         */        
        protected Avatar.Class clazz;
        /** Create a new avatar filter based on the given class.
         * @param clazz the class for an avatar to be accepted
         */        
        public Class(String clazz) {
            this.clazz = Avatar.Class.create(clazz);
        }
        /** Create a new avatar filter based on the given class.
         * @param clazz the class for an avatar to be accepted
         */        
        public Class(Avatar.Class clazz) {
            this.clazz = clazz;
        }
        /** Determine whether the given avatar is of the required class.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is of class
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getClazz() == clazz;
        }
    }
    
    /** Avatar filter based on class. */    
    public static class Culture extends AvatarFilter {
        /** The culture an avatar must belong to be accepted by this filter.
         */        
        protected Avatar.Culture culture;
        /** Create a new avatar filter based on the given culture.
         * @param culture the culture for an avatar to be accepted
         */        
        public Culture(String culture) {
            this.culture = Avatar.Culture.create(culture);
        }
        /** Create a new avatar filter based on the given culture.
         * @param culture the culture for an avatar to be accepted
         */        
        public Culture(Avatar.Culture culture) {
            this.culture = culture;
        }
        /** Determine whether the given avatar belongs to culture.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is in culture
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getCulture() == culture;
        }
        /** String representation of this filter. */
        public String toString() {
            return culture.toString();
        }
    }
    
    /** Avatar filter based on class. */    
    public static class Time extends AvatarFilter {
        /** The time. */        
        protected long time;
        /** The direction. */
        protected boolean before;
        /** Create a new avatar filter based on the given culture.
         * @param culture the culture for an avatar to be accepted
         */        
        public Time(long time, boolean before) {
            this.time = time;
            this.before = before;
        }
        /** Determine whether the given avatar belongs to culture.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is in culture
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getTimestamp() < time == before;
        }
        /** String representation of this filter. */
        public String toString() {
            return (before?"before ":"after ")+
                new java.sql.Date(time).toString();
        }
    }
    
    /** Avatar filter for main characters, i.e. avatars
     * which were reported saved. */    
    public static class Main extends AvatarFilter {
        /** Create a new avatar filter based properties
         */        
        public Main() {
        }
        /** Determine whether the given avatar is a main character.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is a main character
         */        
        public boolean accept(Avatar avatar) {
            String value = avatar.getProperty("Login");
            return value != null && value.equalsIgnoreCase("true");
        }
    }
    
    /** Avatar filter requiring avatar properties,. */    
    public static class Property extends AvatarFilter {
        
        /** Holds value of property name. */
        private String name;
        
        /** Holds value of property value. */
        private String value;
        
        /** Create a new avatar filter based on property setting */        
        public Property(String name, String value) {
            setName(name);
            setValue(value);
        }
        
        /** Determine whether the given avatar has property set.
         * @param avatar the avatar to examine
         * @return true iff the given avatar has property set
         */        
        public boolean accept(Avatar avatar) {
            String current = avatar.getProperty(name);
            if (current != null) {
                return value == null ? true : value.equals(current);
            } else {
                return false;
            }
        }
        
        /** Getter for property name.
         * @return Value of property name.
         */
        public String getName() {
            return this.name;
        }
        
        /** Setter for property name.
         * @param name New value of property name.
         */
        public void setName(String name) {
            this.name = name;
        }
        
        /** Getter for property value.
         * @return Value of property value.
         */
        public String getValue() {
            return this.value;
        }
        
        /** Setter for property value.
         * @param value New value of property value.
         */
        public void setValue(String value) {
            this.value = value;
        }
        
        public String toString() {
            if (value == null) {
                return name+" is set";
            } else {
                return name+" = "+value;
            }
        }
    }
}
