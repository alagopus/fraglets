/*
 * Recognizer.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 30. April 2001, 09:30
 */

package de.rennecke.yaelp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/** This class implements a recognizer for log lines.
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
 * @version $Revision: 1.1 $
 */
public class Recognizer implements Vocabulary {
    /** Known avatars. */
    protected HashMap avatars = new HashMap();
    /** The number of lines recognized. */
    protected int lines;
    
    /** Creates new Recognizer */
    public Recognizer() {
    }

    /** Determine whether the word(s) on the <var>line</var>
     * beginning at position <var>start</var> are a known
     * culture name.
     * @param line line to look at
     * @param start start position in line to look at
     * @return true iff the words at start are a valid culture name
     */    
    public static boolean isRace(Line line, int start)
    {
        Word word = line.getWord(start);
        if (word == W_Barbarian || word == W_Dwarf || word == W_Erudite ||
            word == W_Gnome || word == W_Halfling || word == W_Human ||
            word == W_Iksar || word == W_Ogre || word == W_Troll) {
            return true;
        } else if (start+1 >= line.getLength()) {
            return false;
        } else if (word == W_Dark || word == W_Half ||
                   word == W_High || word == W_Wood) {
            return line.getWord(start+1) == W_Elf;
        } else {
            return false;
        }
    }
    
    /** Determine whether the word in <var>line</var> at position
     * <var>index</var> is a numeric expression.
     * @param line the line to look at
     * @param index index to the word in line to examine
     * @return true iff the word at index is a numeric expression
     */    
    public static boolean isNumeric(Line line, int index)
    {
        String text = line.getWord(index).toString();
        int scan = text.length();
        while (--scan >= 0) {
            if (!Character.isDigit(text.charAt(scan))) {
                return false;
            }
        }
        return true;
    }
    
    /** Determine whether the specified <var>line</var> is a result
     * from a <code>/who</code> invocation.
     * @param line the line to examine
     * @return true if the line in question is a <code>/who</code> line
     */    
    public static boolean isWhoLine(Line line)
    {
        try {
            if (line.getWord(line.getLength()-2) == W_ZONE) {
                return isNumeric(line, 0);
            } else if (line.getWord(0) == W_ANONYMOUS) {
                return line.getLength() > 1;
            } else {
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    /** Update or create an avatar with the specified information.
     * @param timestamp timestamp for the avatar
     * @param name name of the avatar
     * @param level level of the avatar
     * @param clazz character class of the avatar
     * @param culture culture of the avatar
     * @param guild guild of the avatar
     * @param zone zone the avatar was last seen in
     * @return the updated or created avatar
     */    
    protected Avatar updateAvatar(long timestamp, Word name,
                                  int level, Word clazz, Word culture[],
                                  Word guild[], Word zone) {
        Avatar result = (Avatar)avatars.get(name.toString());
        if (result == null) {
            result = new Avatar(timestamp);
            result.setName(name);
            avatars.put(name.toString(), result);
        } else if (result.getTimestamp() > timestamp) {
            return result;
        }
        if (clazz != null) {
            result.setClazz(clazz);
        }
        if (culture != null) {
            result.setCulture(Culture.create(culture));
        }
        if (guild != null) {
            result.setGuild(Guild.create(guild));
        }
        if (level > 0) {
            result.setLevel(level);
        }
        if (zone != null) {
            result.setZone(zone);
        }
        // update time last seen
        result.setTimestamp(timestamp);
        return result;
    }
    
    /** Parse a given who line and update or create an avatar based
     * on the information found.
     * @param line line to parse
     * @return an updated or created avatar, or null
     */    
    public Avatar parseWhoLine(Line line) {
        lines += 1;
        
        try {
            int index = 0;
            int level = Integer.parseInt(line.getWord(index++).toString());
            Word clazz = line.getWord(index++);
            Word name = line.getWord(index++);
            Word word = line.getWord(index++);
            Word culture[];
            if (word == W_Dark || word == W_Half ||
                word == W_High || word == W_Wood) {
                culture = new Word[] {word, line.getWord(index++)};
            } else {
                culture = new Word[] {word};
            }
            int end = line.getLength() - 2;
            Word guild[] = new Word[end - index];
            for (int i = 0; index < end; i++) {
                guild[i] = line.getWord(index++);
            }
            Word zone = line.getWord(end+1);
            return updateAvatar(line.getTimestamp(), name, level,
                                clazz, culture, guild, zone);
        } catch (NumberFormatException ex) {
            // anonymous line
            if (line.getWord(0) == W_ANONYMOUS) {
                int end = line.getLength();
                int index = 1;
                Word name = line.getWord(index++);
                if (end > index) {
                    Word guild[] = new Word[end - index];
                    for (int i = 0; index < end; i++) {
                        guild[i] = line.getWord(index++);
                    }
                    return updateAvatar(line.getTimestamp(), name, -1,
                                        null, null, guild, null);
                } else {
                    return updateAvatar(line.getTimestamp(), name, -1,
                                        null, null, null, null);
                }
            } else {
                return null;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }
    
    /** Get an array of all known avatars so far.
     * @return the array of known avatars
     */    
    public Avatar[] getAvatars() {
        Collection c = avatars.values();
        return (Avatar[])c.toArray(new Avatar[c.size()]);
    }
    
    /** Get the number of who lines recognized until now.
     * @return number of recognized who lines
     */    
    public int getLines() {
        return lines;
    }
}
