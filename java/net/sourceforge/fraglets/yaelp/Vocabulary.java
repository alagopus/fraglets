/*
 * Vocabulary.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 30. April 2001, 09:35
 */

package de.rennecke.yaelp;

/** This interface defines the vocabulary of known words.
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
public interface Vocabulary {
    /** Known word. */
    public static final Word
        W_ANONYMOUS = Word.create("ANONYMOUS"),
        W_Barbarian = Word.create("Barbarian"),
        W_Dark      = Word.create("Dark"),
        W_Dwarf     = Word.create("Dwarf"),
        W_Elf       = Word.create("Elf"),
        W_Erudite   = Word.create("Erudite"),
        W_Gnome     = Word.create("Gnome"),
        W_Half      = Word.create("Half"),
        W_Halfling  = Word.create("Halfling"),
        W_have      = Word.create("have"),
        W_High      = Word.create("High"),
        W_Human     = Word.create("Human"),
        W_Iksar     = Word.create("Iksar"),
        W_Ogre      = Word.create("Ogre"),
        W_Troll     = Word.create("Troll"),
        W_Wood      = Word.create("Wood"),
        W_You       = Word.create("You"),
        W_ZONE      = Word.create("ZONE");
}
