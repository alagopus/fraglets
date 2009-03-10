package net.sourceforge.fraglets.dfind;

import java.io.IOException;
import java.io.InputStream;
import junit.framework.*;

public class SearcherTest extends TestCase {
    
    public SearcherTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SearcherTest.class);
        
        return suite;
    }
    
    /** Test of search method, of class net.sourceforge.fraglets.dfind.Searcher. */
    public void testSearch() {
        System.out.println("testSearch");
        String pattern = "patpatpatternernern";
        StringBuffer b = new StringBuffer();
        b.append("some dummy text with patpatpat ");
        b.append("and another failing ernernern ");
        b.append(b).append(b).append(b).append(b);
        b.append("and finally the real "+pattern+" ");
        b.append(b);
        String text = b.toString();
        Searcher s = new Searcher(pattern);
        int expect = text.indexOf(pattern);
        int pos = s.search(text);
        assertTrue("pos == "+expect, pos == expect);
        byte input[] = text.getBytes();
        pos = s.search(input);
        assertTrue("pos == "+expect, pos == expect);
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            text.indexOf(pattern);
        }
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            s.search(input);
        }
        long t2 = System.currentTimeMillis();
        assertTrue("t1-t0 >= t2-t1", t1-t0 >= t2-t1);
    }
    
    /** Test of setPattern method, of class net.sourceforge.fraglets.dfind.Searcher. */
    public void testSetPattern() {
        System.out.println("testSetPattern");
        Searcher s = new Searcher("pattern");
        s.setPattern("newPattern".getBytes());
        assertEquals(s.search("without new pattern but old"), -1);
        assertEquals(s.search("with newPattern"), 5);
        assertEquals(s.search("with old pattern and newPattern"), 21);
    }
}
