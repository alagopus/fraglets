/*
 * PDLParser.java
 * Copyright (C) 2002 Klaus Rennecke.
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

package net.sourceforge.fraglets.mtgo.trader;

import javax.swing.table.DefaultTableModel;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;

/**
 * Pipe-delimited format from spoiler lists. Expected fields:
 * <pre>
 * name | cost | type | culture | power | toughness | text | flavor | set | rarity | illustrator
 * </pre>
 * @author  kre
 */
public class PDLParser {
    public static final Double ZERO = new Double(0);
    
    DefaultTableModel model;
    
    /** Creates a new instance of PDLParser */
    public PDLParser(DefaultTableModel model) {
        this.model = model;
        model.setColumnCount(2);
        model.setColumnIdentifiers(new Object[] { "Name", "Price" });
    }
    
    public void parse(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line);
        }
//        System.err.println("model size: "+model.getRowCount());
    }
    public void parseLine(String line) {
        int pipe = line.indexOf('|');
        if (pipe > 0) {
            String name = line.substring(0, pipe);
            model.addRow(new Object[] {
                name,
                ZERO,
            });
        }
    }
}
