/*
 * Recognizer.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

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
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.11 $
 */
public class Recognizer {
    /** Known avatars. */
    protected HashMap avatars = new HashMap();
    /** The number of lines recognized. */
    protected int lines;
    /** THe who line parser. */
    protected WhoLine whoLine = new WhoLine();
    /** The currenly played avatar. */
    protected Avatar active = null;
    /** Whether the guild of the currently active avatar was confirmed. */
    protected boolean guildConfirmed = false;
    
    /** Pattern for special lines. */
    protected static final char[] PATTERN__SAVED_ = 
        " saved.".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN_YOU_HAVE_BECOME_BETTER_AT_ =
        "You have become better at ".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN__IS_NOW__ =
        " is now ".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN__OF_YOUR_GUILD_ =
        " of your guild.".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN_WELCOME_TO_EVERQUEST_ =
        "Welcome to EverQuest!".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN_YOU_ARE_NOW_ =
        "You are now ".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN__OF_THE_GUILD_ =
        " of the guild.".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN_YOU_HAVE_JOINED_ =
        "You have joined ".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN__THE_GROUP_ =
        " the group.".toCharArray();
    /** Pattern for special lines. */
    protected static final char[] PATTERN__IS_NO_LONGER_A_MEMBER =
        " is no longer a member".toCharArray();
    
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
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    public static boolean isAvatarName(String name) {
        int scan = name.length();
        while (--scan > 0) {
            if (!Character.isLowerCase(name.charAt(scan))) {
                return false;
            }
        }
        return scan == 0 && Character.isUpperCase(name.charAt(0));
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
    public Avatar updateAvatar(long timestamp, String name, int level,
                                  Avatar.Class clazz, Avatar.Culture culture,
                                  Avatar.Guild guild, Avatar.Zone zone) {
        name = Avatar.normalizeName(name);
        Avatar result = (Avatar)avatars.get(name);
        if (result == null) {
            result = new Avatar(timestamp);
            result.setName(name);
            avatars.put(name.toString(), result);
        }
        if (guild != null) {
            result.setGuild(guild, timestamp);
            if (result == active) {
                guildConfirmed = guild == active.getGuild();
            }
        }
        if (result.getTimestamp() > timestamp) {
            return result;
        }
        if (clazz != null) {
            result.setClazz(clazz);
        }
        if (culture != null) {
            result.setCulture(culture);
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
    
    public void nextFile() {
        active = null;
        guildConfirmed = false;
    }
    
    /** Parse a given who line and update or create an avatar based
     * on the information found.
     * @param line line to parse
     * @return true iff parsed something.
     */    
    public boolean parse(Line line) {
        lines += 1;
        try {
            if (isWhoLine(line)) {
                return whoLine.parse(line) != null;
            }
            if (active == null) {
                if (line.endsWith(PATTERN__SAVED_)) {
                    String name = line.substring(0,
                        line.getLength() - PATTERN__SAVED_.length);
                    if (isAvatarName(name)) {
                        guildConfirmed = false;
                        active = updateAvatar(line.getTimestamp(), name, -1, null, null, null, null);
                        active.setProperty("Login", "true", line.getTimestamp());
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                if (line.getLength() == PATTERN_WELCOME_TO_EVERQUEST_.length &&
                    line.compareTo(0, PATTERN_WELCOME_TO_EVERQUEST_, 0, PATTERN_WELCOME_TO_EVERQUEST_.length)) {
                    active = null; // new login
                    return true;
                }
                if (line.startsWith(PATTERN_YOU_HAVE_BECOME_BETTER_AT_)) {
                    int start = PATTERN_YOU_HAVE_BECOME_BETTER_AT_.length;
                    int mark = line.indexOf(start, '!');
                    int end = line.getLength();
                    if (mark == -1 || end <= mark + 4 ||
                        line.getChar(mark + 1) != ' ' ||
                        line.getChar(mark + 2) != '(' ||
                        line.getChar(end - 1) != ')') {
                        return false;
                    }
                    String name = line.substring(start, mark);
                    String value = line.substring(mark + 3, end - 1);
                    active.setProperty(name, value, line.getTimestamp());
                    return true;
                }
                if (line.startsWith(PATTERN_YOU_HAVE_JOINED_)) {
                    if (line.endsWith(PATTERN__THE_GROUP_)) {
                        return false;
                    } else if (line.getChar(line.getLength() - 1) == '.') {
                        String name = line.substring(PATTERN_YOU_HAVE_JOINED_.length, line.getLength() - 1);
                        Avatar.Guild guild = Avatar.Guild.create(name);
                        active.setGuild(guild, line.getTimestamp());
                        guildConfirmed = guild == active.getGuild();
                        return true;
                    } else {
                        return false;
                    }
                }
                if (line.startsWith(PATTERN_YOU_ARE_NOW_) && line.endsWith(PATTERN__OF_THE_GUILD_)) {
                    String rank = line.substring(PATTERN_YOU_ARE_NOW_.length, line.getLength() - PATTERN__OF_THE_GUILD_.length);
                    active.setProperty("Rank", rank, line.getTimestamp());
                    return true;
                }
            }
            if (line.endsWith(PATTERN__OF_YOUR_GUILD_)) {
                int space = line.indexOf(0, ' ');
                if (line.compareTo(space, PATTERN__IS_NOW__, 0, PATTERN__IS_NOW__.length)) {
                    long timestamp = line.getTimestamp();
                    String name = line.substring(0, space);
                    space += PATTERN__IS_NOW__.length;
                    String rank = line.substring(space, line.getLength() - PATTERN__OF_YOUR_GUILD_.length);
                    Avatar.Guild guild = (guildConfirmed && active != null) ? active.getGuild() : null;
                    Avatar avatar = updateAvatar(timestamp, name, 0, null, null, guild, null);
                    avatar.setProperty("Rank", rank, timestamp);
                    return true;
                } else if (line.compareTo(space, PATTERN__IS_NO_LONGER_A_MEMBER, 0, PATTERN__IS_NO_LONGER_A_MEMBER.length)) {
                    long timestamp = line.getTimestamp();
                    String name = line.substring(0, space);
                    Avatar avatar = updateAvatar(timestamp, name, 0, null, null, Avatar.Guild.create("-"), null);
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (SyntaxError err) {
            System.err.println("syntax error in line: "+line);
            err.printStackTrace();
            return false;
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
            Avatar.Class clazz;
            if (input[position] == 'A') {
                parseAnonymous();
                level = 0;
                clazz = null;
            } else {
                level = parseLevel();
                parseSpace();
                clazz = parseClass();
            }
            parseCharacter(']');
            parseSpace();
            String name = parseName();
            if (name == null) {
                // bug in who, empty name and zone...
                return null;
            }
            skipSpace();
            Avatar.Culture culture = null;
            if (position < input.length) {
                if (input[position] == '(') {
                    culture = parseCulture();
                    skipSpace();
                }
            }
            Avatar.Guild guild = null;
            if (position < input.length) {
                if (input[position] == '<') {
                    guild = parseGuild();
                    skipSpace();
                } else if (clazz != null) {
                    // unguilded
                    guild = Avatar.Guild.create("-");
                }
            }
            Avatar.Zone zone = null;
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
        public Avatar.Class parseClass() {
            if (Character.isUpperCase(input[position])) {
                int start = position;
                while (Character.isLetter(input[position]) || input[position] == ' ')
                    position++;
                if (start < position) {
                    return Avatar.Class.create(new String(input, start, position-start));
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
        public Avatar.Zone parseZone() {
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
                return Avatar.Zone.create(new String(input, start, position-start));
            }
        }
        public Avatar.Culture parseCulture() {
            parseCharacter('(');
            int start = position;
            while (Character.isLetter(input[position]) || input[position] == ' ')
                position++;
            parseCharacter(')');
            return Avatar.Culture.create(new String(input, start, position-start-1));
        }
        public Avatar.Guild parseGuild() {
            parseCharacter('<');
            int start = position;
            while (input[position] != '>')
                position++;
            parseCharacter('>');
            return Avatar.Guild.create(new String(input, start, position-start-1));
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
