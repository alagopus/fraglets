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
 * @version $Revision: 1.7 $
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
                String atom = name.get(0);
                NodeFactory nf = dctx.connectionContext.getNodeFactory();
                if (obj instanceof Document) {
                    id = nf.getId((Document)obj);
                } else if (obj instanceof DOMContext) {
                    id = nf.getId(((DOMContext)obj).getBinding());
                } else if (obj instanceof Element) {
                    int ve = dctx.getVe((Element)obj);
                    id = DOMObjectFactory.getLatest(dctx, ve);
                    return createBinding(dctx, id, atom, ve);
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
                int co = dctx.connectionContext.getPlainTextFactory().getPlainText(comment);
                
                Element binding = dctx.lookupElement(atom);
                if (binding == null) {
                    int ve = dctx.connectionContext.getVersionFactory()
                        .createVersion(id, co);
                    binding = createBinding(dctx, id, atom, ve);
                } else {
                    int ve = dctx.getVe(binding);
                    dctx.connectionContext.getVersionFactory().addVersion(ve, id, co);
                }
                
                return binding;
            } catch (Exception ex) {
                throw DOMContext.namingException(ex);
            }
        } else {
            return null;
        }
    }
    
    private Element createBinding(DOMContext dctx, int id, String atom, int ve) {
        String localName = DOMObjectFactory.isDOMContext(id, dctx)
            ? DOMContext.CONTEXT_TAGNAME
            : DOMContext.BINDING_TAGNAME;
        Document doc = new org.apache.xerces.dom.DocumentImpl();
        Element binding = doc.createElementNS(DOMContext.CONTEXT_NAMESPACE, localName);
        binding.setAttributeNS("", "id", atom);
        binding.setAttributeNS(DOMContext.CONTEXT_NAMESPACE, "ve", String.valueOf(ve));
        return binding;
    }

}
