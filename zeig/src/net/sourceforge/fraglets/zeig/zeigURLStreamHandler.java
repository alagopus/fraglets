/*
 * zeigURLStreamHandler.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 */
package net.sourceforge.fraglets.zeig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.jndi.DOMContext;
import net.sourceforge.fraglets.zeig.jndi.DOMObjectFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

/**
 * @see URLStreamHandler
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class zeigURLStreamHandler extends URLStreamHandler {
    Context ctx;
    /**
     * Create a new URL stream handler for the given context.
     */
    public zeigURLStreamHandler(Context ctx) throws NamingException {
        this.ctx = (Context)ctx.lookup((Name)null);
        ctx.addToEnvironment(DOMContext.OBJECT_FACTORIES, URLStreamObjectFactory.class.getName());
    }
    
    /**
     * @see URLStreamHandler#openConnection
     */
    protected URLConnection openConnection(URL u) throws IOException {
        if (u.getProtocol().equals("zeig")) {
            try {
                return new zeigURLConnection(u, (InputStream)ctx.lookup(u.toExternalForm()));
            } catch (NamingException ex) {
                throw new RemoteException("failed to lookup "+u, ex);
            } catch (ClassCastException ex) {
                // not a leaf node
                // TODO implement context listing
                return null;
            }
        } else {
            return null;
        }
    }
    
    public static class URLStreamObjectFactory extends DOMObjectFactory {
        private XMLSerializer serializer;
        
        public URLStreamObjectFactory() {
            serializer = new XMLSerializer();
            OutputFormat of = new OutputFormat();
            of.setMediaType("text/xml");
            of.setEncoding("UTF-8");
            of.setIndenting(true);
            serializer.setOutputFormat(of);
        }
        /**
         * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
         */
        public Object getObjectInstance(
            Object obj,
            Name name,
            Context ctx,
            Hashtable environment)
            throws NamingException {
            Object intermediate = super.getObjectInstance(obj, name, ctx, environment);
            try {
                return toStream(intermediate);
            } catch (IOException ex) {
                NamingException ne = new NamingException("serializing");
                ne.setRootCause(ex);
                throw ne;
            }
        }

        public InputStream toStream(Object o) throws IOException {
            if (o instanceof InputStream) {
                return (InputStream)o;
            } else if (o instanceof Document) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    serializer.setOutputByteStream(buffer);
                    serializer.asDOMSerializer().serialize((Document)o);
                    return new ByteArrayInputStream(buffer.toByteArray());
                } finally {
                    serializer.reset();
                }
            } else {
                throw new IOException("cannot convert to stream: "+o.getClass());
            }
        }
    }
    
}
