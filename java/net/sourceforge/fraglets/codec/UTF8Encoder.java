/*
 * UCS4Codec.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 01:10
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
 * SOFTWARE.  */

package net.sourceforge.fraglets.codec;

import java.io.UnsupportedEncodingException;

/**
 * An UTF-8 encoder able to encode UCS-4 in addition to UCS-2. See
 * standards ISO/IEC 10646-1:1993 and RFC2279, RFC2781.
 * @author  marion@users.sourceforge.net
 */
public class UTF8Encoder {
    
    /** Holds value of property buffer. */
    private byte[] buffer = new byte[12];
    
    /** Holds value of property size. */
    private int size = 0;
    
    /** Creates a new instance of UCS4Codec */
    public UTF8Encoder() {
    }
    
    /** Encode a string of UCS-4 values in UTF-8. */
    public void encodeUCS4(int data[], int off, int len) {
        while (--len >= 0) {
            encodeUCS4(data[off++]);
        }
    }
    
    /** Encode a string  of UCS-2 values in UTF-8. */
    public void encodeUCS2(char data[], int off, int len) {
        while (--len >= 0) {
            int datum = data[off++];
            if ((datum & 0xf800) == 0xd800) {
                if (datum > 0xdbff) {
                    throw new IllegalArgumentException
                        ("invalid surrogate high-half: "+datum);
                } else if (--len < 0) {
                    throw new IllegalArgumentException
                        ("truncated surrogate");
                }
                int high = (datum & 0x3ff) << 10;
                datum = data[off++];
                if (datum < 0xdc00 || datum > 0xdfff) {
                    throw new IllegalArgumentException
                        ("invalid surrogate low-half: "+datum);
                }
                int low = datum & 0x3ff;
                datum = high | (datum & 0x3ff);
            }
            encodeUCS4(datum);
        }
    }
    
    /** Encode a string in UTF-8. */
    public void encodeString(String data) {
        char copy[] = data.toCharArray();
        encodeUCS2(copy, 0, copy.length);
    }
    
    /** Encode a UCS-4 datum in UTF-8. */
    public final void encodeUCS4(int datum) {
        if (datum < 0) {
            throw new IllegalArgumentException
                ("argument out of range: "+datum);
        } else if (datum <= 0x7f) {
            growBuffer(1);
            buffer[size++] = (byte)(datum & 0xff);
        } else if (datum <= 0x7ff) {
            growBuffer(2);
            buffer[size++] = (byte)(0xc0 | (datum >> 6));
            buffer[size++] = (byte)(0x80 | (datum & 0x3f));
        } else if (datum <= 0xffff) {
            growBuffer(3);
            buffer[size++] = (byte)(0xe0 | (datum >> 12));
            buffer[size++] = (byte)(0x80 | ((datum >> 6) & 0x3f));
            buffer[size++] = (byte)(0x80 | (datum & 0x3f));
        } else if (datum <= 0x1fffff) {
            growBuffer(4);
            buffer[size++] = (byte)(0xf0 | (datum >> 18));
            buffer[size++] = (byte)(0x80 | ((datum >> 12) & 0x3f));
            buffer[size++] = (byte)(0x80 | ((datum >> 6) & 0x3f));
            buffer[size++] = (byte)(0x80 | (datum & 0x3f));
        } else if (datum <= 0x3ffffff) {
            growBuffer(5);
            buffer[size++] = (byte)(0xf8 | (datum >> 24));
            buffer[size++] = (byte)(0x80 | ((datum >> 18) & 0x3f));
            buffer[size++] = (byte)(0x80 | ((datum >> 12) & 0x3f));
            buffer[size++] = (byte)(0x80 | ((datum >> 6) & 0x3f));
            buffer[size++] = (byte)(0x80 | (datum & 0x3f));
        } else {
            growBuffer(6);
            buffer[size++] = (byte)(0xfc | (datum >> 30));
            buffer[size++] = (byte)(0x80 | ((datum >> 24) & 0x3f));
            buffer[size++] = (byte)(0x80 | ((datum >> 18) & 0x3f));
            buffer[size++] = (byte)(0x80 | ((datum >> 12) & 0x3f));
            buffer[size++] = (byte)(0x80 | ((datum >> 6) & 0x3f));
            buffer[size++] = (byte)(0x80 | (datum & 0x3f));
        }
    }
    
    /** Getter for property buffer.
     * @return Value of property buffer.
     */
    public byte[] getBuffer() {
        return this.buffer;
    }
    
