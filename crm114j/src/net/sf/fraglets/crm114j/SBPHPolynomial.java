/*
 * $Id: SBPHPolynomial.java,v 1.1 2004-03-21 21:52:26 marion Exp $
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
 * @version $Id: SBPHPolynomial.java,v 1.1 2004-03-21 21:52:26 marion Exp $
 */
public class SBPHPolynomial implements Polynomial {

    public static final int INITIAL = 0xDEADBEEF;

    public static final int ITERATIONS = 15;

    private int iter;

    private int hashpipe0;
    private int hashpipe1;
    private int hashpipe2;
    private int hashpipe3;
    private int hashpipe4;

    private int hash;
    private int key;

    public SBPHPolynomial() {
        hashpipe0 = INITIAL;
        hashpipe1 = INITIAL;
        hashpipe2 = INITIAL;
        hashpipe3 = INITIAL;
        hashpipe4 = INITIAL;
    }

    /**
     * @see net.sf.fraglets.crm114j.Polynomial#push(int)
     */
    public final void push(int hash) {
        hashpipe4 = hashpipe3;
        hashpipe3 = hashpipe2;
        hashpipe2 = hashpipe1;
        hashpipe1 = hashpipe0;
        hashpipe0 = hash;
        iter = 0;
    }

    /**
     * @see net.sf.fraglets.crm114j.Polynomial#fold()
     */
    public final boolean fold() {
        if (iter >= ITERATIONS) {
            return false;
        }
        int j = iter; // avoid aload and use old local
        hash =
            hashpipe0
                + (3 * hashpipe1 * ((j >> 0) & 0x0001))
                + (5 * hashpipe2 * ((j >> 1) & 0x0001))
                + (11 * hashpipe3 * ((j >> 2) & 0x0001))
                + (23 * hashpipe4 * ((j >> 3) & 0x0001));

        key =
            7 * hashpipe0
                + (13 * hashpipe1 * ((j >> 0) & 0x0001))
                + (29 * hashpipe2 * ((j >> 1) & 0x0001))
                + (51 * hashpipe3 * ((j >> 2) & 0x0001))
                + (101 * hashpipe4 * ((j >> 3) & 0x0001));

        if (key == 0) {
            key = INITIAL;
        }

        iter += 1;
        return true;
    }

    /**
     * @see net.sf.fraglets.crm114j.Polynomial#getHash()
     */
    public int getHash() {
        return hash;
    }

    /**
     * @see net.sf.fraglets.crm114j.Polynomial#getKey()
     */
    public int getKey() {
        return key;
    }

    /**
     * @return
     */
    public int getIter() {
        return iter;
    }

}
