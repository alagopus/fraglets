/*
 * TGADecode.java
 * Copyright (C) 2002 Klaus Rennecke.
 * Created on December 1, 2002, 10:22 AM
 */

package net.sourceforge.fraglets.targa;

import java.awt.image.DirectColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decoder class for reading TGA (TARGA) image files. Only true color
 * image types are supported (codes 2 and 10).
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * @author  marion@users.sourceforge.net
 */
public class TGADecoder {
    
    public static MemoryImageSource decode(InputStream in)
        throws IOException
    {
        return decode(new LEInputStream(in));
    }
    
    public static MemoryImageSource decode(LEInputStream in)
        throws IOException
    {
        int idLength = in.readByte() & 0xff;
        in.skipBytes(1); // byte colorMapType = in.readByte();
        byte imageType = in.readByte();
        
        in.skipBytes(2); // int colorMapOffset = in.readShort() & 0xffff;
        int colorMapLength = in.readShort() & 0xffff;
        int colorMapDepth = in.readByte() & 0xff;
        
        in.skipBytes(4); // ignore origin
//        int xOrigin = in.readShort() & 0xffff;
//        int yOrigin = in.readShort() & 0xffff;
        int width = in.readShort() & 0xffff;
        int height = in.readShort() & 0xffff;
        int depth = in.readByte() & 0xff;
        byte descriptor = in.readByte();
        
        in.skipBytes(idLength); // ignore image id
//        byte imageId[];
//        if (idLength > 0) {
//            imageId = new byte[idLength];
//            in.readFully(imageId);
//        } else {
//            imageId = null;
//        }
        
        int colorMapSize = colorMapLength *
            Math.min((colorMapDepth + 7) / 8, 3);
        in.skipBytes(colorMapSize); // ignore color map
//        byte colorMap[];
//        if (idLength > 0) {
//            colorMap = new byte[idLength];
//            in.readFully(colorMap);
//        } else {
//            colorMap = null;
//        }
        int aMask = 0, rMask, gMask, bMask;
        switch(depth) {
            case 32: aMask = 0x000000ff;
            case 24: rMask = 0x0000ff00; gMask = 0x00ff0000; bMask = 0xff000000; break;
            case 16: aMask = 0x0001;
            case 15: rMask = 0x003e; gMask = 0x07c0; bMask = 0xf800; break;
            case 12: aMask = 0x003;
            case 9: rMask = 0x038; gMask = 0x1c0; bMask = 0xe00; break;
            case 8: aMask = 0x03;
            case 6: rMask = 0x0c; gMask = 0x30; bMask = 0xc0; break;
            case 4: aMask = 0x01;
            case 3: rMask = 0x02; gMask = 0x04; bMask = 0x08; break;
            default:
                throw new IOException("unrecognized image depth: "+depth);
        }
        int bpp = (depth + 7) / 8;
        byte data[] = new byte[width * height * bpp];
        if (imageType == 2) {
            in.readFully(data);
        } else if (imageType == 10) {
            in.readRLE(bpp, data);
        } else {
            throw new IOException("unsupported image type: "+imageType);
        }
        switch ((descriptor >> 4) & 0x03) {
            case 0:
                swapV(data, width * bpp);
                break;
            case 1:
                swapD(data, width, bpp);
                break;
            case 2:
                break;
            case 3:
                swapH(data, width, bpp);
        }
        DirectColorModel cm = new DirectColorModel(depth, rMask, gMask, bMask, aMask);
        if (bpp > 1) {
            int b = bpp;
            int pix[] = new int[data.length / bpp];
            while (--b >= 0) {
                int shift = (3 - b) * 8;
                int i1 = data.length + b, i2 = data.length / bpp;
                while ((i1 -= bpp) >= 0) {
                    pix[--i2] |= (data[i1] & 0xff) << shift;
                }
            }
            return new MemoryImageSource(width, height, cm, pix, 0, width);
        } else {
            return new MemoryImageSource(width, height, cm, data, 0, width * bpp);
        }
    }
    
    public static void swapD(byte data[], int width, int bpp) {
        swapH(data, width, bpp);
        swapV(data, width * bpp);
    }
    
    public static void swapH(byte data[], int width, int bpp) {
        int height = data.length / (width * bpp);
        int step = width * bpp;
        int b = bpp;
        while (--b >= 0) {
            int l = b, h = b + (width - 1) * bpp;
            while (l < h) {
                int scan = height * step;
                while ((scan -= step) >= 0) {
                    byte t = data[l + scan];
                    data[l + scan] = data[h + scan];
                    data[h + scan] = t;
                }
                l += bpp;
                h -= bpp;
            }
        }
    }
    
