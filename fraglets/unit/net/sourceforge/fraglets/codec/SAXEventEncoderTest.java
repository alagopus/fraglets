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
 *
 * @author marion@users.sourceforge.net
 */
public class SAXEventEncoderTest extends TestCase {
    
    public SAXEventEncoderTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SAXEventEncoderTest.class);
        
        return suite;
    }
    
    public void testFromFile() throws SAXException, IOException {
        Driver driver = new Driver();
        SAXEventEncoder encoder = new SAXEventEncoder();
        driver.setDocumentHandler(encoder);
        driver.parse("file:////C:/TEMP/in.xml");
        System.out.println("result length: "+encoder.getUTF8().length);
        java.io.FileOutputStream out =
            new java.io.FileOutputStream("c:/temp/out.txt");
        out.write(encoder.getUTF8());
    }
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    
    public static void print(String name, byte[] data, java.io.PrintStream out) {
        out.print(name);
        out.print(": [");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) out.print(", 0x");
            else out.print("0x");
            out.print(Integer.toString((int)data[i], 16));
        }
        out.println(']');
    }
}
