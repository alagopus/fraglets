/*
 * SAXEventDecoderTest.java
 * JUnit based test
 *
 * Created on 10. Juli 2002, 16:43
 */

package net.sourceforge.fraglets.codec;

import junit.framework.*;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.AttributeList;
import com.jclark.xml.sax.Driver;
import java.io.IOException;
import com.jclark.xml.output.UTF8XMLWriter;

/**
 *
 * @author marion@users.sourceforge.net
 */
public class SAXEventDecoderTest extends TestCase {
    
    public SAXEventDecoderTest(java.lang.String testName) {
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
        SAXEventEncoder encoder = new SAXEventEncoder();
        driver.setDocumentHandler(encoder);
        driver.parse("file:////C:/TEMP/vl.xml");
        SAXEventDecoder decoder = new SAXEventDecoder();
        decoder.setBuffer(encoder.getUTF8());
        System.out.println("result length: "+encoder.getUTF8().length);
        EventOutput output = new EventOutput();
        decoder.toEvents(output);
    }
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    class EventOutput extends UTF8XMLWriter implements DocumentHandler {
        public EventOutput() {
            super(System.err);
        }
        
        public void setDocumentLocator(org.xml.sax.Locator locator) {
            // ignore
        }
        
        public void startDocument() {
        }
        
        public void endDocument() {
        }
        
        public void startElement(String str, AttributeList attrs) {
            try {
                startElement(str);
                int end = attrs.getLength();
                for (int i = 0; i < end; i++) {
                    attribute(attrs.getName(i), attrs.getValue(i));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        public void characters(char[] values, int param, int param2) {
            try {
                write(values, param, param2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        public void ignorableWhitespace(char[] values, int param, int param2) {
            try {
                write(values, param, param2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        public void processingInstruction(String str, String str1) {
            try {
                super.processingInstruction(str, str1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        public void endElement(String str) {
            try {
                super.endElement(str);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }
}
