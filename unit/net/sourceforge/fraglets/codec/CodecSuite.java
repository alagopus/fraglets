/*
 * CodecSuite.java
 * JUnit based test
 *
 * Created on 10. Juli 2002, 05:58
 */

package net.sourceforge.fraglets.codec;

import junit.framework.*;

/**
 *
 * @author marion@users.sourceforge.net
 */
public class CodecSuite extends TestCase {
    
    public CodecSuite(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        //--JUNIT:
        //This block was automatically generated and can be regenerated again.
        //Do NOT change lines enclosed by the --JUNIT: and :JUNIT-- tags.
        
        TestSuite suite = new TestSuite("CodecSuite");
        suite.addTest(net.sourceforge.fraglets.codec.UTF8DecoderTest.suite());
        suite.addTest(net.sourceforge.fraglets.codec.UTF8EncoderTest.suite());
        //:JUNIT--
        //This value MUST ALWAYS be returned from this function.
        return suite;
    }
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    
}
