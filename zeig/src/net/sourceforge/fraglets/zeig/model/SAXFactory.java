/*
 * SAXFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 6, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.model;

import java.io.File;
import java.sql.SQLException;
import java.util.Stack;

import net.sourceforge.fraglets.zeig.cache.SensorCache;
import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author unknown
 */
public class SAXFactory implements ContentHandler {
    private NodeFactory nf;
    private Stack stack;
    private NodeBuffer buffer;
    private Locator locator;
    
    private NodeBuffer result;
    
    public SAXFactory(ConnectionFactory cf) {
        nf = new NodeFactory(cf);
        result = new NodeBuffer();
    }
    
    public int[] getResult() {
        return result.toIntArray();
    }
    
    /**
     * @see org.xml.sax.DocumentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }

    /**
     * @see org.xml.sax.DocumentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        stack = new Stack();
        buffer = new NodeBuffer();
    }

    /**
     * @see org.xml.sax.DocumentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        result.append(buffer.get(0));
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {
            stack.push(buffer);
            buffer = new NodeBuffer();
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            buffer.setId(nf.getName(namespaceURI, localName));
            int end = atts.getLength();
            for (int i = 0; i < end; i++) {
                buffer.append(nf.getAttribute(atts.getURI(i),
                    atts.getLocalName(i), atts.getValue(i)));
            }
            buffer.setAtts(end);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new SAXException(ex);
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        int id;
        try {
            id = nf.getNode(buffer.getId(), buffer.getAtts(), buffer.toIntArray());
            buffer = (NodeBuffer)stack.pop();
            buffer.append(id);
        } catch (SQLException ex) {
            throw new SAXException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        // ignored
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) throws SAXException {
        // ignored
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
        try {
            // declare prefix
            nf.getNamespace(prefix, uri);
        } catch (SQLException ex) {
            throw new SAXException(ex);
        }
    }

    /**
     * @see org.xml.sax.DocumentHandler#characters(char[], int, int)
     */
    public void characters(char[] c, int off, int len)
        throws SAXException {
        try {
            buffer.append(nf.getText(new String(c, off, len)));
        } catch (SQLException ex) {
            throw new SAXException(ex);
        }
    }

    /**
     * Ignored.
     * @see org.xml.sax.DocumentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
        throws SAXException {
        // ignore
    }

    /**
     * @see org.xml.sax.DocumentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String arg0, String arg1)
        throws SAXException {
        try {
            buffer.append(nf.getProcessingInstruction(arg0, arg1));
        } catch (SQLException ex) {
            throw new SAXException(ex);
        }
    }
    
    public static void process(XMLReader reader, File file) {
        if (file.isDirectory()) {
            File list[] = file.listFiles();
            for (int i = 0; i < list.length; i++) {
                process(reader, list[i]);
            }
        } else {
            try {
//                System.out.println("reading "+file.getName()+" ...");
                reader.parse(file.toURL().toExternalForm());
            } catch (SAXException ex) {
//                ex.printStackTrace();
//                if (ex.getException() != null) {
//                    ex.getException().printStackTrace();
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        try {
            ConnectionFactory cf = ConnectionFactory.getInstance();
            SAXFactory sf = new SAXFactory(cf);
            XMLReader reader = XMLReaderFactory
                .createXMLReader("org.apache.xerces.parsers.SAXParser");
            reader.setContentHandler(sf);
            for (int i = 0; i < args.length; i++) {
                process(reader, new File(args[i]));
            }
            int result[] = sf.getResult();
            for (int i = 0; i < result.length; i++) {
                System.out.println("result["+i+"]="+result[i]);
            }
            SensorCache.printStatistics(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace();
            if (ex.getException() != null) {
                ex.getException().printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
