/*
 * Gobbler.java - Binary file filter example.
 * Copyright (C) 2000 Klaus Rennecke.
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
package net.sourceforge.fraglets.gobble;

/**
 * Binary file filter example.
 * <br>This is only a testing and timing example.
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class Gobbler {
    
    /** Creates a new instance of Gobbler */
    private Gobbler() {
    }
    
    /**
     * Gobble up an entire stream.
     * @param in The input stream to read.
     * @param skip The byte code to skip.
     * @throws IOException propagated from i/o methods
     * @return the stream contents, minus the skip bytes
     */
    public static byte[] gobble(java.io.InputStream in, byte skip) throws java.io.IOException {
        int n;
        int offset = 0;
        final int block = 8192;
        byte buffer[] = new byte[Math.max(block, in.available())];
        while ((n = in.read(buffer, offset, buffer.length - offset)) > 0) {
            for (; n > 0 && buffer[offset] != skip; n--, offset++) {
                // advance over initial clean chunk, nothing to do here
            }
            for (int i = offset; n > 0; n--, i++) {
                // copy down the other chunks, dropping the skipped bytes
                byte b = buffer[i];
                if (b != skip) {
                    buffer[offset++] = b;
                }
            }
            if (buffer.length - offset < block) {
                // ensure we read at least with block size, reallocate
                byte grow[] = new byte[buffer.length
                + Math.max(block, buffer.length / 2)];
                System.arraycopy(buffer, 0, grow, 0, offset);
                buffer = grow;
            }
        }
        if (offset < buffer.length) {
            // shrink the buffer so it matches the result size
            byte shrink[] = new byte[offset];
            System.arraycopy(buffer, 0, shrink, 0, offset);
            buffer = shrink;
        }
        return buffer;
    }
    
    /**
     * <p>Filter carriage-returns from a file, and display the result size.</p>
     * <p>Usage: <code>Gobbler <var>filename</var></code>.</p>
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try{
            byte skip = (byte)'\r';
            for (int i = 0; i < 2; i++) {
                // HotSpot warmup
                java.io.InputStream in = new java.io.FileInputStream(args[0]);
                try {
                    gobble(in, skip);
                } finally {
                    in.close();
                }
            }
            // test run
            java.io.InputStream in = new java.io.FileInputStream(args[0]);
            try {
                long now = System.currentTimeMillis();
                System.out.println(gobble(in, skip).length);
                System.out.println("time: "+(System.currentTimeMillis()-now));
            } finally {
                in.close();
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
