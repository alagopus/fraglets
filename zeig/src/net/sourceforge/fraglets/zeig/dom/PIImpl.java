/*
 * PIImpl.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.dom;

import java.sql.SQLException;

import net.sourceforge.fraglets.zeig.model.PlainTextFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * @author unknown
 */
public class PIImpl extends NodeImpl implements ProcessingInstruction {

    /**
     * @param parent
     * @param id
     * @param v
     */
    public PIImpl(NodeImpl parent, int id, int v) {
        super(parent, id, v);
    }

    /**
     * @see org.w3c.dom.ProcessingInstruction#getData()
     */
    public String getData() {
        try {
            return PlainTextFactory.getInstance().getPlainText(getV());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.ProcessingInstruction#getTarget()
     */
    public String getTarget() {
        try {
            return PlainTextFactory.getInstance().getPlainText(getId());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.ProcessingInstruction#setData(java.lang.String)
     */
    public void setData(String data) throws DOMException {
        throw noModification();
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return PROCESSING_INSTRUCTION_NODE;
    }

    /**
     * @see org.w3c.dom.Node#getNodeName()
     */
    public String getNodeName() {
        return getTarget();
    }

    /**
     * @see org.w3c.dom.Node#getNodeValue()
     */
    public String getNodeValue() throws DOMException {
        return getData();
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {
        return new PIImpl((NodeImpl)getParentNode(), getId(), getV());
    }

}
