/*
 * TransformingURIResolver.java -
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
package net.sourceforge.fraglets.zeig.eclipse;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.xml.utils.URI;
import org.w3c.dom.Document;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class TransformingURIResolver implements URIResolver {
    private URI root;
    private Context ctx;
    private String media;
    private String charset;
    
    public TransformingURIResolver(Context ctx) throws NamingException {
        try {
            this.ctx = ctx;
            this.root = new URI((String)ctx.getEnvironment()
                .get(Context.PROVIDER_URL));
        } catch (URI.MalformedURIException e) {
            NamingException ex = new NamingException("provider URL");
            ex.setRootCause(e);
            throw ex;
        }
    }
    
    public Object lookup(Context ctx, String name) throws TransformerException, NamingException {
        try {
            return transform(ctx.lookup(name), new URI(root, ctx.getNameInNamespace()+'/'+name));
        } catch (URI.MalformedURIException e) {
            NamingException ex = new NamingException("lookup URL");
            ex.setRootCause(e);
            throw ex;
        }
    }
    
    /**
     * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
     */
    public Source resolve(String href, String base) throws TransformerException {
        try {
            URI uri = base == null ? null : new URI(base);
            uri = new URI(uri, href);
            Object doc = ctx.lookup(uri.toString());
            return new DOMSource((Document)transform(doc, uri));
        } catch (URI.MalformedURIException e) {
            throw new TransformerException(e);
        } catch (NamingException e) {
            throw new TransformerException(e);
        }
    }
    
    public Object transform(Object obj, URI base) throws NamingException {
        if (obj instanceof Document) {
            try {
                TransformerFactory tFactory = TransformerFactory.newInstance();
                String title = null;
                Source input = new DOMSource((Document)obj);
                input.setSystemId(base.toString());
                tFactory.setURIResolver(this);
                Source stylesheet =
                    tFactory.getAssociatedStylesheet(
                        input,
                        media,
                        title,
                        charset);

                if (stylesheet != null) {
                    CorePlugin.info("transforming "+input.getSystemId()
                        +" with "+stylesheet.getSystemId(), null);
                    Transformer transformer = tFactory.newTransformer(stylesheet);
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
    
    /**
     * @return
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @return
     */
    public String getMedia() {
        return media;
    }

    /**
     * @param string
     */
    public void setCharset(String string) {
        charset = string;
    }

    /**
     * @param string
     */
    public void setMedia(String string) {
        media = string;
    }

}