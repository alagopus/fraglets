/*
 * Avatar.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 30. April 2001, 10:07
 */

package de.rennecke.yaelp;

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
 * @author Klaus Rennecke
 * @version $Revision: 1.2 $
 */
public class Avatar {

    /** Holds value of property level. */
    private int level;
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property clazz. */
    private Clazz clazz;
    
    /** Holds value of property guild. */
    private Guild guild;
    
    /** Holds value of property zone. */
    private Zone zone;
    
    /** Holds value of property culture. */
    private Culture culture;
    
    /** Time last seen in logs. */
    private long timestamp;
    
    /** Creates new Avatar
     * @param timestamp time stamp of log line where the character was seen.
     */
    public Avatar(long timestamp) {
        this.timestamp = timestamp;
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
    public Clazz getClazz() {
        return clazz;
    }
    
    /** Setter for property clazz.
     * @param clazz New value of property clazz.
     */
    public void setClazz(Clazz clazz) {
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
}
