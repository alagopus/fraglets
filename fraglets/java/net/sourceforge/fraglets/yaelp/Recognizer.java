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
 * @version $Revision: 1.2 $
 */
public class Recognizer implements Vocabulary {
    /** Known avatars. */
    protected HashMap avatars = new HashMap();
    /** The number of lines recognized. */
    protected int lines;
    /** THe who line parser. */
    protected WhoLine whoLine = new WhoLine();
    
    /** Creates new Recognizer */
    public Recognizer() {
    }

    /** Determine whether the specified <var>line</var> is a result
     * from a <code>/who</code> invocation.
     * @param line the line to examine
     * @return true if the line in question is a <code>/who</code> line
     */    
    public static boolean isWhoLine(Line line)
    {
        try {
            if (line.getChar(0) == '[') switch(line.getChar(1)) {
                case '1': case '2': case '3': case '4': case '5':
                case '6': case '7': case '8': case '9': case 'A':
                    return true;
            }
            return false;
            // does not work....            
//            if (line.getWord(line.getLength()-2) == W_ZONE) {
//                return isNumeric(line, 0);
//            } else if (line.getWord(0) == W_ANONYMOUS) {
//                return line.getLength() > 1;
//            } else {
//                return false;
//            }
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
    protected Avatar updateAvatar(long timestamp, String name,
                                  int level, Clazz clazz, Culture culture,
                                  Guild guild, Zone zone) {
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
            result.setCulture(culture);
        }
        if (guild != null) {
            result.setGuild(guild);
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
            return whoLine.parse(line);
        } catch (SyntaxError err) {
            System.err.println("syntax error in line: "+line);
            err.printStackTrace();
            return null;
        }
    }
    public static class SyntaxError extends Error {
        public SyntaxError(String message) {
            super(message);
        }
    }
    protected class WhoLine {
        char input[];
        int position;
        public WhoLine() {
        }
        public Avatar parse(Line line) {
            this.input = line.getChars();
            this.position = 0;
            parseCharacter('[');
            int level;
            Clazz clazz;
            if (input[position] == 'A') {
                parseAnonymous();
                level = 0;
                clazz = null;
            } else {
                level = parseLevel();
                parseSpace();
                clazz = parseClazz();
            }
            parseCharacter(']');
            parseSpace();
            String name = parseName();
            if (name == null) {
                // bug in who, empty name and zone...
                return null;
            }
            skipSpace();
            Culture culture = null;
            if (position < input.length) {
                if (input[position] == '(') {
                    culture = parseCulture();
                    skipSpace();
                }
            }
            Guild guild = null;
            if (position < input.length) {
                if (input[position] == '<') {
                    guild = parseGuild();
                    skipSpace();
                }
            }
            Zone zone = null;
            if (position < input.length) {
                if (input[position] == 'Z') {
                    zone = parseZone();
                    skipSpace();
                }
            }
            if (position < input.length) {
                if (input[position] == 'L') {
                    parseLFG();
                }
            }
            if (position < input.length) {
                System.err.println("extra input at end: \""+
                    new String(input, position, input.length-position)+"\"");
            }
            return updateAvatar(line.getTimestamp(), name, level, clazz, culture, guild, zone);
        }
        public void skipSpace() {
            try {
                while (input[position] == ' ')
                    position++;
            } catch (ArrayIndexOutOfBoundsException ex) {
                // end
            }
        }
        public void parseSpace() {
            if (input[position++] != ' ') {
                throw new SyntaxError("space expected at "+position);
            }
        }
        public void parseCharacter(char c) {
            if (input[position++] != c) {
                throw new SyntaxError("character '"+c+"' expected at "+position);
            }
        }
        public int parseLevel() {
            int start = position;
            while (Character.isDigit(input[position]))
                position++;
            if (start == position) {
                throw new NumberFormatException("no number at "+start);
            } else {
                return Integer.parseInt(new String(input, start, position-start));
            }
        }
        public Clazz parseClazz() {
            if (Character.isUpperCase(input[position])) {
                int start = position;
                while (Character.isLetter(input[position]) || input[position] == ' ')
                    position++;
                if (start < position) {
                    return Clazz.create(new String(input, start, position-start));
                }
            }
            return null;
        }
        public void parseAnonymous() {
            parseCharacter('A');
            parseCharacter('N');
            parseCharacter('O');
            parseCharacter('N');
            parseCharacter('Y');
            parseCharacter('M');
            parseCharacter('O');
            parseCharacter('U');
            parseCharacter('S');
        }
        public void parseLFG() {
            parseCharacter('L');
            parseCharacter('F');
            parseCharacter('G');
        }
        public String parseName() {
            if (Character.isUpperCase(input[position])) {
                int start = position;
                try {
                    while (Character.isLetter(input[position]))
                        position++;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    // end
                }
                if (start < position) {
                    return new String(input, start, position-start);
                }
            }
            return null;
        }
        public Zone parseZone() {
            parseCharacter('Z');
            parseCharacter('O');
            parseCharacter('N');
            parseCharacter('E');
            parseCharacter(':');
            parseSpace();
            int start = position;
            try {
                while (Character.isLetterOrDigit(input[position]))
                    position++;
            } catch (ArrayIndexOutOfBoundsException ex) {
                // end
            }
            if (start == position) {
                return null;
            } else {
                return Zone.create(new String(input, start, position-start));
            }
        }
        public Culture parseCulture() {
            parseCharacter('(');
            int start = position;
            while (Character.isLetter(input[position]) || input[position] == ' ')
                position++;
            parseCharacter(')');
            return Culture.create(new String(input, start, position-start-1));
        }
        public Guild parseGuild() {
            parseCharacter('<');
            int start = position;
            while (Character.isLetter(input[position]) || input[position] == ' ')
                position++;
            parseCharacter('>');
            return Guild.create(new String(input, start, position-start-1));
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
