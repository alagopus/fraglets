/*
 * TextImpl.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.dom;

import java.sql.SQLException;

import net.sourceforge.fraglets.zeig.model.PlainTextFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class TextImpl extends NodeImpl implements Text {

    /**
     * @param parent
     * @param id
     * @param v
     */
    public TextImpl(NodeImpl parent, int id, int v) {
        super(parent, id, v);
    }

    /**
     * @see org.w3c.dom.Text#splitText(int)
     */
    public Text splitText(int offset) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.CharacterData#appendData(java.lang.String)
     */
    public void appendData(String arg) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.CharacterData#deleteData(int, int)
     */
    public void deleteData(int offset, int count) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.CharacterData#getData()
     */
    public String getData() throws DOMException {
        try {
            return PlainTextFactory.getInstance().getPlainText(getV());
        } catch (SQLException ex) {
            throw domException(ex);
        }
    }

    /**
     * @see org.w3c.dom.CharacterData#getLength()
     */
    public int getLength() {
        return getData().length();
    }

    /**
     * @see org.w3c.dom.CharacterData#insertData(int, java.lang.String)
     */
    public void insertData(int offset, String arg) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.CharacterData#replaceData(int, int, java.lang.String)
     */
    public void replaceData(int offset, int count, String arg)
        throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.CharacterData#setData(java.lang.String)
     */
    public void setData(String data) throws DOMException {
        throw notImplemented();
    }

    /**
     * @see org.w3c.dom.CharacterData#substringData(int, int)
     */
    public String substringData(int offset, int count) throws DOMException {
        return getData().substring(offset, count);
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return TEXT_NODE;
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {
        return new TextImpl((NodeImpl)getParentNode(), getId(), getV());
    }

}
