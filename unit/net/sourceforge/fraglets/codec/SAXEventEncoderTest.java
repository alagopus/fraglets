/*
 * SAXEventEncoderTest.java
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

import junit.framework.*;
import org.xml.sax.DocumentHandler;
import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.jclark.xml.sax.Driver;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.4 $
 */
public class SAXEventEncoderTest extends SAXCodecTest {

    public SAXEventEncoderTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SAXEventEncoderTest.class);

        return suite;
    }

    public void testFromFile() throws SAXException, IOException {
        Driver driver = new Driver();
        SAXEventEncoder encoder = new SAXEventEncoder();
        driver.setDocumentHandler(encoder);
        driver.parse(toInputSource(SAXCodecTest.TEST_INPUT));
        assertTrue("encoder output", encoder.getUTF8().length > 0);
    }
}
