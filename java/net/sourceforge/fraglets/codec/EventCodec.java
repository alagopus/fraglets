/*
 * EventCodec.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 14:21
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

import java.util.Arrays;

/**
 *
 * @author  marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public abstract class EventCodec {
    public final int STRING_WORD = 0;
    
    /** Counter to assign next literal. */
    protected int nextLiteral = 1;
    
    /** The current alphabet. */
    protected Word[] alphabet;
    
    /** Creates a new instance of EventCodec */
    public EventCodec() {
    }
    
    public Word getWord(int code) {
        return alphabet[code];
    }
    
    public String getString(int code) {
        return getWord(code).getUCS2();
    }
    
    public int[] getUCS4(int code) {
        return getWord(code).getUCS4();
    }
    
    public int createCode() {
        return nextLiteral++;
    }
    
    protected synchronized void ensureCapacity(int code) {
        int size = alphabet == null ? 0 : alphabet.length;
        if (size <= code) {
            int more = Math.max(code + 1, (size + size >> 2));
            Word[] grow = new Word[more];
            if (size > 0) {
                System.arraycopy(alphabet, 0, grow, 0, size);
            }
            alphabet = grow;
        }
    }
    
    public static int[] toUCS4(String ucs2) {
        int in = 0, out = 0, end = ucs2.length();
        int buffer[] = new int[end];
        while (in < end) {
            int datum = ucs2.charAt(in++);
            if ((datum & 0xf800) == 0xd800) {
                if (datum > 0xdbff) {
                    throw new IllegalArgumentException
                        ("invalid surrogate high-half: "+datum);
                } else if (in >= end) {
                    throw new IllegalArgumentException
                        ("truncated surrogate");
                }
                int high = (datum & 0x3ff) << 10;
                datum = ucs2.charAt(in++);
                if (datum < 0xdc00 || datum > 0xdfff) {
                    throw new IllegalArgumentException
                        ("invalid surrogate low-half: "+datum);
                }
                datum = (high | (datum & 0x3ff)) + 0x10000;
            }
            buffer[out++] = datum;
        }
        if (out < buffer.length) {
            int shrink[] = new int[out];
            if (shrink.length > 0) {
                System.arraycopy(buffer, 0, shrink, 0, shrink.length);
            }
            buffer = shrink;
        }
        return buffer;
    }
    
    public static final int hashCode(int ucs4[]) {
        return hashCode(ucs4, 0, ucs4.length);
    }
    
    public static final int hashCode(int ucs4[], int off, int len) {
        int result = len;
        int scan = off + len;
        while (--scan >= off) {
            result = result * 37 + ucs4[scan];
        }
        return result;
    }
    
    public static class Word {
        protected int code;
        protected int ucs4[];
        protected String ucs2;
        protected int hashCache;
        
        public Word(int code, int ucs4[]) {
            this.code = code;
            this.ucs4 = ucs4;
            this.hashCache = EventEncoder.hashCode(this.ucs4);
        }
        
        public Word(int code, int ucs4[], int off, int len, String ucs2) {
            if (len < ucs4.length || off > 0) {
                int shrink[] = new int[len];
                if (len > 0) {
                    System.arraycopy(ucs4, off, shrink, 0, len);
                }
                ucs4 = shrink;
            }
            this.code = code;
            this.ucs4 = ucs4;
            this.ucs2 = ucs2;
            this.hashCache = EventEncoder.hashCode(this.ucs4);
        }
        
        public Word(int code, String ucs2) {
            this.code = code;
            this.ucs4 = EventEncoder.toUCS4(ucs2);
            this.ucs2 = ucs2;
            this.hashCache = EventEncoder.hashCode(this.ucs4);
        }
        
        public int getCode() {
            return this.code;
        }
        
        public int[] getUCS4() {
            return this.ucs4;
        }
        
        public String getUCS2() {
            if (this.ucs2 != null) {
                return this.ucs2;
            }
            synchronized (this) {
                if (this.ucs2 != null) {
                    return this.ucs2;
                }
                StringBuffer buffer = new StringBuffer(ucs4.length);
                int in = 0, end = ucs4.length;
                int ucs4[] = this.ucs4;
                while (in < end) {
                    int value = ucs4[in++]; // never less than zero
                    if (value <= 0xffff) {
                        buffer.append((char)value);
                    } else if (value <= 0x10ffff) {
                        value -= 0x10000;
                        buffer.append((char)(0xd800 | (value >> 10)));
                        buffer.append((char)(0xdc00 | (value & 0x3ff)));
                    } else {
                        throw new IllegalArgumentException
                            ("character value out of range: "+value);
                    }
                }
                return this.ucs2 = buffer.toString();
            }
        }
        
        public String toString() {
            return getUCS2();
        }
        
        public boolean equals(Object other) {
            return (other instanceof Word) &&
                Arrays.equals(getUCS4(), ((Word)other).getUCS4());
        }
        
        public boolean equals(int other[], int off, int len) {
            int ucs4[] = getUCS4();
            if (ucs4.length != len) {
                return false;
            }
            int scan = 0;
            while (--len >= 0) {
                if (ucs4[scan++] != other[off++]) {
                    return false;
                }
            }
            return true;
        }
        
        public int hashCode() {
            return hashCache;
        }
    }
    
    public static void main(String args[]) {
        try {
            EventEncoder encoder = new EventEncoder();
            EventDecoder decoder = new EventDecoder();
            java.io.BufferedReader in = new java.io.BufferedReader
                (new java.io.InputStreamReader(System.in));
            String line;
            while ((line = in.readLine()) != null) {
                encoder.encodeWord(line);
                byte buffer[] = encoder.getUTF8();
                print("buffer", buffer, System.out);
                decoder.setBuffer(buffer);
                while (decoder.hasNext()) {
                    int code = decoder.next();
                    System.out.println("code: "+code+" value='"+decoder.getString(code)+"'");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void print(String name, byte[] data, java.io.PrintStream out) {
        out.print(name);
        out.print(": [");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) out.print(", 0x");
            else out.print("0x");
            out.print(Integer.toString((int)data[i], 16));
        }
        out.println(']');
    }
    
}
