package net.sourceforge.fraglets.codec;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author kre
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite =
			new TestSuite("Test for net.sourceforge.fraglets.codec");
		//$JUnit-BEGIN$
		suite.addTest(SAXEventDecoderTest.suite());
		suite.addTest(SAXEventEncoderTest.suite());
		suite.addTest(UTF8DecoderTest.suite());
		suite.addTest(UTF8EncoderTest.suite());
		suite.addTest(new TestSuite(EventCodecTest.class));
		//$JUnit-END$
		return suite;
	}
}
