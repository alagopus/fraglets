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
 * @version $Revision: 1.1 $
 */
public class Line {
    /** Temporary time stamp specification until first parse. */
    private String timespec;

    /** Holds value of property timestamp. */
    private long timestamp;
    
    /** Holds value of property words. */
    private Word[] words;
    
    /** Timestamp format for lazy parsing. */
    public static final SimpleDateFormat timestampFormat =
        new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);

    
    /** Creates new Line
     * @param timestamp timestamp for the new line
     * @param words words on the new line
     */
    public Line(long timestamp, Word[] words) {
        this.timestamp = timestamp;
        this.words = words;
    }
    
    /** Creates new Line
     * @param timestamp timestamp for the new line
     * @param words words on the new line
     */
    public Line(Date timestamp, Word[] words) {
        this.timestamp = timestamp.getTime();
        this.words = words;
    }

    /** Creates new Line
     * @param timespec timestamp specification, parsed when needed
     * @param words words on the new line
     */
    public Line(String timespec, Word[] words) {
        this.timespec = timespec;
        this.words = words;
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
    
    /** Getter for property words.
     * @return Value of property words.
     */
    public Word[] getWords() {
        return words;
    }
    
    /** Setter for property words.
     * @param words New value of property words.
     */
    public void setWords(Word[] words) {
        this.words = words;
    }
    
    /** Create an external string representation of this line.
     * @return the string representation
     */    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(timestamp);
        buf.append("@");
        for (int i = 0; i < words.length; i++) {
            buf.append(i > 0 ? ",'" : "'").append(words[i]).append('\'');
        }
        return buf.toString();
    }
    
    /** Indexed getter for property word.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public Word getWord(int index) {
        return words[index];
    }
    
    /** Indexed setter for property word.
     * @param index Index of the property.
     * @param word New value of the property at <CODE>index</CODE>.
     */
    public void setWord(int index, Word word) {
        words[index] = word;
    }
    
    /** Getter for property length.
     * @return Value of property length.
     */
    public int getLength() {
        return words.length;
    }
    
}
