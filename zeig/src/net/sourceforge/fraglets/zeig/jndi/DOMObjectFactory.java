/*
 * DOMObjectFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fraglets.zeig.dom.DocumentImpl;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.5 $
 */
public class DOMObjectFactory implements ObjectFactory {

    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */
    public Object getObjectInstance(Object obj, Name name,
        Context ctx, Hashtable environment) throws NamingException {
        if (ctx instanceof DOMContext) {
            int ve = DOMContext.getVe((Element)obj);
            int id = DOMContext.getLatest(ve);
            Document doc = new DocumentImpl(id);
            if (isDOMContext(doc)) {
                return new DOMContext((DOMContext)ctx, name.get(0), doc, ve);
            } else {
                return doc;
            }
        } else {
            return null;
        }
    }

    public static boolean isDOMContext(Document d) {
        return d != null && isDOMContext(d.getDocumentElement());
    }
    
    public static boolean isDOMContext(Element e) {
        return e != null &&
        DOMContext.CONTEXT_NAMESPACE.equals(e.getNamespaceURI()) &&
        DOMContext.CONTEXT_TAGNAME.equals(e.getLocalName());
    }
}
