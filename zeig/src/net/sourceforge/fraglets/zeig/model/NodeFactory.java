/*
 * NodeFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 6, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import net.sourceforge.fraglets.codec.OTPHash;
import net.sourceforge.fraglets.zeig.cache.CacheEntry;
import net.sourceforge.fraglets.zeig.cache.SensorCache;
import net.sourceforge.fraglets.zeig.cache.SimpleCache;
import net.sourceforge.fraglets.zeig.dom.NodeImpl;
import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.7 $
 */
public class NodeFactory implements NodeTags {
    protected ConnectionFactory cf;
    protected NameFactory nm;
    protected NamespaceFactory ns;
    protected PlainTextFactory pt;
    protected XMLTextFactory xt;
    
    protected int node = 0;
    protected int root = 0;
    protected int text = 0;
    protected int pi = 0;
    
    private SimpleCache nameCache;
    private SimpleCache qnameCache;
    
    public NodeFactory(ConnectionFactory cf) {
        this();
        this.cf = cf;
        this.nm = new NameFactory(cf);
        this.ns = new NamespaceFactory(cf);
        this.pt = new PlainTextFactory(cf);
        this.xt = new XMLTextFactory(cf);
    }
    
    private NodeFactory() {
        this.nameCache = new SensorCache("node.name");
        this.qnameCache = new SensorCache("node.qname");
    }
    
    public int getNamespace(String uri) throws SQLException {
        int id = pt.getPlainText(uri);
        try {
            return ns.getNamespace(id);
        } catch (NoSuchElementException ex) {
            if (uri != null && uri.length() > 0) {
                return ns.getNamespace(id, pt.getPlainText("id"+id));
            } else {
                return ns.getNamespace(id, pt.getPlainText(""));
            }
        }
    }
    
    public int getNamespace(String uri, String prefix) throws SQLException {
        return ns.getNamespace(pt.getPlainText(uri), pt.getPlainText(prefix));
    }
    
    public int getName(String uri, String name) throws SQLException {
        return getName(new NameCacheKey(uri, name));
    }
    
    public int getName(NameCacheKey key) throws SQLException {
        NameCacheEntry entry = (NameCacheEntry)nameCache
            .get(key.hash, key);
        if (entry != null) {
            return entry.getId();
        } else {
            int id = nm.getName(getNamespace(key.uri), pt.getPlainText(key.name));
            nameCache.put(new NameCacheEntry(key, key.hash, id));
            return id;
        }
    }
    
    public int getName(String qName) throws SQLException {
        int colon = qName.indexOf(':');
        NameCacheKey key;
        if (colon >= 0) {
            key = new NameCacheKey(qName.substring(0, colon),
                qName.substring(colon + 1));
        } else {
            key = new NameCacheKey(null, qName);
        }
        NameCacheEntry entry = (NameCacheEntry)qnameCache
            .get(key.hash, key);
        if (entry != null) {
            return entry.getId();
        } else {
            int id = nm.getName(
                ns.getNamespace(0, pt.getPlainText(key.uri/*prefix*/)),
                pt.getPlainText(key.name));
            qnameCache.put(new NameCacheEntry(key, key.hash, id));
            return id;
        }
    }
    
    public int getNode(int name, int node[]) throws SQLException {
        return xt.getXMLText(name, node);
    }
    
    public int[] getNodes(int id) throws SQLException {
        return xt.getNodes(id);
    }
    
    public int getProcessingInstruction(String name, String value) throws SQLException {
        return xt.getXMLText(getPi(),
            new int[] {pt.getPlainText(name), pt.getPlainText(value)});
    }
    
    public int getId(Node node) throws SAXException, SQLException {
        if (node instanceof NodeImpl) {
            int id = ((NodeImpl)node).getId();
            if (id > 0) {
                return id;
            }
        }
        
        SAXFactory sf = new SAXFactory(this);
        if (node.getNodeType() != Node.DOCUMENT_NODE) {
            sf.startFragment();
        }
        walk(node, sf);
        if (node.getNodeType() != Node.DOCUMENT_NODE) {
            sf.endFragment();
        }
        return sf.getLastResult();
    }
    
