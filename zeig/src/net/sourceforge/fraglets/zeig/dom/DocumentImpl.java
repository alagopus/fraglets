/*
 * DocumentImpl.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.dom;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

import net.sourceforge.fraglets.zeig.model.NodeBuffer;
import net.sourceforge.fraglets.zeig.model.NodeFactory;
import net.sourceforge.fraglets.zeig.model.PlainTextFactory;
import net.sourceforge.fraglets.zeig.model.VersionFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.3 $
 */
public class DocumentImpl extends NodeImpl implements Document {
    public static final int ROOT;
    
    static {
        try {
            ROOT = NodeFactory.getInstance().getRoot();
        } catch (SQLException ex) {
            // TODO: fixme
            throw new RuntimeException(ex.toString());
        }
    }
    
    public DocumentImpl(int id) {
        super(null, id, ROOT);
    }

    /**
     * @see org.w3c.dom.Document#createAttribute(java.lang.String)
     */
    public Attr createAttribute(String name) throws DOMException {
        throw noModification();    }

    /**
     * @see org.w3c.dom.Document#createAttributeNS(java.lang.String, java.lang.String)
     */
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Document#createCDATASection(java.lang.String)
     */
    public CDATASection createCDATASection(String data) throws DOMException {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Document#createComment(java.lang.String)
     */
    public Comment createComment(String data) {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Document#createDocumentFragment()
     */
    public DocumentFragment createDocumentFragment() {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Document#createElement(java.lang.String)
     */
    public Element createElement(String tagName) throws DOMException {
        try {
            NodeFactory nf = NodeFactory.getInstance();
            int nm = nf.getName(tagName);
            int id = nf.getNode(nm, NodeBuffer.MT);
            return new ElementImpl(null, id, nm);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Document#createElementNS(java.lang.String, java.lang.String)
     */
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        try {
            NodeFactory nf = NodeFactory.getInstance();
            int nm = nf.getName(namespaceURI, qualifiedName);
            int id = nf.getNode(nm, NodeBuffer.MT);
            return new ElementImpl(null, id, nm);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Document#createEntityReference(java.lang.String)
     */
    public EntityReference createEntityReference(String name) throws DOMException {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Document#createProcessingInstruction(java.lang.String, java.lang.String)
     */
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Document#createTextNode(java.lang.String)
     */
    public Text createTextNode(String data) {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Document#getDoctype()
     */
    public DocumentType getDoctype() {
        return null;
    }

    /**
     * @see org.w3c.dom.Document#getDocumentElement()
     */
    public Element getDocumentElement() {
        return (Element)getLastChild();
    }

    /**
     * @see org.w3c.dom.Document#getElementById(java.lang.String)
     */
    public Element getElementById(String elementId) {
        try {
            int nm = NodeFactory.getInstance().getName("", "id");
            int v = PlainTextFactory.getInstance().getPlainText(elementId);
            return getElementById(this, nm, v);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }
    
    public static Element getElementById(Node root, int id, int v) {
        Element result = null;
        NodeList nl = root.getChildNodes();
        for (int i = 0; result == null && i < nl.getLength(); i++) {
            Node child = nl.item(i);
            NamedNodeMapImpl atts = (NamedNodeMapImpl)child.getAttributes();
            AttrImpl attr = (AttrImpl)atts.getNamedItem(id);
            if (attr != null && attr.getV() == v) {
                result = (Element)child;
            } else {
                result = getElementById(child, id, v);
            }
        }
        return result;
    }

    /**
     * @see org.w3c.dom.Document#getElementsByTagName(java.lang.String)
     */
    public NodeList getElementsByTagName(String tagname) {
        return getDocumentElement().getElementsByTagName(tagname);
    }

    /**
     * @see org.w3c.dom.Document#getElementsByTagNameNS(java.lang.String, java.lang.String)
     */
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return getDocumentElement().getElementsByTagNameNS(namespaceURI, localName);
    }

    /**
     * @see org.w3c.dom.Document#getImplementation()
     */
    public DOMImplementation getImplementation() {
        // TODO not implemented
        return null;
    }

    /**
     * @see org.w3c.dom.Document#importNode(org.w3c.dom.Node, boolean)
     */
    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return DOCUMENT_NODE;
    }
    
    public static void main(String args[]) {
        try {
            OutputFormat of = new OutputFormat();
            of.setIndent(1);
            XMLSerializer handler = new XMLSerializer(System.out, of);
            for (int i = 0; i < args.length; i++) {
                int id = Integer.parseInt(args[i]);
                id = VersionFactory.getInstance().getValue(id);
                handler.serialize(new DocumentImpl(id));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public String toString() {
        try {
            OutputFormat of = new OutputFormat();
            of.setIndent(1);
            StringWriter out = new StringWriter();
            XMLSerializer handler = new XMLSerializer(out, of);
            handler.serialize(this);
            return out.toString();
        } catch (IOException ex) {
            return super.toString() + ":" + ex.toString();
        }
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {
        return new DocumentImpl(getId());
    }

}
