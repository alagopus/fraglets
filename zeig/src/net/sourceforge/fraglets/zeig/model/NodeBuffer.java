/*
 * QueryBuffer.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 6, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.model;

/**
 * @author unknown
 * @version $Revision: 1.4 $
 */
public class NodeBuffer {
    public static final int MT[] = new int[0];
    
    int buffer[];
    int size;
    int id;
    
    public NodeBuffer() {
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