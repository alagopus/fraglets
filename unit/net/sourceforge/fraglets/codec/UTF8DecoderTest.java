/*
 * UTF8DecoderTest.java
 * Copyright (C) 2002, Klaus Rennecke.
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

package net.sourceforge.fraglets.codec;

import junit.framework.*;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.6 $
 */
public class UTF8DecoderTest extends TestCase {
    
    public UTF8DecoderTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Test of setBuffer method, of class net.sourceforge.fraglets.codec.UTF8Decoder. */
    public void testSetBuffer() throws java.io.UnsupportedEncodingException {
        String sample = "foo";
        UTF8Decoder decoder = new UTF8Decoder();
        byte[] testBuffer = sample.getBytes("UTF-8");
        decoder.setBuffer(testBuffer);
        String result = decoder.decodeString(sample.length());
        assertEquals("set buffer failed", sample, result);
    }
    
    /** Test of decodeUCS4 method, of class net.sourceforge.fraglets.codec.UTF8Decoder. */
    public void testDecodeUCS4() {
        int count = 1000;
        int sample[] = new int[count];
        int check[] = new int[count];
        byte test[] = null;
        java.util.Random random = new java.util.Random(count);
        UTF8Encoder encoder = new UTF8Encoder();
        UTF8Decoder decoder = new UTF8Decoder();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                // this does actually not include max value, but
                // Random cannot provide it.
                sample[j] = random.nextInt(0x7fffffff);
            }
            encoder.clear();
            encoder.encodeUCS4(sample, 0, sample.length);
            test = encoder.toByteArray();
            decoder.setBuffer(test);
            check = decoder.decodeUCS4(check, 0, check.length);
            if (sample.length != check.length) {
                assertEquals("result length", test.length, check.length);
            } else {
                int scan = sample.length;
                while (--scan >= 0) {
                    assertEquals("result data", sample[scan], check[scan]);
                }
            }
        }
    }
    
    /** Test of decodeUCS2 method, of class net.sourceforge.fraglets.codec.UTF8Decoder. */
    public void testDecodeUCS2() throws java.io.UnsupportedEncodingException {
        int count = 1000;
        char sample[] = new char[count];
        char check[] = new char[count];
        byte test[] = null;
        java.util.Random random = new java.util.Random(count);
        UTF8Encoder encoder = new UTF8Encoder();
        UTF8Decoder decoder = new UTF8Decoder();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                char value = (char)random.nextInt(0x10000);
                    if (value >= 0xd800 && value <= 0xdfff) {
                        if (j + 1 >= count) {
                            value &= 0xefff;
                        } else {
                            if (value > 0xdbff) {
                                value &= 0xfbff;
                            }
                            sample[j++] = value;
                            value = (char)(random.nextInt(0x400) + 0xdc00);
                        }
                    }
                    sample[j] = value;
            }
            encoder.clear();
            encoder.encodeUCS2(sample, 0, sample.length);
            test = encoder.toByteArray();
            decoder.setBuffer(test);
            check = decoder.decodeUCS2(check, 0, check.length);
            assertEquals("result length", sample.length, check.length);
            int scan = sample.length;
            while (--scan >= 0) {
                assertEquals("result data", sample[scan], check[scan]);
            }
        }
    }
    
    /** Test of decodeString method, of class net.sourceforge.fraglets.codec.UTF8Decoder. */
    public void testDecodeString() throws java.io.UnsupportedEncodingException {
        String sample = "foo";
        UTF8Decoder decoder = new UTF8Decoder();
        byte[] testBuffer = sample.getBytes("UTF-8");
        decoder.setBuffer(testBuffer);
        String result = decoder.decodeString(sample.length());
        assertEquals("decode string", sample, result);
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
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UTF8DecoderTest.class);
        
        return suite;
    }
    
}
