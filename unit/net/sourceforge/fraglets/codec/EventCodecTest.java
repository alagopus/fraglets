package net.sourceforge.fraglets.codec;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision$
 */
public class EventCodecTest extends TestCase {
	protected EventCodec testCodec;

	/**
	 * Constructor for EventCodecTest2.
	 * @param arg0
	 */
	public EventCodecTest(String arg0) {
		super(arg0);
	}

	/**
	 * Lazily create a text codec fixture.
	 * 
	 * @return the test codec fixture
	 */
	protected TestCodec getTestCodec() {
		if (this.testCodec == null) {
			this.testCodec = new TestCodec();
		}
		return (TestCodec)this.testCodec;
	}

	/**
	 * Test the fixture.
	 */
	public void testEventCodec() {
		assertNotNull("constructor", getTestCodec());
	}

	/**
	 * Test for reset().
	 */
	public void testReset() {
		TestCodec codec = getTestCodec();
		int code1 = codec.createCode();
		testCodec.reset();
		int code2 = codec.createCode();
		assertEquals("same code after reset", code1, code2);
		
	}

	/**
	 * Test for getFirstLiteral().
	 */
	public void testGetFirstLiteral() {
		TestCodec codec = getTestCodec();
		assertTrue("first literal <= created code", codec.getFirstLiteral() <= codec.createCode());
	}

	/**
	 * Test for getString(int).
	 */
	public void testGetString() {
		TestCodec codec = getTestCodec();
		String text = "string";
		int code = codec.createCode();
		codec.ensureCapacity(code);
		EventCodec.Word word = new EventCodec.Word(code, text);
		codec.alphabet[code] = word;
		assertEquals("word equals getString()", word.toString(), codec.getString(code));
	}

	/**
	 * Test for getUCS4(int).
	 */
	public void testGetUCS4() {
		TestCodec codec = getTestCodec();
		String text = "string";
		int ucs4[] = EventCodec.toUCS4(text);
		int code = codec.createCode();
		codec.ensureCapacity(code);
		EventCodec.Word word = new EventCodec.Word(code, ucs4);
		codec.alphabet[code] = word;
		assertEquals("word equals getUCS4()", word.getUCS4(), codec.getUCS4(code));
	}

	/**
	 * Test for createCode().
	 */
	public void testCreateCode() {
		TestCodec codec = getTestCodec();
		int code1 = codec.createCode();
		int code2 = codec.createCode();
		assertTrue("code1 != code1", code1 != code2);
	}

	/**
	 * Test for ensureCapacity(int).
	 */
	public void testEnsureCapacity() {
		TestCodec codec = getTestCodec();
		int code1 = codec.createCode();
		codec.ensureCapacity(code1);
		try {
			codec.getWord(code1);
		} catch (Exception ex) {
			fail("ensureCapacity: "+ex);
		}
	}

	/**
	 * Test for toUCS4(String).
	 */
	public void testToUCS4()
	throws UnsupportedEncodingException {
		String text = "string";
		int ucs4[] = toUCS4(text);
		assertTrue("ucs4(text) equals toUCS4(text))",
			Arrays.equals(ucs4, EventCodec.toUCS4(text)));
	}
	
	public static int[] toUCS4(String text)
	throws UnsupportedEncodingException {
		UTF8Decoder decoder = new UTF8Decoder();
		decoder.setBuffer(text.getBytes("UTF-8"));
		return decoder.decodeUCS4(null, 0, text.length());
	}

	public static class TestCodec extends EventCodec {
	}
}
