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

import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.TraceListener;
import org.apache.xalan.trace.TracerEvent;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializerTrace;
import org.apache.xml.utils.URI;
import org.w3c.dom.Document;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.3 $
 */
public class TransformingURIResolver implements URIResolver, TraceListener {
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
            trace("lookup "+name);
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
            trace("resolve "+href+" from "+base);
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
                trace("transform "+obj);
                TransformerFactory factory = TransformerFactory.newInstance();
                String title = null;
                Source input = new DOMSource((Document)obj);
                input.setSystemId(base.toString());
                factory.setURIResolver(this);
                Source stylesheet =
                factory.getAssociatedStylesheet(
                        input,
                        media,
                        title,
                        charset);

                if (stylesheet != null) {
                    CorePlugin.info("transforming "+((DOMSource)input).getNode()
                        +" with "+((DOMSource)stylesheet).getNode(), null);
                    Transformer transformer = factory.newTransformer(stylesheet);
                    try {
                        ((TransformerImpl)transformer).getTraceManager()
                            .addTraceListener(this);
                    } catch (ClassCastException ex) {
                        CorePlugin.info("cannot trace "+transformer.getClass().getName(), null);
                    }
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

    /* (non-Javadoc)
     * @see org.apache.xalan.trace.TraceListener#trace(org.apache.xalan.trace.TracerEvent)
     */
    public void trace(TracerEvent e) {
        trace("trace "+TracerEvent.printNode(e.m_sourceNode)
            +" -("+TracerEvent.printNode(e.m_styleNode)+")-> ...");
    }

    /* (non-Javadoc)
     * @see org.apache.xalan.trace.TraceListener#selected(org.apache.xalan.trace.SelectionEvent)
     */
    public void selected(SelectionEvent e) throws TransformerException {
        trace("selected "+e.m_xpath.getPatternString()+"("+TracerEvent.printNode(e.m_sourceNode)+")  -> "+e.m_selection);
    }

    /* (non-Javadoc)
     * @see org.apache.xalan.trace.TraceListener#generated(org.apache.xalan.trace.GenerateEvent)
     */
    public void generated(GenerateEvent e) {
        switch (e.m_eventtype)
        {
        case SerializerTrace.EVENTTYPE_STARTDOCUMENT :
          trace("generated STARTDOCUMENT");
          break;
        case SerializerTrace.EVENTTYPE_ENDDOCUMENT :
          trace("generated ENDDOCUMENT");
          break;
        case SerializerTrace.EVENTTYPE_STARTELEMENT :
          trace("generated STARTELEMENT: " + e.m_name);
          if (e.m_atts != null) {
              int end = e.m_atts.getLength();
              for (int i = 0; i < end; i++) {
                  trace ("generated ATTRIBUTE: "
                      +e.m_atts.getQName(i)
                      +"="+e.m_atts.getValue(i));
              }
          }
          break;
        case SerializerTrace.EVENTTYPE_ENDELEMENT :
          trace("generated ENDELEMENT: " + e.m_name);
          break;
        case SerializerTrace.EVENTTYPE_CHARACTERS :
        {
          String chars = new String(e.m_characters, e.m_start, e.m_length);

          trace("generated CHARACTERS: " + chars);
        }
        break;
        case SerializerTrace.EVENTTYPE_CDATA :
        {
          String chars = new String(e.m_characters, e.m_start, e.m_length);

          trace("generated CDATA: " + chars);
        }
        break;
        case SerializerTrace.EVENTTYPE_COMMENT :
          trace("generated COMMENT: " + e.m_data);
          break;
        case SerializerTrace.EVENTTYPE_PI :
          trace("generated PI: " + e.m_name + ", " + e.m_data);
          break;
        case SerializerTrace.EVENTTYPE_ENTITYREF :
          trace("generated ENTITYREF: " + e.m_name);
          break;
        case SerializerTrace.EVENTTYPE_IGNORABLEWHITESPACE :
          trace("generated IGNORABLEWHITESPACE");
          break;
        }
    }
    
    protected void trace(String str) {
        CorePlugin.info(str, null);
    }

}