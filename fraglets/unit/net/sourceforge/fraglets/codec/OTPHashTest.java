/*
 * OTPHashTest.java
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

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision$
 */
public class OTPHashTest extends STDHashTest {
	
	/**
	 * Constructor for OTPHashTest.
	 * @param name
	 */
	public OTPHashTest(String name) {
		super(name);
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(OTPHashTest.class);
		int size[] = SAMPLE_SIZE;
		int length[] = SAMPLE_LENGTH;
		for (int i = 0; i < size.length; i++) {
			for (int k = 0; k < length.length; k++) {
				suite.addTest(new TestHashLString(size[i], SAMPLE_LOAD, length[k]));
			}
		}
		
		return suite;
	}

	public void tearDown() {
		OTPHash.reset();
	}

	/**
	 * Test for chain(int,int).
	 */
	public void testChainII() {
		int i1 = 1234;
		int i2 = 5678;
		int h1 = OTPHash.hash(new int[] {i1, i2});
		int h2 = OTPHash.chain(i1, i2);
		assertEquals("chained equals array hash", h1, h2);
	}
	
	public static class TestHashLString extends STDHashTest.TestHashLString {
		public TestHashLString(int size, double load[], int length) {
			super(size, load, length);
		}
		
		public int hash(String sample) {
			return OTPHash.hash(sample);
		}
		
		public void tearDown() {
			OTPHash.reset();
		}
	}
	
	public void testHashConsistence() {
		String sample = "sample";
		int h1 = OTPHash.hash(sample);
		int h2 = OTPHash.hash(sample.toCharArray());
		assertEquals("char[]", h1, h2);
		int h3 = OTPHash.hash(new StringBuffer(sample));
		assertEquals("StringBuffer", h2, h3);
//		try {
//			int h4 = OTPHash.hash((Serializable)sample);
//			assertEquals("Serializable", h3, h4);
//		} catch (Exception ex) {
//			fail("unexpected: "+ex);
//		}
	}
    
    /** Repeat count for array portion tests. */
    public static final int REPEAT_PORTION = 20;
    
    /** Key length for array portion tests. */
    public static final int REPEAT_LENGTH = 64;
    
    public void testHashBArray() {
        Random random = new Random("testHashBArray".hashCode());
        byte key[] = new byte[REPEAT_LENGTH];
        int repeat = REPEAT_PORTION;
        while (--repeat >= 0) {
            random.nextBytes(key);
            byte portion[] = new byte[random.nextInt(key.length)];
            int offset = random.nextInt(key.length - portion.length);
            System.arraycopy(key, offset, portion, 0, portion.length);
            assertEquals(
                "byte array portion hash equal",
                OTPHash.hash(portion),
                OTPHash.hash(key, offset, portion.length));
        }
    }

    public void testHashCArray() {
        Random random = new Random("testHashCArray".hashCode());
        char key[];
        byte buffer[] = new byte[REPEAT_LENGTH * 2];
        int repeat = REPEAT_PORTION;
        try {
            while (--repeat >= 0) {
                random.nextBytes(buffer);
                key = new String(buffer, "UTF-16BE").toCharArray();
                char portion[] = new char[random.nextInt(key.length)];
                int offset = random.nextInt(key.length - portion.length);
                System.arraycopy(key, offset, portion, 0, portion.length);
                assertEquals(
                    "char array portion hash equal",
                    OTPHash.hash(portion),
                    OTPHash.hash(key, offset, portion.length));
            }
        } catch (UnsupportedEncodingException ex) {
            fail("unexpected: "+ex);
        }
    }

    public void testHashIArray() {
        Random random = new Random("testHashIArray".hashCode());
        int key[] = new int[REPEAT_LENGTH];
        int repeat = REPEAT_PORTION;
        while (--repeat >= 0) {
            for (int i = 0; i < key.length; i++) {
                key[i] = random.nextInt();
            }
            int portion[] = new int[random.nextInt(key.length)];
            int offset = random.nextInt(key.length - portion.length);
            System.arraycopy(key, offset, portion, 0, portion.length);
            assertEquals(
                "int array portion hash equal",
                OTPHash.hash(portion),
                OTPHash.hash(key, offset, portion.length));
        }
    }
}
