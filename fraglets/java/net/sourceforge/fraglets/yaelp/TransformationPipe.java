/*
 * TransformationPipe.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on July 19, 2002, 7:45 AM
 */

package net.sourceforge.fraglets.yaelp;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import com.jclark.xml.sax.Driver;
import com.jclark.xsl.sax.Destination;
import java.io.UnsupportedEncodingException;
import java.io.PipedInputStream;
import java.io.Writer;
import java.io.OutputStream;
import java.net.URL;
import com.jclark.xsl.sax.EncodingName;
import com.jclark.xsl.sax.OutputMethodHandlerImpl;
import com.jclark.xsl.sax.XSLProcessor;
import com.jclark.xsl.sax.XSLProcessorImpl;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * A PipedInputStream reading from a XML transformation.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * @author  marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class TransformationPipe extends PipedInputStream implements ErrorHandler, Destination {
    protected XSLProcessor processor;
    
    protected Exception failure;
    
    protected OutputStream out;
    
    protected String encoding;
    
    /** Creates a new instance of TransformationPipe */
    public TransformationPipe() throws IOException {
        this((String)null);
    }
    
    /** Creates a new instance of TransformationPipe */
    public TransformationPipe(String encoding) throws IOException {
        setEncoding(encoding);
        out = new PipedOutputStream(this);
    }
    
    public TransformationPipe loadStylesheet(URL xsl) throws IOException, SAXException {
        InputStream in = xsl.openStream();
        try {
            return loadStylesheet(in);
        } finally {
            in.close();
        }
    }
    
    public TransformationPipe loadStylesheet(InputStream xsl) throws IOException, SAXException {
        return loadStylesheet(new InputSource(xsl));
    }
    
    public TransformationPipe loadStylesheet(InputSource xsl) throws IOException, SAXException {
        if (processor == null) {
            processor = new XSLProcessorImpl();
            processor.setParser(new Driver());
            OutputMethodHandlerImpl handler =
                new OutputMethodHandlerImpl(processor);
            processor.setOutputMethodHandler(handler);
            handler.setDestination(this);
        }
        processor.loadStylesheet(xsl);
        return this;
    }
    
    public TransformationPipe transform(final InputStream xml) {
        new Thread("TransformationPipe") {
            public void run() {
                try {
                    try {
                        processor.parse(new InputSource(xml));
                    } finally {
                        out.close();
                        xml.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    failure = ex;
                }
            }
        }.start();
        Thread.yield();
        return this;
    }
    
    public void error(SAXParseException ex) throws SAXException {
        ex.printStackTrace();
        failure = ex;
    }
    
    public void fatalError(SAXParseException ex) throws SAXException {
        ex.printStackTrace();
        failure = ex;
    }
    
    public void warning(SAXParseException ex) throws SAXException {
        ex.printStackTrace();
    }
    
    protected void receive(int param) throws IOException {
        if (failure == null) {
            super.receive(param);
        } else {
            throw new IOException(failure.toString());
        }
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public OutputStream getOutputStream(String contentType, String encoding) throws IOException {
        return out;
    }
    
    public Writer getWriter(String contentType, String encoding) throws IOException, UnsupportedEncodingException {
        return new OutputStreamWriter(getOutputStream(contentType, encoding),
            EncodingName.toJava(getEncoding()));
    }

    public boolean keepOpen() {
        return false;
    }
    
    public Destination resolve(String uri) {
        return null;
    }
    
    /** Setter for property encoding.
     * @param encoding New value of property encoding.
     */
    public void setEncoding(String encoding) {
        if (encoding == null) {
            encoding = "UTF-8";
        }
        this.encoding = EncodingName.toIana(encoding);
    }
    
}
