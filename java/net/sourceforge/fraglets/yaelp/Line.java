/*
 * Line.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

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
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.7 $
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
     * @param chars words on the new line
     */
    public Line(String timespec, char chars[]) {
        recycle(timespec, chars);
    }
    
    /** Recycle this instance, initializing it with new values.
     * @param timespec timestamp specification, parsed when needed
     * @param chars words on the new line */    
    public void recycle(String timespec, char chars[]) {
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
    
    public int getLength() {
        return chars.length;
    }
    
    /** Indexed getter for property char.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public char getChar(int index) {
        return chars[index];
    }
    
    /** Check whether this line starts with the characters
     * in <var>other</var>.
     * @param other the characters to compare to
     * @return true if <var>other</var> characters compare equal */    
    public boolean startsWith(char[] other) {
        if (chars.length >= other.length) {
            return compareTo(0, other, 0, other.length);
        } else {
            return false;
        }
    }
    
    /** Check whether this line ends with the characters
     * in <var>other</var>.
     * @param other the characters to compare to
     * @return true if <var>other</var> characters compare equal */    
    public boolean endsWith(char[] other) {
        int lineOff = chars.length - other.length;
        if (lineOff >= 0) {
            return compareTo(lineOff, other, 0, other.length);
        } else {
            return false;
        }
    }
    
    /** Compare a portion of this line to the <var>other</var>
     * characters. Comparision starts at <var>lineOff</var> in
     * the line and at <var>otherOff</var> in the other characters.
     * Only <var>len</var> characters are compared at most.
     * @param lineOff offset into this line
     * @param other other characters to compare to
     * @param otherOff offset into other characters
     * @param len length of consecutive characters to compare
     * @return true if all characters in <var>len</var> compare equal */
    public boolean compareTo(int lineOff, char other[], int otherOff, int len) {
        char chars[] = this.chars;
        if (lineOff + len > chars.length) {
            return false;
        }
        while (--len >= 0) {
            if (chars[lineOff++] != other[otherOff++]) {
                return false;
            }
        }
        return true;
    }
    
    public int indexOf(int start, char ch) {
        char chars[] = this.chars;
        int end = chars.length;
        while (start < end) {
            if (chars[start] == ch) {
                return start;
            } else {
                start += 1;
            }
        }
        return -1;
    }
    
    /** Create a String as a substring of this line.
     * @param off starting offset for the desired string
     * @param len length of the desired string
     * @return the created String */
    public String substring(int off, int len) {
        return new String(chars, off, len - off);
    }
    
    /** Create a String as a substring of this line.
     * @param off starting offset for the desired string
     * @return the created String */
    public String substring(int off) {
        return new String(chars, off, chars.length - off);
    }
}