    /** Setter for property buffer.
     * @param buffer New value of property buffer.
     */
    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }
    
    /** Setter for properties buffer and size.
     * @param buffer New value of property buffer.
     * @param size New value of property size.
     */
    public void setBuffer(byte[] buffer, int size) {
        if (size > buffer.length) {
            throw new IndexOutOfBoundsException
                ("size too big, "+size+">"+buffer.length);
        }
        setBuffer(buffer);
        setSize(size);
    }
    
    /** Getter for property size.
     * @return Value of property size.
     */
    public int getSize() {
        return this.size;
    }
    
    /** Setter for property size.
     * @param size New value of property size.
     */
    public void setSize(int size) {
        if (size > buffer.length) {
            throw new IndexOutOfBoundsException
                ("size too big, "+size+">"+buffer.length);
        }
        this.size = size;
    }
    
    /** Return a copy of the current buffer, trimmed to the current size. */
    public byte[] toByteArray() {
        byte result[] = new byte[getSize()];
        if (result.length > 0) {
            System.arraycopy(getBuffer(), 0, result, 0, result.length);
        }
        return result;
    }
    
    /** Grow the current buffer so that it fits size+amount. */
    protected final void growBuffer(int amount) {
        if (size + amount > buffer.length) {
            int more = Math.max(size + amount, (size + size >> 2));
            byte grow[] = new byte[more];
            if (buffer != null && buffer.length > 0) {
                System.arraycopy(buffer, 0, grow, 0, size);
            }
            buffer = grow;
        }
    }
    
    public static void main(String args[]) {
        int count = 1000;
        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }
        char sample[] = new char[count];
        byte check[] = null;
        byte test[] = null;
        String sampleCheck = null;
        try {
            long local = 0;
            long system = 0;
            java.util.Random random = new java.util.Random(count);
            UTF8Encoder encoder = new UTF8Encoder();
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < count; j++) {
                    char value = random.nextInt(100) <= 2 ?
                        (char)random.nextInt(0x10000) :
                        (char)random.nextInt(0x100);
                    if (value >= 0xd800 && value <= 0xdfff) {
                        value &= 0xefff; // don't trust UTF-16 system decoding
//                        if (j + 1 >= count) {
//                            value &= 0xefff;
//                        } else {
//                            if (value > 0xdbff) {
//                                value &= 0xfbff;
//                            }
//                            sample[j++] = value;
//                            value = (char)(random.nextInt(0x400) + 0xdc00);
//                        }
                    }
                    sample[j] = value;
                }
                encoder.setSize(0);
                long t0 = System.currentTimeMillis();
                encoder.encodeUCS2(sample, 0, sample.length);
                long t1 = System.currentTimeMillis();
                test = encoder.toByteArray();
                sampleCheck = new String(sample);
                long t2 = System.currentTimeMillis();
                check = sampleCheck.getBytes("UTF-8");
                long t3 = System.currentTimeMillis();
                local += t1 - t0;
                system += t3 - t2;
                if (test.length != check.length) {
                    throw new RuntimeException
                        ("length differs: "+test.length+" != "+check.length);
                } else {
                    int scan = test.length;
                    while (--scan >= 0) {
                        if (test[scan] != check[scan]) {
                            throw new RuntimeException
                                ("result differs at ["+scan+"]: "
                                + (int)test[scan] + " != "
                                + (int)check[scan]);
                        }
                    }
                }
            }
            System.out.println("local: "+((double)local / (double)count)+"ms");
            System.out.println("system: "+((double)system / (double)count)+"ms");
        } catch (Exception ex) {
            ex.printStackTrace();
            print("sample", sample, System.err);
            print("test  ", test, System.err);
            print("check ", check, System.err);
        }
    }
    
    public static void print(String name, byte[] data, java.io.PrintStream out) {
        out.print(name);
        out.print(": [");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) out.print(", 0b");
            else out.print("0b");
            out.print(Integer.toString((int)(data[i] & 0xff), 2));
        }
        out.println(']');
    }
    
    public static void print(String name, int[] data, java.io.PrintStream out) {
        out.print(name);
        out.print(": [");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) out.print(", 0x");
            else out.print("0x");
            out.print(Integer.toString(data[i], 16));
        }
        out.println(']');
    }
    
    public static void print(String name, char[] data, java.io.PrintStream out) {
        out.print(name);
        out.print(": [");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) out.print(", 0b");
            else out.print("0b");
            out.print(Integer.toString((int)data[i], 2));
        }
        out.println(']');
    }
}
