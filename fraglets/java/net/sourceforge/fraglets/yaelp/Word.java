/*
 * Word.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 28. April 2001, 05:19
 */

package de.rennecke.yaelp;

import java.util.HashMap;

/** This class implements the singleton object model of a word.
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
public class Word {
    /** The text of this word. */
    protected String text;
    /** The map to register shared words. */
    protected static HashMap shared = new HashMap();
    /** Creates new Word
     * @param text text for the new word
     */
    protected Word(String text) {
        this.text = text;
    }
    /** Create or reference a word with the given text.
     * @param text text of the word to create or reference
     * @return the created or referenced word
     */    
    public static Word create(String text) {
        Word result = (Word)shared.get(text);
        if (result == null) {
            result = new Word(text);
            shared.put(text, result);
        }
        return result;
    }
    /** Create or reference a word based on the given portion of a
     * character array.
     * @param text array of characters which will be used for the text of the word
     * @param off offset to the starting character in the given text to use
     * @param len length of the text to use from the given character array
     * @return the created or referenced word
     */    
    public static Word create(char text[], int off, int len) {
        return create(new String(text, off, len));
    }
    /** Compare to another word.
     * @param other other word to compare to
     * @return true iff the other word is identical to this
     */    
    public boolean equals(Word other) {
        return this == other;
    }
    /** Compare to another object
     * @param other other object to compare to
     * @return true if the other object is identical to this
     */    
    public boolean equals(Object other) {
        try {
            return equals((Word)other);
        } catch (ClassCastException ignored) {
            return false;
        }
    }
    /** Create an external string representation for this word.
     * @return the string representation
     */    
    public String toString() {
        return text;
    }
    /** Create an external string representation for an array of
     * words, i.e. a sentence.
     * @param words word to include in the string
     * @return the created string representation
     */    
    public static String toString(Word[] words) {
        if (words == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        int end = words.length;
        if (end > 0) {
            buf.append(words[0]);
            for (int i = 1; i < end; i++) {
                buf.append(' ').append(words[i]);
            }
        }
        return buf.toString();
    }
    
    /** Compute a hash code for this word.
     * @return the computed hash code
     */    
    public int hashCode() {
        return text.hashCode();
    }
    
}
