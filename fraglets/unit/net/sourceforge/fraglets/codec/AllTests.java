/*
 * AllTests.java
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
        //	suite.addTest(STDHashTest.suite());
        suite.addTest(OTPHashTest.suite());
        //$JUnit-END$
        return suite;
    }
}
