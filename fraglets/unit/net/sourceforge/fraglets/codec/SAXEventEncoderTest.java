/*
 * SAXEventEncoderTest.java
 * JUnit based test
 *
 * Created on 10. Juli 2002, 16:33
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
 * @version $Revision: 1.2 $
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
