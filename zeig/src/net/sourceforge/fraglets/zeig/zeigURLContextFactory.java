/*
 * zeigURLContextFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 17, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig;

import java.util.Hashtable;

import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class zeigURLContextFactory implements ObjectFactory {

    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
        Context ctx = createURLContext(environment);
        if (obj == null) {
            return ctx;
        }
        try {
            if (obj instanceof String) {
                return ctx.lookup((String)obj);
            }
            if (obj instanceof String[]) {
                String urls[] = (String[])obj;
                if (urls.length == 0) {
                    throw new ConfigurationException("empty URL array");
                }
                NamingException ne = null;
                for (int i = 0; i < urls.length; i++) {
                    try {
                        return ctx.lookup(urls[i]);
                    } catch (NamingException e) {
                        ne = e;
                    }
                }
                throw ne;
            }
        } finally {
            ctx.close();
        }
        throw new IllegalArgumentException
            ("first argument must be String or String[]");
    }
    
    protected Context createURLContext(Hashtable env) throws NamingException {
        return new zeigURLContext(env);
    }

}
