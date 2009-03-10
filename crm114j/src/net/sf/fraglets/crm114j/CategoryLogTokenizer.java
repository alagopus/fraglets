/*
 * $Id: CategoryLogTokenizer.java,v 1.2 2004-04-04 23:39:21 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
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
 * @version $Id: CategoryLogTokenizer.java,v 1.2 2004-04-04 23:39:21 marion Exp $
 */
public class CategoryLogTokenizer extends Tokenizer {

    /**
     * @param buffer
     */
    public CategoryLogTokenizer(char[] buffer) {
        super(buffer);
    }

    /**
     * 
     */
    protected CategoryLogTokenizer() {
        super();
    }

    /**
     * @param str
     */
    public CategoryLogTokenizer(String str) {
        super(str);
    }

    /**
     * @see net.sf.fraglets.crm114j.Tokenizer#next()
     */
    public boolean next() {
        char[] b = getBuffer();
        int o = getOff() + getLen();
        int i = o, l = getMax();
        if (i < l) {
            boolean in = Character.isLetterOrDigit(b[i++]);
            while (i < l && in == Character.isLetterOrDigit(b[i])) {
                i++;
            }
            reset(b, o, i - o, l);
            return true;
        } else {
            reset(b, o, 0, l);
            return false;
        }
    }

}
