/*
 * NodeImpl.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.dom;

import java.sql.SQLException;

import net.sourceforge.fraglets.zeig.model.NameFactory;
import net.sourceforge.fraglets.zeig.model.NamespaceFactory;
import net.sourceforge.fraglets.zeig.model.NodeBuffer;
import net.sourceforge.fraglets.zeig.model.NodeFactory;
import net.sourceforge.fraglets.zeig.model.PlainTextFactory;
import net.sourceforge.fraglets.zeig.model.XMLTextFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class NodeImpl implements Node {
    private int id;
    private int v;
    
    private NodeImpl parent;
    private NodeList children;
    private NamedNodeMap attributes;
    
    public NodeImpl(NodeImpl parent, int id, int v) {
        this.parent = parent;
        this.id = id;
        this.v = v;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getV() {
        return this.v;
    }
    
    protected static DOMException notImplemented() {
        return new DOMException
            (DOMException.NOT_SUPPORTED_ERR, "not implemented");
    }
    
    protected static DOMException domException(SQLException ex) {
        return new DOMException((short)ex.getErrorCode(), ex.getMessage());
    }

    protected static DOMException domException(SAXException ex) {
        if (ex.getException() != null) {
            return new DOMException((short)0, ex.getException().toString());
        }
        return new DOMException((short)0, ex.getMessage());
    }
    
    protected void validate() throws SAXException, SQLException {
        id = 0;
        id = NodeFactory.getInstance().getId(this);
    }
    
    /**
     * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node)
     */
    public Node appendChild(Node newChild) throws DOMException {
        try {
            NodeFactory nf = NodeFactory.getInstance();
            NodeBuffer buffer = new NodeBuffer(nf.getNodes(getId()));
            buffer.append(nf.getNode()).append(nf.getId(newChild));
            children = null;
            id = nf.getNode(v, buffer.toIntArray());
            if (parent != null) {
                parent.validate();
            }
            return newChild;
        } catch (SAXException ex) {
            throw domException(ex);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {
        // always deep
        return new NodeImpl(parent, id, v);
    }

    /**
     * @see org.w3c.dom.Node#getAttributes()
     */
    public NamedNodeMap getAttributes() {
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    attributes = new NamedNodeMapImpl(this, true);
                }
            }
        }
        return attributes;
    }

    /**
     * @see org.w3c.dom.Node#getChildNodes()
     */
    public NodeList getChildNodes() {
        if (children == null) {
            synchronized (this) {
                if (children == null) {
                    children = new NamedNodeMapImpl(this, false);
                }
            }
        }
        return children;
    }

    /**
     * @see org.w3c.dom.Node#getFirstChild()
     */
    public Node getFirstChild() {
        return getChildNodes().item(0);
    }

    /**
     * @see org.w3c.dom.Node#getLastChild()
     */
    public Node getLastChild() {
        NodeList nl = getChildNodes();
        return nl.item(nl.getLength() - 1);
    }
    
    public int getNm() {
        try {
            return XMLTextFactory.getInstance().getName(id);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }
    
    public int getLocalNm() {
        try {
            return NameFactory.getInstance().getValue(getNm());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Node#getLocalName()
     */
    public String getLocalName() {
        try {
            return PlainTextFactory.getInstance().getPlainText(getLocalNm());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }
    
    public int getNamespace() {
        try {
            return NameFactory.getInstance().getNamespace(getNm());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }
    
    public int getNamespaceURIId() {
        try {
            return NamespaceFactory.getInstance().getUri(getNamespace());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Node#getNamespaceURI()
     */
    public String getNamespaceURI() {
        try {
            return PlainTextFactory.getInstance().getPlainText(getNamespaceURIId());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Node#getNextSibling()
     */
    public Node getNextSibling() {
        NamedNodeMapImpl nl = (NamedNodeMapImpl)getParentNode().getChildNodes();
        int index = nl.getIndex(this) + 1;
        if (index < nl.getLength()) {
            return nl.item(index);
        } else {
            return null;
        }
    }

    /**
     * @see org.w3c.dom.Node#getNodeName()
     */
    public String getNodeName() {
        String prefix = getPrefix();
        if (prefix.length() > 0) {
            return prefix + ":" + getLocalName();
        } else {
            return getLocalName();
        }
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return TEXT_NODE;
    }

    /**
     * @see org.w3c.dom.Node#getNodeValue()
     */
    public String getNodeValue() throws DOMException {
        try {
            return PlainTextFactory.getInstance().getPlainText(v);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Node#getOwnerDocument()
     */
    public Document getOwnerDocument() {
        Node scan = this;
        while (!(scan instanceof Document)) {
            scan = (Node)scan.getParentNode();
        }
        return (Document)scan;
    }

    /**
     * @see org.w3c.dom.Node#getParentNode()
     */
    public Node getParentNode() {
        return parent;
    }

    public int getNamespacePrefixId() {
        try {
            return NamespaceFactory.getInstance().getValue(getNamespace());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Node#getPrefix()
     */
    public String getPrefix() {
        try {
            return PlainTextFactory.getInstance().getPlainText(getNamespacePrefixId());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Node#getPreviousSibling()
     */
    public Node getPreviousSibling() {
        NamedNodeMapImpl nl = (NamedNodeMapImpl)getParentNode().getChildNodes();
        int index = nl.getIndex(this) - 1;
        if (index >= 0) {
            return nl.item(index);
        } else {
            return null;
        }
    }

    /**
     * @see org.w3c.dom.Node#hasAttributes()
     */
    public boolean hasAttributes() {
        return getAttributes().getLength() > 0;
    }

    /**
     * @see org.w3c.dom.Node#hasChildNodes()
     */
    public boolean hasChildNodes() {
        return getChildNodes().getLength() > 0;
    }

    /**
     * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node)
     */
    public Node insertBefore(Node newChild, Node refChild)
        throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Node#normalize()
     */
    public void normalize() {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node)
     */
    public Node removeChild(Node oldChild) throws DOMException {
        return replaceChild(null, oldChild);
    }

    /**
     * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node)
     */
    public Node replaceChild(Node newChild, Node oldChild)
        throws DOMException {
        try {
            NodeImpl newNode = (NodeImpl)newChild;
            NodeImpl oldNode = (NodeImpl)oldChild;
            
            NodeFactory nf = NodeFactory.getInstance();
            NodeList newNodes = null;
            int nodes[] = nf.getNodes(getId());
            int length = nodes.length;
            int nodeId = nf.getNode();
            if (oldChild == null) {
                length += 2;
            }
            if (newNode == null) {
                length -= 2;
            } else if (newNode.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
                newNodes = newNode.getChildNodes();
                length += (newNodes.getLength() - 1) * 2;
            }
            NodeBuffer buffer = new NodeBuffer(length);
            int scan = 0;
            if (oldNode != null) {
                while (scan < nodes.length) {
                    if (nodes[scan] != nodeId || nodes[scan+1] != oldNode.id) {
                        buffer.append(nodes[scan++]);
                        buffer.append(nodes[scan++]);
                    } else {
                        scan += 2;
                        break;
                    }
                }
            } else {
                while (scan < nodes.length) {
                    buffer.append(nodes[scan++]);
                    buffer.append(nodes[scan++]);
                }
            }
            if (newNodes != null) {
                for (int i = 0; i < newNodes.getLength(); i++) {
                    NodeImpl insert = (NodeImpl)newNodes.item(i);
                    buffer.append(nodeId);
                    buffer.append(insert.id);
                }
            } else if (newNode != null) {
                buffer.append(nodeId);
                buffer.append(newNode.id);
            }
            while (scan < nodes.length) {
                buffer.append(nodes[scan++]);
                buffer.append(nodes[scan++]);
            }
            children = null;
            id = nf.getNode(v, buffer.toIntArray());
            if (parent != null) {
                parent.validate();
            }
            return oldChild;
        } catch (SAXException ex) {
            throw domException(ex);
        } catch (SQLException ex) {
            throw domException(ex);
        } catch (ClassCastException ex) {
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, ex.toString());
        }
    }

    /**
     * @see org.w3c.dom.Node#setNodeValue(java.lang.String)
     */
    public void setNodeValue(String nodeValue) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Node#setPrefix(java.lang.String)
     */
    public void setPrefix(String prefix) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Node#supports(java.lang.String, java.lang.String)
     */
    public boolean supports(String feature, String version) {
        return false;
    }

}
