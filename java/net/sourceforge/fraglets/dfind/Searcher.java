/*
 * Searcher.java
 * Copyright (C) 2001 Klaus Rennecke.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Created on 25. Juli 2001, 04:53
 */

package net.sourceforge.fraglets.dfind;

import java.io.IOException;
import java.io.InputStream;

/** Plain word search using the Boyer/Moore algorithm of inverse deltas.
 *
 * @author  kre
 * @version 
 */
public class Searcher {
    /** Minimum buffer size. */
    public static final int MINSZ = 8192;
    /** Search pattern. */
    private byte pattern[];
    /** Search buffer. */
    private byte buffer[];
    /** Reverse delta for last character. */
    private int endDelta[];
    /** Reverse delta for next substring. */
    private int subDelta[];
    
    /** Creates new Searcher */
    public Searcher(String pattern) {
        this(pattern.getBytes());
    }
    
    /** Creates new Searcher */
    public Searcher(byte pattern[]) {
        setPattern(pattern);
    }
    
    /** Search the given input for the pattern, returning the relative
     * position from start, or -1. */
    public long search(InputStream in) throws IOException {
        long pos = 0;
        int n = in.read(buffer);
        while (n > 0) {
            int hit = search(pattern, buffer, 0, n, subDelta, endDelta);
            if (hit >= 0) {
                return pos + hit;
            } else {
                pos += n;
                int chunk = pattern.length-1;
                System.arraycopy(buffer, buffer.length-chunk,
                    buffer, 0, chunk);
                n = in.read(buffer, chunk, buffer.length-chunk);
            }
        }
        return -1;
    }
    
    /** Search the given input for the pattern, returning the
     * position from start, or -1. */
    public int search(String str) {
        return search(str.getBytes());
    }
    /** Search the given input for the pattern, returning the
     * position from start, or -1. */
    public int search(byte in[]) {
        if (in.length <= buffer.length) {
            return search(pattern, in, 0, in.length, subDelta, endDelta);
        } else {
            int pos = 0;
            int end = in.length;
            int fill = buffer.length;
            System.arraycopy(in, 0, buffer, 0, fill);
            while (pos < end) {
                int hit = search(pattern, buffer, 0, fill, subDelta, endDelta);
                if (hit >= 0) {
                    return pos + hit;
                } else {
                    pos += fill;
                    int chunk = pattern.length-1;
                    System.arraycopy(buffer, buffer.length-chunk,
                        buffer, 0, chunk);
                    fill = Math.min(end-pos, buffer.length-chunk);
                    System.arraycopy(in, 0, buffer, chunk, fill);
                    fill += chunk;
                }
            }
        }
        return -1;
    }
    
    public void setPattern(byte pattern[]) {
        this.pattern = pattern;
        int end = pattern.length;
        
        if (end < MINSZ / 16) {
            buffer = new byte[MINSZ];
        } else {
            buffer = new byte[end*16];
        }
        
        endDelta = new int[Byte.MAX_VALUE+1];
        subDelta = new int[end];
        int tmpDelta[] = new int[end];
        
        for (int i = 0; i < end; i++) {
            int c = pattern[i];
            tmpDelta[i] = i + 1 - endDelta[c];
            endDelta[c] = i + 1;
        }
        
        for (int i = 0; i < endDelta.length; i++) {
            endDelta[i] = end;
        }
        for (int i = 0; i < end; i++) {
            endDelta[pattern[i]] = end - i - 1;
        }
        endDelta[pattern[end-1]] = buffer.length + end * 2;

        for (int i = 0; i < end; i++) {
            subDelta[i] = end - rpr(i, pattern, end, tmpDelta);
        }
    }
    
    public static int search (byte w[], byte buf[],
                              int start, int size,
                              int subDelta[], int endDelta[])
    {
        int length = w.length;
        int large = endDelta[w[length-1]];
        int i = start + length - 1;

        for (;;) {
            while (i < size) {
                i += endDelta[buf[i]];
            }

            if (i < large) {
                return -1; /* not found */
            }

            i -= large;

            int j = length - 2;
            int k = i - 1;
            while (j >= 0 && buf[k--] == w[j]) {
                j--;
            }

            i -= length - 1;
            if (j < 0) {
                return i; /* found */
            }

            i += j + subDelta[j];
        }
    }
    
    public static int rpr (int j, byte w[], int length, int delta[])
    {
        int i, d;

        /* search substring j+1..length, return immediately if empty substring */
        if (++j >= length) {
            return j-1;
        }

        /* first possible occurence */
        d = j - delta[j];

        /* match the whole rest of w[j..length-1] */
        for (i = 0; j + i < length; i++) {
            /* if not before w[0], compare */
            if (d + i >= 0 && w[d+i] != w[j+i]) {
                if (d < 0) {
                    /* looking for substring in w[j..length-1] at word[0] */
                    i = -(d--);
                } else {
                    /* looking for next possible occurence */
                    i = 0;
                    d -= delta[d];
                }
            }
        }
        return d;
    }
}
