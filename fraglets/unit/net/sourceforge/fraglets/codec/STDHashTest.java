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
public class STDHashTest extends TestCase {
	
	public static final int SAMPLE_SIZE[] =
		new int[] { 10, 100, 1000, 10000, 100000 };
	
	public static final double SAMPLE_LOAD[] =
		new double[] { 0.125D, 0.5D, 0.75D, 1.0D, 2.0D, 4.0D };
		
	public static final int SAMPLE_LENGTH[] =
		new int[] { 0, 2, 4, 8, 16, 64, 128 };
	
	/**
	 * Constructor for OTPHashTest.
	 * @param name
	 */
	public STDHashTest(String name) {
		super(name);
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("STDHashTest");
		int size[] = SAMPLE_SIZE;
		int length[] = SAMPLE_LENGTH;
		for (int i = 0; i < size.length; i++) {
			for (int k = 0; k < length.length; k++) {
				suite.addTest(new TestHashLString(size[i], SAMPLE_LOAD, length[k]));
			}
		}
		
		return suite;
	}

	public static class TestHashLString extends TestCase {
		private int size;
		private double load[];
		private int length;
		
		public TestHashLString(int size, double load[], int length) {
			super(nameFor(size, length));
			this.size = size;
			this.load = load;
			this.length = length;
		}
		
		public static String nameFor(int size, int length) {
			return "testHashLString.size"+size+".length"+length;
		}
		
		protected void runTest() throws Throwable {
			if (length > 0) {
				runTestHashLString(size, load, length);
			} else {
				runTestHashLString(size, load);
			}
		}
		
		public void runTestHashLString(int size, double load[]) {
			int samples[] = new int[size];
			int scan = size;
			char key[] = null;
			while (--scan >= 0) {
				key = incrementKey(key);
				String sample = new String(key);
				samples[scan] = hash(sample);
			}
			
			for (int i = 0; i < load.length; i++) {
				checkStatistics(samples, load[i], key.length);
			}
		}
		
		public void runTestHashLString(int size, double load[], int length) {
			Random random = new Random((long)String.valueOf(length).hashCode());
			int samples[] = new int[size];
			int scan = size;
			byte buffer[] = new byte[length * 2];
			try {
				while (--scan >= 0) {
					random.nextBytes(buffer);
					String sample = new String(buffer, "UTF-16BE");
					samples[scan] = hash(sample);
				}
			} catch (UnsupportedEncodingException ex) {
				fail("unexpected: "+ex);
			}
			
			for (int i = 0; i < load.length; i++) {
				checkStatistics(samples, load[i], length);
			}
		}
		
		public int hash(String sample) {
			return sample.hashCode();
		}
	}
	
	/**
	 * Utility method to create keys over the ASCII range of [' '-127].
	 * The provided previous key will be changed in-place even if the
	 * result will be re-allocated.
	 * 
	 * @param key the previous key
	 * @return the next key
	 */
	public static char[] incrementKey(char key[]) {
		if (key == null) return new char[] { ' ' };
		int digit = key.length - 1;
		while (digit >= 0 && key[digit] >= (char)127) {
			key[digit--] = ' ';
		}
		if (digit >= 0) {
			key[digit] = (char)(key[digit] + 1);
		} else {
			char grow[] = new char[key.length+1];
			System.arraycopy(key, 0, grow, 0, key.length);
			grow[grow.length-1] = ' ';
			key = grow;
		}
		return key;
	}
	
	public static void checkStatistics(int samples[], double load, int length) {
		double stats[] = computeStatistics(samples, load);
		checkStatistics(load, stats[0], stats[1]);
	}
	
	public static void checkStatistics(double load, double average, double maximum) {
		assertTrue("average <= load + 1", average <= load + 1);
		assertTrue("maximum <= (load + 1) * 4", maximum <= (load + 1) * 4);
	}
	
	/**
	 * Compute the average chain length.
	 * 
	 * @param input hash value samples
	 * @param load the minimum load factor to apply
	 * @return the average chain length
	 */
	public static double[] computeStatistics(int input[], double load) {
		int samples[] = new int[input.length];
		int scan = samples.length;
		int size = (int)Math.ceil(samples.length / load);
		while (--scan >= 0) {
			samples[scan] = input[scan] % size;
		}
		return computeStatistics(samples);
	}
	
	public static double[] computeStatistics(int samples[]) {
		Arrays.sort(samples);
		int scan = samples.length;
		double average = 0;
		int last = samples[--scan];
		int chain = 1;
		int count = 1;
		int large = 0;
		while (--scan >= 0) {
			int next = samples[scan];
			if (next == last) {
				chain += 1;
			} else {
				large = Math.max(large, chain);
				average += chain;
				count += 1;
				chain = 1;
				last = next;
			}
		}
		large = Math.max(large, chain);
		average += chain;
		average /= count;
		return new double[] { average, (double)large };
	}
}
