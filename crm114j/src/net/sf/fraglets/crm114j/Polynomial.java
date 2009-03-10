/*
 * $Id: Polynomial.java,v 1.1 2004-03-21 21:52:26 marion Exp $
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
 * @version $Id: Polynomial.java,v 1.1 2004-03-21 21:52:26 marion Exp $
 */
public interface Polynomial {
    
    /** Push another token hash into the pipeline. */
    public void push(int hash);
    
    /** Fold the hashes. */
    public boolean fold();
    
    /** Get the hash value. */
    public int getHash();
    
    /** Get the crosscut. */
    public int getKey();
    
    /** Get the iteration count. */
    public int getIter();
}
