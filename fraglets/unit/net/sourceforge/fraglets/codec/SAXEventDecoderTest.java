/*
 * SAXEventDecoderTest.java
 * JUnit based test
 *
 * Created on 10. Juli 2002, 16:43
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
 * @version $Revision: 1.2 $
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
