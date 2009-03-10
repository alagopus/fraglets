/*
 * UTF8EncoderTest.java
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
import java.io.UnsupportedEncodingException;

/**
 *
 * @author marion@users.sourceforge.net
 */
public class UTF8EncoderTest extends TestCase {
    
    public UTF8EncoderTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Test of encodeUCS4 method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testEncodeUCS4() throws java.io.UnsupportedEncodingException {
        String sample = "f\u00FCrchterlich";
        int[] data = new int[sample.length()];
        for (int i = 0; i < data.length; i++) {
            data[i] = sample.charAt(i);
        }
        UTF8Encoder encoder = new UTF8Encoder();
        encoder.encodeUCS4(data, 0, data.length);
        assertEquals("UCS-4 encoding", sample,
            new String(encoder.toByteArray(), "UTF-8"));
    }
    
    /** Test of encodeUCS2 method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testEncodeUCS2() throws java.io.UnsupportedEncodingException {
        String sample = "f\u00FCrchterlich";
        char[] data = sample.toCharArray();
        UTF8Encoder encoder = new UTF8Encoder();
        encoder.encodeUCS2(data, 0, data.length);
        assertEquals("UCS-2 encoding", sample,
            new String(encoder.toByteArray(), "UTF-8"));
    }
    
    public void testEncodeUCS2Random() throws java.io.UnsupportedEncodingException {
        int count = 1000;
        char sample[] = new char[count];
        byte check[] = null;
        byte test[] = null;
        String sampleCheck = null;
        java.util.Random random = new java.util.Random(count);
        UTF8Encoder encoder = new UTF8Encoder();
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
            sampleCheck = new String(sample);
            check = sampleCheck.getBytes("UTF-8");
            assertEquals("result length", test.length, check.length);
            int scan = test.length;
            while (--scan >= 0) {
                assertEquals("result data", test[scan], check[scan]);
            }
        }
    }
    
    /** Test of encodeString method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testEncodeString() throws java.io.UnsupportedEncodingException {
        String sample = "f\u00FCrchterlich";
        UTF8Encoder encoder = new UTF8Encoder();
        encoder.encodeString(sample);
        assertEquals("string encoding", sample,
            new String(encoder.toByteArray(), "UTF-8"));
    }
    
    /** Test of getSize method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testGetSize() {
        UTF8Encoder encoder = new UTF8Encoder();
        encoder.encodeString("test");
        assertTrue("get size", encoder.getSize() > 0);
    }
    
    /** Test of setSize method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testSetSize() {
        UTF8Encoder encoder = new UTF8Encoder();
        encoder.encodeString("test");
        encoder.clear();
        assertTrue("set size", encoder.getSize() == 0);
    }
    
    /** Test of toByteArray method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testToByteArray() {
        UTF8Encoder encoder = new UTF8Encoder();
        encoder.encodeString("test1islonger");
        byte[] testBuffer = encoder.toByteArray();
        encoder.clear();
        encoder.encodeString("test2");
        byte[] checkBuffer = encoder.toByteArray();
        assertTrue("buffer copy", checkBuffer != testBuffer);
        assertTrue("copy size", checkBuffer.length < testBuffer.length);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UTF8EncoderTest.class);
        return suite;
    }
    
}
