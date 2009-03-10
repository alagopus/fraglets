/*
 * $Id: StringHash.java,v 1.1 2004-03-21 21:52:26 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 * Algorithm from CRM114 Copyright 2001-2004  William S. Yerazunis.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.1
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.fraglets.crm114j;

/**
 * @version $Id: StringHash.java,v 1.1 2004-03-21 21:52:26 marion Exp $
 */
public class StringHash {
    
    public static int hash(Tokenizer tok) {
        return hash(tok.getBuffer(), tok.getOff(), tok.getLen());
    }

    public static int hash(char[] str, int off, int len) {
        int i;
        int hval;
        char chtmp;
    
        // initialize hval
        hval = len;
    
        //  for each character in the incoming text:
        for (i = 0; i < len; i++) {
            //    xor in the current byte against each byte of hval
            //    (which alone gaurantees that every bit of input will have
            //    an effect on the output)
            char c = str[i];
            hval ^= (c << 16 | c);
    
            //    add some bits out of the middle as low order bits.
            hval = hval + ((hval >>> 12) & 0x0000ffff);
    
            //     swap bytes 0 with 3 
            hval = (hval & 0x00ffff00) | (hval << 24) | (hval >>> 24);
    
            //    rotate hval 3 bits to the left (thereby making the
            //    3rd msb of the above mess the hsb of the output hash)
            hval = (hval << 3) + (hval >> 29);
        }
        return hval;
    }
}
