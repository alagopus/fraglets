/*
 * UTF8EncoderTest.java
 * JUnit based test
 *
 * Created on 10. Juli 2002, 05:14
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
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UTF8EncoderTest.class);
        
        return suite;
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
            encoder.setSize(0);
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
    
    /** Test of getBuffer method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testGetBuffer() {
        UTF8Encoder encoder = new UTF8Encoder();
        encoder.encodeString("test");
        assertNotNull("buffer is null", encoder.getBuffer());
    }
    
    /** Test of setBuffer method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testSetBuffer() {
        UTF8Encoder encoder = new UTF8Encoder();
        byte[] testBuffer = new byte[10];
        encoder.setBuffer(testBuffer);
        assertSame("set buffer failed", testBuffer, encoder.getBuffer());
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
        encoder.setSize(0);
        assertTrue("set size", encoder.getSize() == 0);
    }
    
    /** Test of toByteArray method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testToByteArray() {
        UTF8Encoder encoder = new UTF8Encoder();
        byte[] testBuffer = new byte[80];
        encoder.setBuffer(testBuffer);
        encoder.encodeString("test");
        byte[] checkBuffer = encoder.toByteArray();
        assertTrue("buffer copy", checkBuffer != testBuffer);
        assertTrue("copy size", checkBuffer.length < testBuffer.length);
    }
    
    /** Test of growBuffer method, of class net.sourceforge.fraglets.codec.UTF8Encoder. */
    public void testGrowBuffer() {
        UTF8Encoder encoder = new UTF8Encoder();
        byte[] testBuffer = new byte[10];
        encoder.setBuffer(testBuffer);
        encoder.growBuffer(12);
        assertTrue("grow buffer", encoder.getBuffer().length >= 12);
    }
}
