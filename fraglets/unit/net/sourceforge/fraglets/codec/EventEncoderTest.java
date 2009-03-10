/*
 * EventEncoderTest.java
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

import junit.framework.TestCase;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision$
 */
public class EventEncoderTest extends TestCase {

    /**
     * Constructor for EventEncoderTest.
     * @param arg0
     */
    public EventEncoderTest(String arg0) {
        super(arg0);
    }

    public void testReset() {
    }

    public void testEnsureCapacity() {
    }

    public void testEventEncoder() {
    }

    public void testGetUTF8() {
    }

    /*
     * Test for int declareWord(int[])
     */
    public void testDeclareWordIArray() {
    }

    /*
     * Test for int declareWord(String)
     */
    public void testDeclareWordString() {
    }

    /*
     * Test for void encodeWord(int[])
     */
    public void testEncodeWordIArray() {
    }

    /*
     * Test for void encodeWord(String)
     */
    public void testEncodeWordString() {
    }

    /*
     * Test for void encodeWord(int)
     */
    public void testEncodeWordI() {
    }

    public void testGetLoad() {
    }

    public void testRehash() {
    }

    /*
     * Test for Word insert(Word)
     */
    public void testInsertWord() {
    }

    /*
     * Test for Word insert(int[])
     */
    public void testInsertIArray() {
    }

    /*
     * Test for Word insert(String)
     */
    public void testInsertString() {
    }

    public void testLookup() {
    }

    public void testNextPrime() {
        assertEquals("first prime", 2, getEventEncoder().nextPrime(0));
        assertEquals("next prime 3", 3, getEventEncoder().nextPrime(3));
        assertEquals("next prime 4", 5, getEventEncoder().nextPrime(4));
        assertEquals("next prime 5", 5, getEventEncoder().nextPrime(5));
        assertEquals("next prime 6", 7, getEventEncoder().nextPrime(7));
    }

    public EventEncoder getEventEncoder() {
        if (eventEncoder == null) {
            eventEncoder = new EventEncoder();
        }
        return eventEncoder;
    }

    private EventEncoder eventEncoder;
}
