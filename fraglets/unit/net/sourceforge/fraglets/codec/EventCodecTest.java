package net.sourceforge.fraglets.codec;

import junit.framework.TestCase;

/**
 * @author kre
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EventCodecTest extends TestCase {
	private EventCodec testCodec;

	/**
	 * Constructor for EventCodecTest2.
	 * @param arg0
	 */
	public EventCodecTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		testCodec = new TestCodec();
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		testCodec = null;
	}

	public void testEventCodec() {
		assertNotNull("constructor", testCodec);
	}

	public void testReset() {
	}

	public void testGetFirstLiteral() {
		assertTrue("first literal >= 1", testCodec.getFirstLiteral() >= 1);
	}

	public void testGetWord() {
	}

	public void testGetString() {
	}

	public void testGetUCS4() {
	}

	public void testCreateCode() {
	}

	public void testEnsureCapacity() {
	}

	public void testToUCS4() {
	}

	/*
	 * Test for int hashCode(int[])
	 */
	public void testHashCodeIArray() {
	}

	/*
	 * Test for int hashCode(int[], int, int)
	 */
	public void testHashCodeIArrayII() {
	}

	public void testMain() {
	}

	public void testPrint() {
	}
	
	public static class TestCodec extends EventCodec {
	}
}
