/*
 * SAXEventEncoder.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 14:21
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
 * SOFTWARE.  */

package net.sourceforge.fraglets.codec;

import org.xml.sax.DocumentHandler;
import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 *
 * @author  marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class SAXEventEncoder extends EventEncoder
    implements SAXEventCodes, DocumentHandler {
    
    /** Creates a new instance of SAXEventEncoder */
    public SAXEventEncoder() {
        nextLiteral = NEXT_WORD;
    }
    
    public void characters(char[] values, int off, int len)
        throws SAXException {
        encodeWord(CHARACTERS_WORD);
        encodeWord(new String(values, off, len));
    }
    
    public void endDocument() throws SAXException {
        encodeWord(END_DOCUMENT_WORD);
    }
    
    public void endElement(String str) throws SAXException {
        encodeWord(END_ELEMENT_WORD);
        encodeWord(str);
    }
    
    public void ignorableWhitespace(char[] values, int off, int len)
        throws SAXException {
        encodeWord(IGNORABLE_WHITESPACE_WORD);
        encodeWord(new String(values, off, len));
    }
    
    public void processingInstruction(String str1, String str2)
        throws SAXException {
        encodeWord(PROCESSING_INSTRUCTION_WORD);
        encodeWord(str1);
        encodeWord(str2);
    }
    
    public void setDocumentLocator(Locator locator) {
        throw new RuntimeException("not implemented");
    }
    
    public void startDocument() throws SAXException {
        encodeWord(START_DOCUMENT_WORD);
    }
    
    public void startElement(String str, AttributeList attrs)
        throws SAXException {
        int count = attrs != null ? attrs.getLength() : 0;
        encodeWord(START_ELEMENT_WORD);
        encodeWord(str);
        int triplet[] = new int[3];
        int list[] = new int[count];
        while (--count >= 0) {
            triplet[0] = declareWord(attrs.getName(count));
            triplet[1] = declareWord(attrs.getType(count));
            triplet[2] = declareWord(attrs.getValue(count));
            list[count] = declareWord(triplet);
        }
        encodeWord(list);
    }
    
    public void encodeWord(String ucs2) {
        if (ucs2 != null) {
            super.encodeWord(ucs2);
        } else {
            super.encodeWord(NULL_WORD);
        }
    }
    
    public int declareWord(String ucs2) {
        if (ucs2 != null) {
            return super.declareWord(ucs2);
        } else {
            return NULL_WORD;
        }
    }
    
}