    public static void swapV(byte data[], int width) {
        int height = data.length / width;
        int l = 0, h = height - 1;
        while (l < h) {
            int o0 = l * width;
            int o1 = h * width;
            for (int i = 0; i < width; i++) {
                byte t = data[o0 + i];
                data[o0 + i] = data[o1 + i];
                data[o1 + i] = t;
            }
            l += 1;
            h -= 1;
        }
    }
    
    /** This class implements a DataInputStream reading integer values in
     * little endian byte order. */
    public static class LEInputStream extends BufferedInputStream implements DataInput
    {
        public LEInputStream(InputStream in)
        {
            super(in);
        }
        
        /** Reads one input byte and returns
         * <code>true</code> if that byte is nonzero,
         * <code>false</code> if that byte is zero.
         * This method is suitable for reading
         * the byte written by the <code>writeBoolean</code>
         * method of interface <code>DataOutput</code>.
         *
         * @return     the <code>boolean</code> value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public boolean readBoolean() throws IOException {
            int c = super.read();
            if (c == -1) {
                throw new EOFException();
            }
            return c != 0;
        }
        
        /** Reads and returns one input byte.
         * The byte is treated as a signed value in
         * the range <code>-128</code> through <code>127</code>,
         * inclusive.
         * This method is suitable for
         * reading the byte written by the <code>writeByte</code>
         * method of interface <code>DataOutput</code>.
         *
         * @return     the 8-bit value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public byte readByte() throws IOException {
            int c = super.read();
            if (c == -1) {
                throw new EOFException();
            }
            return (byte)c;
        }
        
        /** Reads an input <code>char</code> and returns the <code>char</code> value.
         * A Unicode <code>char</code> is made up of two bytes.
         * Let <code>a</code>
         * be the first byte read and <code>b</code>
         * be the second byte. The value
         * returned is:
         * <p><pre><code>(char)((a &lt;&lt; 8) | (b &amp; 0xff))
         * </code></pre>
         * This method
         * is suitable for reading bytes written by
         * the <code>writeChar</code> method of interface
         * <code>DataOutput</code>.
         *
         * @return     the Unicode <code>char</code> read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public char readChar() throws IOException {
            int c1, c2;
            if ((c1 = super.read()) == -1) {
                throw new EOFException();
            }
            if ((c2 = super.read()) == -1) {
                throw new EOFException();
            }
            return (char)((c1 << 8) | (c2 & 0xff));
        }
        
        /** Reads eight input bytes and returns
         * a <code>double</code> value. It does this
         * by first constructing a <code>long</code>
         * value in exactly the manner
         * of the <code>readlong</code>
         * method, then converting this <code>long</code>
         * value to a <code>double</code> in exactly
         * the manner of the method <code>Double.longBitsToDouble</code>.
         * This method is suitable for reading
         * bytes written by the <code>writeDouble</code>
         * method of interface <code>DataOutput</code>.
         *
         * @return     the <code>double</code> value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readLong());
        }
        
        /** Reads four input bytes and returns
         * a <code>float</code> value. It does this
         * by first constructing an <code>int</code>
         * value in exactly the manner
         * of the <code>readInt</code>
         * method, then converting this <code>int</code>
         * value to a <code>float</code> in
         * exactly the manner of the method <code>Float.intBitsToFloat</code>.
         * This method is suitable for reading
         * bytes written by the <code>writeFloat</code>
         * method of interface <code>DataOutput</code>.
         *
         * @return     the <code>float</code> value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readInt());
        }
        
        /** Reads some bytes from an input
         * stream and stores them into the buffer
         * array <code>b</code>. The number of bytes
         * read is equal
         * to the length of <code>b</code>.
         * <p>
         * This method blocks until one of the
         * following conditions occurs:<p>
         * <ul>
         * <li><code>b.length</code>
         * bytes of input data are available, in which
         * case a normal return is made.
         *
         * <li>End of
         * file is detected, in which case an <code>EOFException</code>
         * is thrown.
         *
         * <li>An I/O error occurs, in
         * which case an <code>IOException</code> other
         * than <code>EOFException</code> is thrown.
         * </ul>
         * <p>
         * If <code>b</code> is <code>null</code>,
         * a <code>NullPointerException</code> is thrown.
         * If <code>b.length</code> is zero, then
         * no bytes are read. Otherwise, the first
         * byte read is stored into element <code>b[0]</code>,
         * the next one into <code>b[1]</code>, and
         * so on.
         * If an exception is thrown from
         * this method, then it may be that some but
         * not all bytes of <code>b</code> have been
         * updated with data from the input stream.
         *
         * @param     b   the buffer into which the data is read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public void readFully(byte[] b) throws IOException {
            readFully(b, 0, b.length);
        }
        
        /**
         * Reads <code>len</code>
         * bytes from
         * an input stream.
         * <p>
         * This method
         * blocks until one of the following conditions
         * occurs:<p>
         * <ul>
         * <li><code>len</code> bytes
         * of input data are available, in which case
         * a normal return is made.
         *
         * <li>End of file
         * is detected, in which case an <code>EOFException</code>
         * is thrown.
         *
         * <li>An I/O error occurs, in
         * which case an <code>IOException</code> other
         * than <code>EOFException</code> is thrown.
         * </ul>
         * <p>
         * If <code>b</code> is <code>null</code>,
         * a <code>NullPointerException</code> is thrown.
         * If <code>off</code> is negative, or <code>len</code>
         * is negative, or <code>off+len</code> is
         * greater than the length of the array <code>b</code>,
         * then an <code>IndexOutOfBoundsException</code>
         * is thrown.
         * If <code>len</code> is zero,
         * then no bytes are read. Otherwise, the first
         * byte read is stored into element <code>b[off]</code>,
         * the next one into <code>b[off+1]</code>,
         * and so on. The number of bytes read is,
         * at most, equal to <code>len</code>.
         *
         * @param     b   the buffer into which the data is read.
         * @param off  an int specifying the offset into the data.
         * @param len  an int specifying the number of bytes to read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public void readFully(byte[] b, int off, int len) throws IOException {
            while (len > 0) {
                int n = super.read(b, off, len);
                if (n == -1) {
                    throw new EOFException();
                }
                off += n;
                len -= n;
            }
        }
        
        public void readRLE(int bpp, byte b[]) throws IOException {
            readRLE(bpp, b, 0, b.length);
        }
        
        public void readRLE(int bpp, byte b[], int off, int len) throws IOException {
            int end = off + len;
            while (off < end) {
                int c = super.read();
                if (c == -1) {
                    throw new EOFException();
                }
                len = ((c & 0x7f) + 1) * bpp;
                if (len + off > end) {
                    throw new IOException("packet extends over end of buffer");
                }
                if ((c & 0x80) == 0) {
                    // raw
                    readFully(b, off, len);
                    off += len;
                } else {
                    // repeat
                    for (int i = 0; i < bpp; i++) {
                        c = super.read();
                        if (c == -1) {
                            throw new EOFException();
                        }
                        byte v = (byte)c;
                        int scan = off + len + i;
                        while ((scan -= bpp) >= off) {
                            b[scan] = v;
                        }
                    }
                    off += len;
                }
            }
        }
        
        /** Reads four input bytes and returns an
         * <code>int</code> value. Let <code>a</code>
         * be the first byte read, <code>b</code> be
         * the second byte, <code>c</code> be the third
         * byte,
         * and <code>d</code> be the fourth
         * byte. The value returned is:
         * <p><pre>
         * <code>
         * (((a &amp; 0xff) &lt;&lt; 24) | ((b &amp; 0xff) &lt;&lt; 16) |
         * &#32;((c &amp; 0xff) &lt;&lt; 8) | (d &amp; 0xff))
         * </code></pre>
         * This method is suitable
         * for reading bytes written by the <code>writeInt</code>
         * method of interface <code>DataOutput</code>.
         *
         * @return     the <code>int</code> value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public int readInt() throws IOException {
            int i1 = readUnsignedShort();
            int i2 = readUnsignedShort();
            return (i2 << 16) | i1;
        }
        
        /** Reads the next line of text from the input stream.
         * It reads successive bytes, converting
         * each byte separately into a character,
         * until it encounters a line terminator or
         * end of
         * file; the characters read are then
         * returned as a <code>String</code>. Note
         * that because this
         * method processes bytes,
         * it does not support input of the full Unicode
         * character set.
         * <p>
         * If end of file is encountered
         * before even one byte can be read, then <code>null</code>
         * is returned. Otherwise, each byte that is
         * read is converted to type <code>char</code>
         * by zero-extension. If the character <code>'\n'</code>
         * is encountered, it is discarded and reading
         * ceases. If the character <code>'\r'</code>
         * is encountered, it is discarded and, if
         * the following byte converts &#32;to the
         * character <code>'\n'</code>, then that is
         * discarded also; reading then ceases. If
         * end of file is encountered before either
         * of the characters <code>'\n'</code> and
         * <code>'\r'</code> is encountered, reading
         * ceases. Once reading has ceased, a <code>String</code>
         * is returned that contains all the characters
         * read and not discarded, taken in order.
         * Note that every character in this string
         * will have a value less than <code>&#92;u0100</code>,
         * that is, <code>(char)256</code>.
         *
         * @return the next line of text from the input stream,
         *         or <CODE>null</CODE> if the end of file is
         *         encountered before a byte can be read.
         * @exception  IOException  if an I/O error occurs.
         *
         */
        public String readLine() throws IOException {
            StringBuffer buffer = null;
            int c;
            while ((c = super.read()) != -1) {
                if (buffer == null) {
                    buffer = new StringBuffer();
                }
                if (c == '\n') {
                    break;
                } else if (c == '\r') {
                    super.mark(1);
                    if (super.read() != '\n') {
                        super.reset();
                    }
                    break;
                } else {
                    buffer.append((char)c);
                }
            }
            return buffer == null ? null : buffer.toString();
        }
        
