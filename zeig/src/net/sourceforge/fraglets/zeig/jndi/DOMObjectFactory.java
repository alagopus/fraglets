/*
 * DOMObjectFactory.java -
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

import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import net.sourceforge.fraglets.zeig.zeigURLContext;
import net.sourceforge.fraglets.zeig.dom.DocumentImpl;
import net.sourceforge.fraglets.zeig.eclipse.CorePlugin;

import org.apache.xml.utils.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.16 $
 */
public class DOMObjectFactory implements ObjectFactory {

    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */
    public Object getObjectInstance(Object obj, Name name,
        Context ctx, Hashtable environment) throws NamingException {
        if (ctx instanceof DOMContext) {
            try {
                DOMContext dctx = (DOMContext)ctx;
                int ve = dctx.getVe((Element)obj);
                int id = getLatest(dctx, ve);
                boolean me = dctx.getMe((Element)obj);
                if (me) {
                    return dctx.connectionContext.getMediaFactory()
                        .getMedia(id);
                }
                Document doc = new DocumentImpl(id, dctx.connectionContext);
                if (isDOMContext(doc)) {
                    return new DOMContext(dctx, name.get(0), doc, ve);
                } else {
                    return transform(dctx, doc, name);
                }
            } catch (SQLException ex) {
                throw DOMContext.namingException(ex);
            }
        } else {
            return null;
        }
    }
    
    public static boolean isDOMContext(int id, DOMContext ctx) {
        try {
            return isDOMContext(new DocumentImpl(id, ctx.connectionContext));
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isDOMContext(Document d) {
        return d != null && isDOMContext(d.getDocumentElement());
    }
    
    public static boolean isDOMContext(Element e) {
        try {
            return e != null &&
            DOMContext.CONTEXT_NAMESPACE.equals(e.getNamespaceURI()) &&
            DOMContext.CONTEXT_TAGNAME.equals(e.getLocalName());
        } catch (NullPointerException ex) {
            return false;
        }
    }
    
    public static int getLatest(DOMContext ctx, int ve) throws NamingException {
        try {
            return ctx.connectionContext.getVersionFactory().getValue(ve);
        } catch (SQLException ex) {
            throw DOMContext.namingException(ex);
        }
    }
    
    public Object transform(final DOMContext ctx, Object obj, final Name name) {
        String media = ctx.getProperty(DOMContext.PRESENTATION_MEDIA);
        if (obj instanceof Document
            && (media == null || !media.equals("verbatim"))) {
            try {
                // 1. Instantiate the TransformerFactory.
                TransformerFactory tFactory = TransformerFactory.newInstance();
                // 2a. Get the stylesheet from the XML source.
                String title = null;
                String charset = ctx.getProperty(DOMContext.PRESENTATION_CHARSET);
                Source input = new DOMSource((Document)obj);
                URI base = new URI(ctx.getProperty(Context.PROVIDER_URL));
                base = new URI(base, ctx.getNameInNamespace()+"/"+name.get(0));
                input.setSystemId(base.toString());
                zeigURLContext urlCtx = zeigURLContext.getInstance(ctx);
                tFactory.setURIResolver(urlCtx);
                Source stylesheet =
                    tFactory.getAssociatedStylesheet(
                        input,
                        media,
                        title,
                        charset);

                if (stylesheet != null) {
                    // 2b. Process the stylesheet and generate a Transformer.
                    Transformer transformer = tFactory.newTransformer(stylesheet);

                    // 3. Use the Transformer to perform the transformation and send the
                    //    the output to a Result object.
                    DOMResult output = new DOMResult();
                    transformer.transform(input, output);
                    obj = output.getNode();
                }
            } catch (Exception e) {
                CorePlugin.error("transform", e);
            }
        }
        return obj;
    }

}
