/*
 * DOMStateFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;

import net.sourceforge.fraglets.zeig.model.NodeFactory;
import net.sourceforge.fraglets.zeig.model.SAXFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.5 $
 */
public class DOMStateFactory implements StateFactory {
    /**
     * @see javax.naming.spi.StateFactory#getStateToBind(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */
    public Object getStateToBind(Object obj, Name name,
        Context ctx, Hashtable environment) throws NamingException {
        if (ctx instanceof DOMContext) {
            DOMContext dctx = (DOMContext)ctx;
            try {
                int id;
                NodeFactory nf = dctx.sharedContext.getNodeFactory();
                if (obj instanceof Document) {
                    id = nf.getId((Document)obj);
                } else if (obj instanceof DOMContext) {
                    id = nf.getId(((DOMContext)obj).getBinding());
                } else {
                    InputSource in;
                    if (obj instanceof InputSource) {
                        in = (InputSource)obj;
                    } else if (obj instanceof String) {
                        in = new InputSource((String)obj);
                    } else {
                        throw new IllegalArgumentException
                            ("invalid binding: " + obj);
                    }
                    id = new SAXFactory(nf).parse(in);
                }
                
                String comment = (String)environment
                    .get(DOMContext.VERSION_COMMENT);
                if (comment == null) {
                    comment = "";
                }
                int co = dctx.sharedContext.getPlainTextFactory().getPlainText(comment);
                
                String atom = name.get(0);
                Element binding = dctx.lookupElement(atom);
                if (binding == null) {
                    int ve = dctx.sharedContext.getVersionFactory()
                        .createVersion(id, co);
                    String localName = DOMObjectFactory.isDOMContext(id, dctx)
                        ? DOMContext.CONTEXT_TAGNAME
                        : DOMContext.BINDING_TAGNAME;
                    Document doc = new org.apache.xerces.dom.DocumentImpl();
                    binding = doc.createElementNS(DOMContext.CONTEXT_NAMESPACE, localName);
                    binding.setAttributeNS("", "id", atom);
                    binding.setAttributeNS(DOMContext.CONTEXT_NAMESPACE, "ve", String.valueOf(ve));
                } else {
                    int ve = dctx.getVe(binding);
                    dctx.sharedContext.getVersionFactory().addVersion(ve, id, co);
                }
                
                return binding;
            } catch (Exception ex) {
                throw DOMContext.namingException(ex);
            }
        } else {
            return null;
        }
    }

}