        /** Reads eight input bytes and returns
         * a <code>long</code> value. Let <code>a</code>
         * be the first byte read, <code>b</code> be
         * the second byte, <code>c</code> be the third
         * byte, <code>d</code>
         * be the fourth byte,
         * <code>e</code> be the fifth byte, <code>f</code>
         * be the sixth byte, <code>g</code> be the
         * seventh byte,
         * and <code>h</code> be the
         * eighth byte. The value returned is:
         * <p><pre> <code>
         * (((long)(a &amp; 0xff) &lt;&lt; 56) |
         *  ((long)(b &amp; 0xff) &lt;&lt; 48) |
         *  ((long)(c &amp; 0xff) &lt;&lt; 40) |
         *  ((long)(d &amp; 0xff) &lt;&lt; 32) |
         *  ((long)(e &amp; 0xff) &lt;&lt; 24) |
         *  ((long)(f &amp; 0xff) &lt;&lt; 16) |
         *  ((long)(g &amp; 0xff) &lt;&lt;  8) |
         *  ((long)(h &amp; 0xff)))
         * </code></pre>
         * <p>
         * This method is suitable
         * for reading bytes written by the <code>writeLong</code>
         * method of interface <code>DataOutput</code>.
         *
         * @return     the <code>long</code> value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public long readLong() throws IOException {
            int i1 = readInt();
            int i2 = readInt();
            return ((long)i2 << 32) | ((long)i1 & 0xffffffffL);
        }
        
        /** Reads in a string that has been encoded using a modified UTF-8 format.
         * The general contract of <code>readUTF</code>
         * is that it reads a representation of a Unicode
         * character string encoded in Java modified
         * UTF-8 format; this string of characters
         * is then returned as a <code>String</code>.
         * <p>
         * First, two bytes are read and used to
         * construct an unsigned 16-bit integer in
         * exactly the manner of the <code>readUnsignedShort</code>
         * method . This integer value is called the
         * <i>UTF length</i> and specifies the number
         * of additional bytes to be read. These bytes
         * are then converted to characters by considering
         * them in groups. The length of each group
         * is computed from the value of the first
         * byte of the group. The byte following a
         * group, if any, is the first byte of the
         * next group.
         * <p>
         * If the first byte of a group
         * matches the bit pattern <code>0xxxxxxx</code>
         * (where <code>x</code> means "may be <code>0</code>
         * or <code>1</code>"), then the group consists
         * of just that byte. The byte is zero-extended
         * to form a character.
         * <p>
         * If the first byte
         * of a group matches the bit pattern <code>110xxxxx</code>,
         * then the group consists of that byte <code>a</code>
         * and a second byte <code>b</code>. If there
         * is no byte <code>b</code> (because byte
         * <code>a</code> was the last of the bytes
         * to be read), or if byte <code>b</code> does
         * not match the bit pattern <code>10xxxxxx</code>,
         * then a <code>UTFDataFormatException</code>
         * is thrown. Otherwise, the group is converted
         * to the character:<p>
         * <pre><code>(char)(((a&amp; 0x1F) &lt;&lt; 6) | (b &amp; 0x3F))
         * </code></pre>
         * If the first byte of a group
         * matches the bit pattern <code>1110xxxx</code>,
         * then the group consists of that byte <code>a</code>
         * and two more bytes <code>b</code> and <code>c</code>.
         * If there is no byte <code>c</code> (because
         * byte <code>a</code> was one of the last
         * two of the bytes to be read), or either
         * byte <code>b</code> or byte <code>c</code>
         * does not match the bit pattern <code>10xxxxxx</code>,
         * then a <code>UTFDataFormatException</code>
         * is thrown. Otherwise, the group is converted
         * to the character:<p>
         * <pre><code>
         * (char)(((a &amp; 0x0F) &lt;&lt; 12) | ((b &amp; 0x3F) &lt;&lt; 6) | (c &amp; 0x3F))
         * </code></pre>
         * If the first byte of a group matches the
         * pattern <code>1111xxxx</code> or the pattern
         * <code>10xxxxxx</code>, then a <code>UTFDataFormatException</code>
         * is thrown.
         * <p>
         * If end of file is encountered
         * at any time during this entire process,
         * then an <code>EOFException</code> is thrown.
         * <p>
         * After every group has been converted to
         * a character by this process, the characters
         * are gathered, in the same order in which
         * their corresponding groups were read from
         * the input stream, to form a <code>String</code>,
         * which is returned.
         * <p>
         * The <code>writeUTF</code>
         * method of interface <code>DataOutput</code>
         * may be used to write data that is suitable
         * for reading by this method.
         * @return     a Unicode string.
         * @exception  EOFException            if this stream reaches the end
         *               before reading all the bytes.
         * @exception  IOException             if an I/O error occurs.
         * @exception  UTFDataFormatException  if the bytes do not represent a
         *               valid UTF-8 encoding of a string.
         *
         */
        public String readUTF() throws IOException {
            return DataInputStream.readUTF(this);
        }
        
