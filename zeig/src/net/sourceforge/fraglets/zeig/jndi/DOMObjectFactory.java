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
 * @version $Revision: 1.2 $
 */
public class DOMObjectFactory implements ObjectFactory {

    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */
    public Object getObjectInstance(Object obj, Name name,
        Context ctx, Hashtable environment) throws NamingException {
        if (ctx instanceof DOMContext) {
            Document doc = new DocumentImpl(DOMContext.getLatest((Element)obj));
            if (DOMContext.CONTEXT_NAMESPACE.equals(doc.getDocumentElement().getNamespaceURI()) &&
                "context".equals(doc.getDocumentElement().getLocalName())) {
                return new DOMContext((DOMContext)ctx, name.get(0), doc);
            } else {
                return doc;
            }
        } else {
            return null;
        }
    }

}
