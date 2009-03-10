/*
 * SAXEventDecoderTest.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import sun.beans.editors.ByteEditor;

import com.jclark.xml.output.UTF8XMLWriter;
import com.jclark.xml.sax.Driver;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.3 $
 */
public class SAXEventDecoderTest extends SAXCodecTest {
    
    public SAXEventDecoderTest(String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SAXEventDecoderTest.class);
        
        return suite;
    }
    
    /** Test of toEvents method, of class net.sourceforge.fraglets.codec.SAXEventDecoder. */
    public void testToEvents() throws SAXException, IOException {
        Driver driver = new Driver();
        EventOutput output = new EventOutput();
        SAXEventEncoder encoder = new SAXEventEncoder();
        output.setDelegate(encoder);
        driver.setDocumentHandler(output);
        driver.parse(toInputSource(TEST_INPUT));
        byte sample[] = output.getBytes();
        SAXEventDecoder decoder = new SAXEventDecoder();
        decoder.setBuffer(encoder.getUTF8());
        output.reset();
        decoder.toEvents(output);
        byte result[] = output.getBytes();
        assertTrue("equal output", Arrays.equals(sample, result));
    }
    
}