        /** Reads one input byte, zero-extends
         * it to type <code>int</code>, and returns
         * the result, which is therefore in the range
         * <code>0</code>
         * through <code>255</code>.
         * This method is suitable for reading
         * the byte written by the <code>writeByte</code>
         * method of interface <code>DataOutput</code>
         * if the argument to <code>writeByte</code>
         * was intended to be a value in the range
         * <code>0</code> through <code>255</code>.
         *
         * @return     the unsigned 8-bit value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public int readUnsignedByte() throws IOException {
            int c1;
            if ((c1 = super.read()) == -1) {
                throw new EOFException();
            }
            return c1 & 0xff;
        }
        
        /** Reads two input bytes and returns
         * an <code>int</code> value in the range <code>0</code>
         * through <code>65535</code>. Let <code>a</code>
         * be the first byte read and
         * <code>b</code>
         * be the second byte. The value returned is:
         * <p><pre><code>(((a &amp; 0xff) &lt;&lt; 8) | (b &amp; 0xff))
         * </code></pre>
         * This method is suitable for reading the bytes
         * written by the <code>writeShort</code> method
         * of interface <code>DataOutput</code>  if
         * the argument to <code>writeShort</code>
         * was intended to be a value in the range
         * <code>0</code> through <code>65535</code>.
         *
         * @return     the unsigned 16-bit value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public int readUnsignedShort() throws IOException {
            return readShort() & 0xffff;
        }
        
        /** Makes an attempt to skip over
         * <code>n</code> bytes
         * of data from the input
         * stream, discarding the skipped bytes. However,
         * it may skip
         * over some smaller number of
         * bytes, possibly zero. This may result from
         * any of a
         * number of conditions; reaching
         * end of file before <code>n</code> bytes
         * have been skipped is
         * only one possibility.
         * This method never throws an <code>EOFException</code>.
         * The actual
         * number of bytes skipped is returned.
         *
         * @param      n   the number of bytes to be skipped.
         * @return     the number of bytes actually skipped.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public int skipBytes(int n) throws IOException {
            return (int)super.skip(n);
        }
        
        /** Reads two input bytes and returns
         * a <code>short</code> value. Let <code>a</code>
         * be the first byte read and <code>b</code>
         * be the second byte. The value
         * returned
         * is:
         * <p><pre><code>(short)((a &lt;&lt; 8) * | (b &amp; 0xff))
         * </code></pre>
         * This method
         * is suitable for reading the bytes written
         * by the <code>writeShort</code> method of
         * interface <code>DataOutput</code>.
         *
         * @return     the 16-bit value read.
         * @exception  EOFException  if this stream reaches the end before reading
         *               all the bytes.
         * @exception  IOException   if an I/O error occurs.
         *
         */
        public short readShort() throws IOException {
            int c1, c2;
            if ((c1 = super.read()) == -1) {
                throw new EOFException();
            }
            if ((c2 = super.read()) == -1) {
                throw new EOFException();
            }
            return (short)((c2 << 8) | (c1 & 0xff));
        }
        
    }
}
