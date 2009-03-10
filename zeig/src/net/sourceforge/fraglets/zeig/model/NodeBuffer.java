/*
 * QueryBuffer.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
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
 */
package net.sourceforge.fraglets.zeig.model;

/**
 * @author unknown
 * @version $Revision: 1.6 $
 */
public class NodeBuffer {
    public static final int MT[] = new int[0];
    
    int buffer[];
    int size;
    int id;
    
    public NodeBuffer() {
    }
    
    public NodeBuffer(int initial) {
        buffer = new int[initial];
    }
    
    public NodeBuffer(int initial[]) {
        if (initial != null && initial.length > 0) {
            buffer = initial;
            size = initial.length;
        }
    }
    
    public NodeBuffer append(int value) {
        if (buffer == null) {
            buffer = new int[8];
        } else if (size >= buffer.length) {
            int grow[] = new int[buffer.length + buffer.length];
            System.arraycopy(buffer, 0, grow, 0, buffer.length);
            buffer = grow;
        }
        buffer[size++] = value;
        return this;
    }
    
    public int get(int index) {
        if (index >= 0 && index < size) {
            return buffer[index];
        } else {
            throw new ArrayIndexOutOfBoundsException
                (index + "not in [0," + size + "[");
        }
    }
    
    public int[] toIntArray() {
        if (size > 0) {
            int result[] = new int[size];
            System.arraycopy(buffer, 0, result, 0, size);
            return result;
        } else {
            return NodeBuffer.MT;
        }
    }
    
    public int getSize() {
        return size;
    }
    
    /**
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * @param i
     */
    public void setId(int i) {
        id = i;
    }

}