/*
 * AttrImpl.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class AttrImpl extends NodeImpl implements Attr {
    
    public AttrImpl(NodeImpl owner, int nm, int v) {
        super(owner, nm, v);
    }
    
    public int getNm() {
        return getId();
    }
    
    /**
     * @see org.w3c.dom.Attr#getName()
     */
    public String getName() {
        return getNodeName();
    }

    /**
     * @see org.w3c.dom.Attr#getOwnerElement()
     */
    public Element getOwnerElement() {
        return (Element)getParentNode();
    }

    /**
     * Always
     * @see org.w3c.dom.Attr#getSpecified()
     */
    public boolean getSpecified() {
        return true;
    }

    /**
     * @see org.w3c.dom.Attr#getValue()
     */
    public String getValue() {
        return getNodeValue();
    }

    /**
     * @see org.w3c.dom.Attr#setValue(java.lang.String)
     */
    public void setValue(String value) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return ATTRIBUTE_NODE;
    }
    
    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {
        return new AttrImpl((NodeImpl)getParentNode(), getId(), getV());
    }

}
