/*
 * UTF8Decoder.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 04:06
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

/**
 * An UTF-8 decoder able to decode UCS-4 in addition to UCS-2. See
 * standards ISO/IEC 10646-1:1993 and RFC2279, RFC2781.
 * 
 * This implementation requires that you know in advance how many
 * characters you will decode from a given input. You may try to
 * use it catching ArrayIndexOutOfBoundsException to detect the end
 * if input. However, doing that may discard up to 5 partial bytes
 * at the end of the input.
 * 
 * Format checking is minimal in favor of performance. If you feed
 * it garbage, you will get garbage output.
 * 
 * @author  marion@users.sourceforge.net
 * @version $Revision: 1.6 $
 */
public class UTF8Decoder {
	/** Input buffer. */
    private byte buffer[];
    
    /** Offset into buffer. */
    private int off;
    
    /** End of buffer. */
    private int end;
    
    /** Byte value table. */
    private static int value[];
    
    /** Code length table. */
    private static int length[];
    
    /** Creates a new instance of UTF8Decoder */
    public UTF8Decoder() {
        initTables();
    }

	/**
	 * Set the current input buffer. Note that the buffer is
	 * used in-place and no copy is created to decode it.
	 * 
	 * @param buffer the new input buffer
	 */
    public void setBuffer(byte buffer[]) {
        setBuffer(buffer, 0, buffer.length);
    }
    
	/**
	 * Set the current input buffer. Note that the buffer is
	 * used in-place and no copy is created to decode it.
	 * 
	 * @param buffer the new input buffer
	 * @param off where to start in buffer
	 * @param len how many bytes to use from buffer
	 */
    public void setBuffer(byte buffer[], int off, int len) {
        this.buffer = buffer;
        this.off = off;
        this.end = off + len;
    }
    
	/**
	 * Compute how many bytes are still in the input buffer.
	 * Note that this is always at least as much as the number
	 * of UCS-4 encoded characters available, usually more.
	 * 
	 * @return number of bytes still available
	 */
    public int available() {
        return end - off;
    }
    
	/**
	 * Decode a UCS-4 character string.
	 * 
	 * @param buffer the buffer to hold the result, or null
	 * @param off where to start in buffer, ignored when buffer is null
	 * @param len number of characters to decode
	 * @return the decoded characters as an array of integers
	 * @throws ArrayIndexOutOfBoundsException when input or output
	 * buffers are exhausted before len characters are decoded
	 */
    public int[] decodeUCS4(int buffer[], int off, int len)
    throws ArrayIndexOutOfBoundsException {
        if (buffer == null) {
            buffer = new int[len];
            off = 0; // ignored
        }
        while (--len >= 0) {
            buffer[off++] = decodeUCS4();
        }
        return buffer;
    }
    
	/**
	 * Decode a UCS-2 character string. This will convert UCS-4
	 * characters to surrogate pairs when needed. Note that this
	 * means that you may have to provide more room in the output
	 * buffer: two characters for each surrogate pair instead of
	 * one.
	 * 
	 * @param buffer the buffer to hold the result, or null
	 * @param off where to start in buffer, ignored when buffer is null
	 * @param len number of UCS-2 characters to decode, including
	 * surrogate pairs
	 * @return the decoded characters as an array of characters
	 * @throws ArrayIndexOutOfBoundsException when input or output
	 * buffers are exhausted before len characters are decoded
	 */
    public char[] decodeUCS2(char buffer[], int off, int len)
    throws ArrayIndexOutOfBoundsException {
        if (buffer == null) {
            buffer = new char[len];
            off = 0; // ignored
        }
        while (--len >= 0) {
            int value = decodeUCS4(); // never less than zero
            if (value <= 0xffff) {
                buffer[off++] = (char)value;
            } else if (value <= 0x10ffff) {
                if (--len < 0) {
                    throw new IllegalStateException
                        ("no room for UTF-16 encoded character: "+value);
                }
                value -= 0x10000;
                buffer[off++] = (char)(0xd800 | (value >> 10));
                buffer[off++] = (char)(0xdc00 | (value & 0x3ff));
            } else {
                throw new IllegalArgumentException
                    ("character value out of range: "+value);
            }
        }
        return buffer;
    }
    
