/*
 * DOMStateFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.jndi;

import java.io.File;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;
import net.sourceforge.fraglets.zeig.model.NodeFactory;
import net.sourceforge.fraglets.zeig.model.PlainTextFactory;
import net.sourceforge.fraglets.zeig.model.SAXFactory;
import net.sourceforge.fraglets.zeig.model.VersionFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
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
                if (obj instanceof Document) {
                    id = NodeFactory.getInstance().getId((Document)obj);
                } else {
                    InputSource in;
                    if (obj instanceof File) {
                        in = new InputSource(((File)obj).toURL().toExternalForm());
                    } else if (obj instanceof String) {
                        in = new InputSource((String)obj);
                    } else {
                        throw new IllegalArgumentException
                            ("invalid binding: " + obj);
                    }
                    SAXFactory sf = new SAXFactory(ConnectionFactory.getInstance());
                    sf.parse(in);
                    id = sf.getLastResult();
                }
                
                String comment = (String)environment
                    .get(DOMContext.VERSION_COMMENT);
                if (comment == null) {
                    comment = "";
                }
                int co = PlainTextFactory.getInstance().getPlainText(comment);
                
                String atom = name.get(0);
                Element binding = dctx.lookupElement(atom);
                if (binding == null) {
                    int ve = VersionFactory.getInstance()
                        .createVersion(id, co);
                    binding = dctx.getBinding().createElement("binding");
                    binding.setAttribute("id", atom);
                    binding.setAttribute("ve", String.valueOf(ve));
                } else {
                    int ve = DOMContext.getVe(binding);
                    VersionFactory.getInstance().addVersion(ve, id, co);
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
