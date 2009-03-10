/*
 * SAXEventDecoder.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 15:38
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
import org.xml.sax.SAXException;
import org.xml.sax.AttributeList;

/**
 *
 * @author  marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class SAXEventDecoder extends EventDecoder implements SAXEventCodes {
    
    /** Creates a new instance of SAXEventDecoder */
    public SAXEventDecoder() {
        nextLiteral = NEXT_WORD;
    }
    
    public int getFirstLiteral() {
        return NEXT_WORD;
    }
    
    protected String nextString() {
        int code = next();
        if (code == NULL_WORD) {
            return null;
        } else {
            return getWord(code).getUCS2();
        }
    }
    
    protected AttributeList nextAttributes() {
        return new WordAttributes(next());
    }
    
    public void toEvents(DocumentHandler handler) throws SAXException {
        while (hasNext()) {
            int code;
            switch (code = next()) {
                case CHARACTERS_WORD: {
                    char values[] = nextString().toCharArray();
                    handler.characters(values, 0, values.length);
                    break;
                }
                case START_DOCUMENT_WORD: {
                    handler.startDocument();
                    break;
                }
                case END_DOCUMENT_WORD: {
                    handler.endDocument();
                    break;
                }
                case START_ELEMENT_WORD: {
                    String name = nextString();
                    AttributeList attrs = nextAttributes();
                    handler.startElement(name, attrs);
                    break;
                }
                case END_ELEMENT_WORD: {
                    String name = nextString();
                    handler.endElement(name);
                    break;
                }
                case PROCESSING_INSTRUCTION_WORD: {
                    String str1 = nextString();
                    String str2 = nextString();
                    handler.processingInstruction(str1, str2);
                    break;
                }
                case IGNORABLE_WHITESPACE_WORD: {
                    char values[] = nextString().toCharArray();
                    handler.ignorableWhitespace(values, 0, values.length);
                    break;
                }
                default:
                    throw new SAXException("format error: "+code);
            }
        }
    }
    
    class WordAttributes implements AttributeList {
        int code;
        
        public WordAttributes (int code) {
            this.code = code;
        }
        
        public int getLength() {
            return getUCS4(code).length;
        }
        
        public String getName(int index) {
            int subcode = getUCS4(code)[index];
            return getString(getUCS4(subcode)[0]);
        }
        
        public String getType(int index) {
            int subcode = getUCS4(code)[index];
            return getString(getUCS4(subcode)[1]);
        }
        
        public String getValue(int index) {
            int subcode = getUCS4(code)[index];
            return getString(getUCS4(subcode)[2]);
        }
        
        public String getType(String str) {
            int count = getLength();
            while (--count >= 0) {
                if (str.equals(getName(count))) {
                    return getType(count);
                }
            }
            return null;
        }
        
        public String getValue(String str) {
            int count = getLength();
            while (--count >= 0) {
                if (str.equals(getName(count))) {
                    return getValue(count);
                }
            }
            return null;
        }
        
        protected String getString(int code) {
            if (code == NULL_WORD) {
                return null;
            } else {
                return SAXEventDecoder.this.getString(code);
            }
        }
    }
}
