package net.sourceforge.fraglets.codec;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision$
 */
public class AllTests {

	/**
	 * Create a test suite for this package.
	 * @return the test suite.
	 */
	public static Test suite() {
		TestSuite suite =
			new TestSuite("Test for net.sourceforge.fraglets.codec");
		//$JUnit-BEGIN$
		suite.addTestSuite(SAXEventDecoderTest.class);
		suite.addTestSuite(SAXEventEncoderTest.class);
		suite.addTestSuite(UTF8DecoderTest.class);
		suite.addTestSuite(UTF8EncoderTest.class);
		suite.addTestSuite(EventCodecTest.class);
//		suite.addTest(STDHashTest.suite());
		suite.addTest(OTPHashTest.suite());
		//$JUnit-END$
		return suite;
	}
}
