/*
 * NodeFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 6, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author unknown
 */
public class NodeFactory implements NodeTags {
    protected PlainTextFactory pt;
    protected NamespaceFactory ns;
    protected NameFactory nm;
    protected AttributeFactory at;
    protected XMLTextFactory xt;
    
    protected int text = 0;
    protected int attr = 0;
    protected int pi = 0;
    
    public NodeFactory(ConnectionFactory cf) {
        this.pt = new PlainTextFactory(cf);
        this.ns = new NamespaceFactory(cf);
        this.nm = new NameFactory(cf);
        this.at = new AttributeFactory(cf);
        this.xt = new XMLTextFactory(cf);
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
        return nm.getName(getNamespace(uri), pt.getPlainText(name));
    }
    
    public int getAttribute(String uri, String name, String value) throws SQLException {
        if (uri == null) {
            uri = "";
        }
        return at.getAttribute(getName(uri, name), pt.getPlainText(value));
    }
    
    public int getText(String text) throws SQLException {
        return xt.getXMLText(getText(), 0, new int[] {pt.getPlainText(text)});
    }
    
    public int getNode(int name, int atts, int nodes[]) throws SQLException {
        return xt.getXMLText(name, atts, nodes);
    }
    
    public int getProcessingInstruction(String name, String value) throws SQLException {
        return xt.getXMLText(getPi(), 0,
            new int[] {pt.getPlainText(name), pt.getPlainText(value)});
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
    public int getPi() throws SQLException {
        if (pi == 0) {
            pi = getName("", NODE_PROCESSING_INSTRUCTION);
        }
        return pi;
    }

}
