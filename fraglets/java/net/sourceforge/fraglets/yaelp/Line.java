/*
 * Line.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 28. April 2001, 06:20
 */

package de.rennecke.yaelp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** This class implements a parsed log line, with timestamp and word tokens.
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
 * @author  kre
 * @version $Revision: 1.2 $
 */
public class Line {
    /** Temporary time stamp specification until first parse. */
    private String timespec;

    /** Holds value of property timestamp. */
    private long timestamp;
    
    /** Timestamp format for lazy parsing. */
    public static final SimpleDateFormat timestampFormat =
        new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);

    /** Holds value of property chars. */
    private char[] chars;    
    
    /** Creates new Line
     * @param timestamp timestamp for the new line
     * @param words words on the new line
     */
    public Line(long timestamp, char chars[]) {
        this.timestamp = timestamp;
        this.chars = chars;
    }
    
    /** Creates new Line
     * @param timestamp timestamp for the new line
     * @param words words on the new line
     */
    public Line(Date timestamp, char chars[]) {
        this.timestamp = timestamp.getTime();
        this.chars = chars;
    }

    /** Creates new Line
     * @param timespec timestamp specification, parsed when needed
     * @param words words on the new line
     */
    public Line(String timespec, char chars[]) {
        this.timespec = timespec;
        this.chars = chars;
    }
    
    /** Getter for property timestamp.
     * @return Value of property timestamp.
     */
    public long getTimestamp() {
        String spec = timespec;
        if (spec != null) try {
            timespec = null;
            timestamp = timestampFormat.parse(spec).getTime();
        } catch (ParseException ex) {
            // doh, leave timestamp alone....
        }
        return timestamp;
    }
    
    /** Setter for property timestamp.
     * @param timestamp New value of property timestamp.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /** Setter for property timestamp.
     * @param timestamp New value of property timestamp.
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp.getTime();
    }
    
    /** Create an external string representation of this line.
     * @return the string representation
     */    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(timestamp);
        buf.append("@");
        buf.append(chars);
        return buf.toString();
    }
    
    /** Getter for property chars.
     * @return Value of property chars.
     */
    public char[] getChars() {
        return chars;
    }
    
    /** Indexed getter for property char.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public char getChar(int index) {
        return chars[index];
    }
}
