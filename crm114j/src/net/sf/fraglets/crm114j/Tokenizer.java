/*
 * $Id: Tokenizer.java,v 1.2 2004-04-03 23:36:47 marion Exp $
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
 * @version $Id: Tokenizer.java,v 1.2 2004-04-03 23:36:47 marion Exp $
 */
public class Tokenizer {
    private char[] buffer;
    private int off;
    private int len;
    
    public Tokenizer(char[] buffer) {
        reset(buffer);
    }
    
    protected Tokenizer() {
    }
    
    public Tokenizer(String str) {
        this(str.toCharArray());
    }
    
    public boolean next() {
        off += len;
        len = 0;
        char[] b = buffer;
        int i = off, l = b.length;
        while (i < l && !isGraph(b[i])) {
            i++;
        } 
        off = i;
        while (i < l && isGraph(b[i])) {
            i++;
        } 
        len = i - off;
        return len > 0;
    }
    
    public void reset(char[] buffer) {
        this.buffer = buffer;
        this.off = 0;
        this.len = 0;
    }
    
    public static boolean isGraph(char c) {
        return !Character.isSpaceChar(c) && !Character.isISOControl(c);
    }
    
    /**
     * @return
     */
    public final char[] getBuffer() {
        return buffer;
    }

    /**
     * @return
     */
    public final int getLen() {
        return len;
    }

    /**
     * @return
     */
    public final int getOff() {
        return off;
    }

}
