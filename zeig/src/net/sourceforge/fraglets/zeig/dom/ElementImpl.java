/*
 * ElementImpl.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
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
package net.sourceforge.fraglets.zeig.dom;

import java.sql.SQLException;
import java.util.ArrayList;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.4 $
 */
public class ElementImpl extends NodeImpl implements Element {
    
    public ElementImpl(NodeImpl parent, int id, int v) {
        super(parent, id, v);
    }

    /**
     * @see org.w3c.dom.Element#getAttribute(java.lang.String)
     */
    public String getAttribute(String name) {
        return getAttributeNode(name).getValue();
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNode(java.lang.String)
     */
    public Attr getAttributeNode(String name) {
        return (Attr)getAttributes().getNamedItem(name);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNodeNS(java.lang.String, java.lang.String)
     */
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        return (Attr)getAttributes().getNamedItemNS(namespaceURI, localName);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNS(java.lang.String, java.lang.String)
     */
    public String getAttributeNS(String namespaceURI, String localName) {
        return getAttributeNodeNS(namespaceURI, localName).getValue();
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagName(java.lang.String)
     */
    public NodeList getElementsByTagName(String name) {
        try {
            int nm = getNodeFactory().getName(name);
            return new Traversal(this, nm);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagNameNS(java.lang.String, java.lang.String)
     */
    public NodeList getElementsByTagNameNS(
        String namespaceURI,
        String localName) {
        try {
            int nm = getNodeFactory().getName(namespaceURI, localName);
            return new Traversal(this, nm);
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.Element#getTagName()
     */
    public String getTagName() {
        return getNodeName();
    }

    /**
     * @see org.w3c.dom.Element#hasAttribute(java.lang.String)
     */
    public boolean hasAttribute(String name) {
        return getAttributeNode(name) != null;
    }

    /**
     * @see org.w3c.dom.Element#hasAttributeNS(java.lang.String, java.lang.String)
     */
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return getAttributeNodeNS(namespaceURI, localName) != null;
    }

    /**
     * @see org.w3c.dom.Element#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNode(org.w3c.dom.Attr)
     */
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNS(java.lang.String, java.lang.String)
     */
    public void removeAttributeNS(String namespaceURI, String localName)
        throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Element#setAttribute(java.lang.String, java.lang.String)
     */
    public void setAttribute(String name, String value) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNode(org.w3c.dom.Attr)
     */
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNodeNS(org.w3c.dom.Attr)
     */
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNS(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setAttributeNS(
        String namespaceURI,
        String qualifiedName,
        String value)
        throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return ELEMENT_NODE;
    }

    public static class Traversal implements NodeList {
        private ArrayList nodes;
        
        public Traversal(NodeImpl root, int nm) {
            nodes = new ArrayList();
            walk(root, nm);
        }
        
        private void walk(NodeImpl node, int nm) {
            NodeList nl = node.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                NodeImpl child = (NodeImpl)nl.item(i);
                if (nm == child.getId()) {
                    nodes.add(child); 
                }
                walk(child, nm);
            }
        }

        /**
         * @see org.w3c.dom.NodeList#getLength()
         */
        public int getLength() {
            return nodes.size();
        }

        /**
         * @see org.w3c.dom.NodeList#item(int)
         */
        public Node item(int index) {
            return (Node)nodes.get(index);
        }
    }
    
    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {
        return new ElementImpl((NodeImpl)getParentNode(), getId(), getV());
    }

}