    protected void walk(Node node, SAXFactory sf) throws SAXException {
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
            {
                sf.startDocument();
                break;
            }
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                ProcessingInstruction pi = (ProcessingInstruction)node;
                sf.processingInstruction(pi.getTarget(), pi.getData());
                return;
            }
            case Node.ELEMENT_NODE:
            {
                Element el = (Element)node;
                sf.startElement(el.getNamespaceURI(), el.getLocalName(),
                    el.getTagName(), new NNLAttributes(el.getAttributes()));
                break;
            }
            case Node.TEXT_NODE:
            {
                Text tn = (Text)node;
                char data[] = tn.getData().toCharArray();
                sf.characters(data, 0, data.length);
                return;
            }
        }
        
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            walk(nl.item(i), sf);
        }
        
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
            {
                sf.endDocument();
                break;
            }
            case Node.ELEMENT_NODE:
            {
                Element el = (Element)node;
                sf.endElement(el.getNamespaceURI(), el.getLocalName(),
                    el.getTagName());
                break;
            }
        }
    }
    
    /**
     * @return
     */
    public int getNode() throws SQLException {
        if (node == 0) {
            node = getName("", NODE_NODE);
        }
        return node;
    }

    /**
     * @return
     */
    public int getText() throws SQLException {
        if (text == 0) {
            text = getName("", NODE_TEXT);
        }
        return text;
    }

    /**
     * @return
     */
    public int getRoot() throws SQLException {
        if (root == 0) {
            root = getName("", NODE_ROOT);
        }
        return root;
    }

    /**
     * @return
     */
    public int getPi() throws SQLException {
        if (pi == 0) {
            pi = getName("", NODE_PROCESSING_INSTRUCTION);
        }
        return pi;
    }
    
    public PlainTextFactory getPlainTextFactory() {
        return pt;
    }

    public NameFactory getNameFactory() {
        return nm;
    }
    
    public NamespaceFactory getNamespaceFactory() {
        return ns;
    }
    
    public XMLTextFactory getXMLTextFactory() {
        return xt;
    }
    
    public static class NameCacheKey {
        protected String uri;
        protected String name;
        protected int hash;
        
        public NameCacheKey(String uri, String name) {
            if (uri == null) {
                uri = "";
            }
            if (name == null) {
                throw new NullPointerException("null name");
            }
            this.uri = uri;
            this.name = name;
            this.hash = OTPHash.chain(OTPHash.hash(uri),OTPHash.hash(name));
        }
        
        public boolean equals(NameCacheKey other) {
            return hash == other.hash
                && uri.equals(other.uri) && name.equals(other.name);
        }
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object other) {
            try {
                return equals((NameCacheKey)other);
            } catch (ClassCastException ex) {
                return false;
            }
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return hash;
        }

    }

    public static class NameCacheEntry extends CacheEntry {
        private NameCacheKey key;
        int id;

        /**
         * @param hash
         */
        public NameCacheEntry(NameCacheKey key, int hash, int id) {
            super(hash);
            this.key = key;
            this.id = id;
        }

        /**
         * @see net.sourceforge.fraglets.zeig.cache.CacheEntry#equals(java.lang.Object)
         */
        public boolean equals(Object other) {
            if (key.equals(other)) {
                return true;
            } else if (other instanceof NameCacheEntry) {
                return ((NameCacheEntry)other).id == this.id;
            } else {
                return false;
            }
        }

        /**
         * @see net.sourceforge.fraglets.zeig.cache.CacheEntry#equals(int)
         */
        public boolean equals(int other) {
            return other == this.id;
        }
        
        public final int getId() {
            return this.id;
        }
    }
    
    public static class NNLAttributes implements Attributes {
        private NamedNodeMap delegate;
        
        public NNLAttributes(NamedNodeMap delegate) {
            this.delegate = delegate;
        }

        /**
         * @see org.xml.sax.Attributes#getIndex(java.lang.String)
         */
        public int getIndex(String qName) {
            return getIndex(delegate.getNamedItem(qName));
        }

        /**
         * @see org.xml.sax.Attributes#getIndex(java.lang.String, java.lang.String)
         */
        public int getIndex(String uri, String localPart) {
            return getIndex(delegate.getNamedItemNS(uri, localPart));
        }
        
        protected int getIndex(Node pivot) {
            int scan = delegate.getLength();
            while (--scan >= 0) {
                if (delegate.item(scan) == pivot) {
                    return scan;
                }
            }
            return -1;
        }

        /**
         * @see org.xml.sax.Attributes#getLength()
         */
        public int getLength() {
            return delegate.getLength();
        }

        /**
         * @see org.xml.sax.Attributes#getLocalName(int)
         */
        public String getLocalName(int index) {
            return delegate.item(index).getLocalName();
        }

        /**
         * @see org.xml.sax.Attributes#getQName(int)
         */
        public String getQName(int index) {
            return ((Attr)delegate.item(index)).getName();
        }

        /**
         * Not implemented.
         * @see org.xml.sax.Attributes#getType(int)
         */
        public String getType(int index) {
            return "CDATA"; // TODO not implemented
        }

        /**
         * Not implemented.
         * @see org.xml.sax.Attributes#getType(java.lang.String)
         */
        public String getType(String qName) {
            return "CDATA"; // TODO not implemented
        }

        /**
         * Not implemented.
         * @see org.xml.sax.Attributes#getType(java.lang.String, java.lang.String)
         */
        public String getType(String uri, String localName) {
            return "CDATA"; // TODO not implemented
        }

        /**
         * @see org.xml.sax.Attributes#getURI(int)
         */
        public String getURI(int index) {
            return ((Attr)delegate.item(index)).getNamespaceURI();
        }

        /**
         * @see org.xml.sax.Attributes#getValue(int)
         */
        public String getValue(int index) {
            return ((Attr)delegate.item(index)).getValue();
        }

        /**
         * @see org.xml.sax.Attributes#getValue(java.lang.String)
         */
        public String getValue(String qName) {
            return ((Attr)delegate.getNamedItem(qName)).getValue();
        }

        /**
         * @see org.xml.sax.Attributes#getValue(java.lang.String, java.lang.String)
         */
        public String getValue(String uri, String localName) {
            return ((Attr)delegate.getNamedItemNS(uri, localName)).getValue();
        }
    }
}
