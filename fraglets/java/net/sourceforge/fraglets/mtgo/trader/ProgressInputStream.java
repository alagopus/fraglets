/*
 * ProgressInputStream.java
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

import java.io.InputStream;
import java.io.IOException;
import java.io.FilterInputStream;

/**
 * A filter input stream signalling progress to a progress listener.
 * @author  marion@users.sourceforge.net
 */
public class ProgressInputStream extends FilterInputStream {
    protected int need;
    protected int have;
    protected ProgressListener listener;
    public ProgressInputStream(InputStream in, ProgressListener listener, int size) {
        super(in);
        this.listener = listener;
        this.need = size;
        this.have = 0;
    }
    
    public int read(byte[] values) throws IOException {
        int retValue;
        retValue = super.read(values);
        listener.setPercent((have += retValue) * 100 / need);
        return retValue;
    }
    
    public int read(byte[] values, int param, int param2) throws IOException {
        int retValue;
        retValue = super.read(values, param, param2);
        listener.setPercent((have += retValue) * 100 / need);
        return retValue;
    }
    
    public long skip(long param) throws IOException {
        long retValue;
        retValue = super.skip(param);
        listener.setPercent((have += retValue) * 100 / need);
        return retValue;
    }
    
    public int read() throws IOException {
        int retValue;
        retValue = super.read();
        listener.setPercent((have += 1) * 100 / need);
        return retValue;
    }
    
    public void close() throws IOException {
        listener.setObjective(null);
        super.close();
    }
    
}