	/**
	 * Decode a UCS-2 encoded string.
	 *
	 * @param len number of UCS-2 characters to decode, including
	 * surrogate pairs
	 * @return the decoded characters as a String
	 * @throws ArrayIndexOutOfBoundsException when the input
	 * buffer is exhausted before len characters are decoded
	 */
    public String decodeString(int len)
    throws ArrayIndexOutOfBoundsException {
        return new String(decodeUCS2(null, 0, len));
    }
    
	/**
	 * Decode a UCS-4 character.
	 * 
	 * @return the decoded character as an integer
	 * @throws ArrayIndexOutOfBoundsException when the input
	 * buffer is exhausted
	 */
    public final int decodeUCS4()
    throws ArrayIndexOutOfBoundsException {
    	try {
	        int datum = buffer[off++] & 0xff;
	        int result = value[datum];
	        
	        // this is a bit reckless, ignoring the byte tags
	        // for continued values but it's quite fast that way
	        switch (length[datum]) {
	            case 6:
	                result = result << 6 | (buffer[off++] & 0x3f); 
	            case 5:
	                result = result << 6 | (buffer[off++] & 0x3f); 
	            case 4:
	                result = result << 6 | (buffer[off++] & 0x3f); 
	            case 3:
	                result = result << 6 | (buffer[off++] & 0x3f); 
	            case 2:
	                result = result << 6 | (buffer[off++] & 0x3f); 
	            case 1:
	                return result;
	            default:
	                throw new IllegalArgumentException
	                    ("format error: illegal byte: "+datum);
	        }
    	} catch (ArrayIndexOutOfBoundsException ex) {
    		off = end; // reset invalid offset
    		throw ex;
    	} finally {
    		// detect buffer underrun
    		if (off > end) {
    			ArrayIndexOutOfBoundsException ex =
    				new ArrayIndexOutOfBoundsException(off);
    			off = end; // reset invalid offset
    			throw ex;
    		}
    	}
    }
    
	/**
	 * Initialize decoder tables. Both tables are used to interpret
	 * the first byte of an encoded character.
	 * 
	 * The <code>length</code> table determines how many bytes make
	 * up the character. If the first byte is invalid,
	 * <code>length[byte]</code> is zero.
	 * 
	 * The <code>value</code> table contains the partial value of the
	 * first byte, shifted down by <code>length[byte] * 6 - 1</code>.
	 */
    private static void initTables() {
        if (length != null) {
            return;
        }
        synchronized (UTF8Decoder.class) {
            if (length != null) {
                return;
            }
            length = new int[256];
            value = new int[256];
            for (int i = 0; i <= 0x7f; i++) {
                length[i] = 1;
                value[i] = i;
            }
            for (int i = 0x80; i < 0xc0; i++) {
                length[i] = 0; // invalid
                value[i] = 0;
            }
            for (int i = 0xc0; i <= 0xdf; i++) {
                length[i] = 2;
                value[i] = i & 0x1f;
            }
            for (int i = 0xe0; i <= 0xef; i++) {
                length[i] = 3;
                value[i] = i & 0xf;
            }
            for (int i = 0xf0; i <= 0xf7; i++) {
                length[i] = 4;
                value[i] = i & 0x7;
            }
            for (int i = 0xf8; i <= 0xfb; i++) {
                length[i] = 5;
                value[i] = i & 0x3;
            }
            for (int i = 0xfc; i <= 0xfd; i++) {
                length[i] = 6;
                value[i] = i & 0x1;
            }
            for (int i = 0xfe; i <= 0xff; i++) {
                length[i] = 0; // invalid
                value[i] = 0;
            }
        }
    }
}
