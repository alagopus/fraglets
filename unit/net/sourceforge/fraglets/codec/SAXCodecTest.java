/*
 * SAXCodecTest.java
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

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import sun.beans.editors.ByteEditor;

import com.jclark.xml.output.UTF8XMLWriter;
import com.jclark.xml.sax.Driver;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision$
 */
public abstract class SAXCodecTest extends TestCase {

    /** Test XML input. */
    public static final String TEST_INPUT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<root>\n"
            + " <sub id=\"sub1\">Some text</sub>\n"
            + " <sub id=\"sub2\">Some more text</sub>\n"
            + "</root>\n";

    /**
     * @see junit.framework.TestCase#TestCase(String)
     */
    public SAXCodecTest(String testName) {
        super(testName);
    }

    public InputSource toInputSource(String input) throws IOException {
        return new InputSource(new StringReader(input));
    }

    public class EventOutput extends UTF8XMLWriter implements DocumentHandler {
        private DocumentHandler delegate;

        public EventOutput() {
            super(new ByteArrayOutputStream());
        }

        public byte[] getBytes() {
            return ((ByteArrayOutputStream) lock).toByteArray();
        }

        public void reset() {
            ((ByteArrayOutputStream) lock).reset();
            setDelegate(null);
        }

        public void setDelegate(DocumentHandler delegate) {
            this.delegate = delegate;
        }

        public DocumentHandler getDelegate() {
            return delegate;
        }

        public void setDocumentLocator(Locator locator) {
            if (delegate != null) {
                delegate.setDocumentLocator(locator);
            }
        }

        public void startDocument() throws SAXException {
            if (delegate != null) {
                delegate.startDocument();
            }
        }

        public void endDocument() throws SAXException {
            if (delegate != null) {
                delegate.endDocument();
            }
        }

        public void startElement(String str, AttributeList attrs)
            throws SAXException {
            if (delegate != null) {
                delegate.startElement(str, attrs);
            }
            try {
                startElement(str);
                int end = attrs.getLength();
                for (int i = 0; i < end; i++) {
                    attribute(attrs.getName(i), attrs.getValue(i));
                }
            } catch (IOException ex) {
                throw new UndeclaredThrowableException(ex);
            }
        }

        public void characters(char[] values, int param, int param2)
            throws SAXException {
            if (delegate != null) {
                delegate.characters(values, param, param2);
            }
            try {
                write(values, param, param2);
            } catch (IOException ex) {
                throw new UndeclaredThrowableException(ex);
            }
        }

        public void ignorableWhitespace(char[] values, int param, int param2)
            throws SAXException {
            if (delegate != null) {
                delegate.ignorableWhitespace(values, param, param2);
            }
            try {
                write(values, param, param2);
            } catch (IOException ex) {
                throw new UndeclaredThrowableException(ex);
            }
        }

        public void processingInstruction(String str, String str1) {
            try {
                if (delegate != null) {
                    delegate.processingInstruction(str, str1);
                }
                super.processingInstruction(str, str1);
            } catch (Exception ex) {
                throw new UndeclaredThrowableException(ex);
            }
        }

        public void endElement(String str) {
            try {
                if (delegate != null) {
                    delegate.endElement(str);
                }
                super.endElement(str);
            } catch (Exception ex) {
                throw new UndeclaredThrowableException(ex);
            }
        }
    }
}
