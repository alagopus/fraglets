/*
 * zeigURLContextFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 17, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import net.sourceforge.fraglets.zeig.jndi.DOMContext;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class zeigURLContextFactory implements ObjectFactory {

    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
        // TODO no-args
        if (obj instanceof String) {
            obj = new String[] { (String)obj };
        }
        if (obj instanceof String[]) {
            Context urlCtx = createURLContext(environment);
            try {
                NamingException ne = null;
                String urls[] = (String[])obj;
                for (int i = 0; i < urls.length; i++) {
                    try {
                        return urlCtx.lookup(urls[i]);
                    } catch (NamingException e) {
                        ne = e;
                    }
                }
                throw ne;
            } finally {
                urlCtx.close();
            }
        }
        return null;
    }
    
    protected Context createURLContext(Hashtable env) throws NamingException {
        return new DOMContext(env);
    }

}
