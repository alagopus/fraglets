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
}
