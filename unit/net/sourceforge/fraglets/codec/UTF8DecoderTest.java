/*
 * UTF8DecoderTest.java
 * JUnit based test
 *
 * Created on 10. Juli 2002, 05:39
 */

package net.sourceforge.fraglets.codec;

import junit.framework.*;

/**
 *
 * @author marion@users.sourceforge.net
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
        String sampleCheck = null;
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
        String sampleCheck = null;
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
