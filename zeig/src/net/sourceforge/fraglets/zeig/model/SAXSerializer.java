/*
 * SAXSerializer.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 6, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.SQLException;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author unknown
 */
public class SAXSerializer {
    private NodeFactory nf;
    private int piTag;
    private int textTag;
    
    public SAXSerializer(NodeFactory nf) throws SQLException {
        this.nf = nf;
        this.textTag = nf.getText();
        this.piTag = nf.getPi();
    }
    
    public void serialize(ContentHandler handler, int id) throws SAXException {
        handler.startDocument();
        serializeNode(handler, id);
        handler.endDocument();
    }
    
    public void serializeNode(ContentHandler handler, int id) throws SAXException {
        try {
            int name = nf.xt.getValue(id);
            int node[] = nf.xt.getNodes(id);
            if (name == textTag) {
                serialize(handler, name, node);
            } else {
                int atts[] = nf.xt.getAttributes(id);
                serialize(handler, name, atts, node);
            }
        } catch (SQLException ex) {
            throw new SAXException(ex);
        }
    }
    
    protected void serialize(ContentHandler handler, int name, int atts[], int node[]) throws SAXException {
        try {
            if (name != piTag) {
                int ns = nf.nm.getNamespace(name);
                String uri = nf.pt.getPlainText(nf.ns.getUri(ns));
                String prefix = nf.pt.getPlainText(nf.ns.getValue(ns));
                String lName = nf.pt.getPlainText(nf.nm.getValue(name));
                handler.startElement(uri, prefix, lName, new Atts(atts));
                for (int i = 1; i < node.length; i++) {
                    serializeNode(handler, node[i]);
                }
                handler.endElement(uri, prefix, lName);
            } else {
                String target = nf.pt.getPlainText(node[0]);
                String data = nf.pt.getPlainText(node[1]);
                handler.processingInstruction(target, data);
            }
        } catch (SQLException ex) {
            throw new SAXException(ex);
        }
    }
    
    protected void serialize(ContentHandler handler, int name, int node[]) throws SAXException {
        try {
            for (int i = 1; i < node.length; i++) {
                char chars[] = nf.pt.getPlainText(node[i]).toCharArray();
                handler.characters(chars, 0, chars.length);
            }
        } catch (SQLException ex) {
            throw new SAXException(ex);
        }
    }
    
    public static void main(String args[]) {
        try {
            SAXSerializer s = new SAXSerializer
                (new NodeFactory(ConnectionFactory.getInstance()));
            OutputFormat of = new OutputFormat();
            of.setIndent(1);
            XMLSerializer handler = new XMLSerializer(System.out, of);
            for (int i = 0; i < args.length; i++) {
                int id = Integer.parseInt(args[i]);
                s.serialize(handler, id);
            }
        } catch (SAXException ex) {
            ex.printStackTrace();
            if (ex.getException() != null) {
                ex.getException().printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public class Atts implements Attributes {
        private int atts[];
        
        public Atts(int atts[]) throws SQLException {
            this.atts = atts;
        }
        
        public int getLength() {
            return atts.length;
        }
        
        /**
         * @see org.xml.sax.Attributes#getName(int)
         */
        public String getUri(int index) {
            try {
                int id;
                id = nf.at.getName(atts[index]);
                id = nf.nm.getNamespace(id);
                id = nf.ns.getUri(id);
                return nf.pt.getPlainText(id);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * @see org.xml.sax.Attributes#getType(int)
         */
        public String getType(int arg0) {
            // TODO not supported
            return "CDATA";
        }

        /**
         * @see org.xml.sax.Attributes#getType(java.lang.String)
         */
        public String getType(String arg0, String arg1) {
            // TODO not supported
            return "CDATA";
        }

        /**
         * @see org.xml.sax.Attributes#getType(java.lang.String)
         */
        public String getType(String arg0) {
            // TODO not supported
            return "CDATA";
        }

        /**
         * @see org.xml.sax.AttributeList#getValue(int)
         */
        public String getValue(int index) {
            try {
                int id;
                id = nf.at.getValue(atts[index]);
                return nf.pt.getPlainText(id);
            } catch (Exception ex) {
                return null;
            }
        }
        
        /**
         * @see org.xml.sax.Attributes#getIndex(java.lang.String, java.lang.String)
         */
        public int getIndex(String uri, String localPart) {
            int scan = atts.length;
            while (--scan >= 0) {
                if (localPart.equals(this.getLocalName(scan)) &&
                    uri.equals(this.getURI(scan))) {
                    break;
                }
            }
            return scan;
        }

        /**
         * @see org.xml.sax.Attributes#getIndex(java.lang.String)
         */
        public int getIndex(String qName) {
            int scan = atts.length;
            while (--scan >= 0) {
                if (qName.equals(getQName(scan))) {
                    break;
                }
            }
            return scan;
        }

        /**
         * @see org.xml.sax.Attributes#getLocalName(int)
         */
        public String getLocalName(int index) {
            try {
                int id;
                id = nf.at.getName(atts[index]);
                id = nf.nm.getValue(id);
                return nf.pt.getPlainText(id);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * @see org.xml.sax.Attributes#getQName(int)
         */
        public String getQName(int index) {
            try {
                int id;
                id = nf.at.getName(atts[index]);
                id = nf.nm.getNamespace(id);
                id = nf.ns.getValue(id);
                String prefix = nf.pt.getPlainText(id);
                return prefix.length() > 0
                    ? prefix + ':' + getLocalName(index)
                    : getLocalName(index);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * @see org.xml.sax.Attributes#getURI(int)
         */
        public String getURI(int index) {
            try {
                int id;
                id = nf.at.getName(atts[index]);
                id = nf.nm.getNamespace(id);
                id = nf.ns.getUri(id);
                return nf.pt.getPlainText(id);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * @see org.xml.sax.Attributes#getValue(java.lang.String, java.lang.String)
         */
        public String getValue(String uri, String localName) {
            return getValue(getIndex(uri, localName));
        }

        /**
         * @see org.xml.sax.Attributes#getValue(java.lang.String)
         */
        public String getValue(String qName) {
            return getValue(getIndex(qName));
        }

    }
}
