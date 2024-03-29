/*
 * DOMStateFactory.java -
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
package net.sourceforge.fraglets.zeig.jndi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Hashtable;

import javax.activation.FileTypeMap;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;

import net.sourceforge.fraglets.zeig.model.MediaFactory;
import net.sourceforge.fraglets.zeig.model.NodeFactory;
import net.sourceforge.fraglets.zeig.model.SAXFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.10 $
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
                boolean me = false; // media object?
                NodeFactory nf = dctx.connectionContext.getNodeFactory();
                if (obj instanceof Document) {
                    id = nf.getId((Document)obj);
                } else if (obj instanceof DOMContext) {
                    id = nf.getId(((DOMContext)obj).getBinding());
                } else if (obj instanceof Element) {
                    int ve = dctx.getVe((Element)obj);
                    id = DOMObjectFactory.getLatest(dctx, ve);
                    return createBinding(dctx, id, atom, ve, me);
                } else if (obj instanceof File) {
                    File file = (File)obj;
                    FileInputStream in = new FileInputStream(file);
                    try {
                        InputSource is = new InputSource(in);
                        is.setSystemId(file.toString());
                        id = new SAXFactory(nf, true).parse(is);
                    } catch (SAXException ex) {
                        MediaFactory.RetryInputException rex = null;
                        MediaFactory mf = dctx.connectionContext.getMediaFactory();
                        String type = FileTypeMap.getDefaultFileTypeMap()
                            .getContentType(file);
                        do {
                            in.close();
                            in = new FileInputStream(file);
                            try {
                                if (rex == null) {
                                    id = mf.getMedia(in, type);
                                } else {
                                    id = rex.retry(in);
                                    rex = null;
                                    me = true;
                                }
                            } catch (MediaFactory.RetryInputException rexx) {
                                rex = rexx;
                                id = 0; // compiler wants it
                            }
                        } while (rex != null);
                    } finally {
                        in.close();
                    }
                } else {
                    InputSource in;
                    if (obj instanceof InputSource) {
                        in = (InputSource)obj;
                    } else if (obj instanceof InputStream) {
                        in = new InputSource((InputStream)obj);
                    } else if (obj instanceof String) {
                        in = new InputSource(new StringReader((String)obj));
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
                    binding = createBinding(dctx, id, atom, ve, me);
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
    
    private Element createBinding(DOMContext dctx, int id, String atom, int ve, boolean me) {
        String localName = (!me && DOMObjectFactory.isDOMContext(id, dctx))
            ? DOMContext.CONTEXT_TAGNAME
            : DOMContext.BINDING_TAGNAME;
        Document doc = new org.apache.xerces.dom.DocumentImpl();
        Element binding = doc.createElementNS(DOMContext.CONTEXT_NAMESPACE, localName);
        binding.setAttributeNS("", "id", atom);
        binding.setAttributeNS(DOMContext.CONTEXT_NAMESPACE, "ve", String.valueOf(ve));
        binding.setAttributeNS(DOMContext.CONTEXT_NAMESPACE, "me", String.valueOf(me));
        return binding;
    }

}
