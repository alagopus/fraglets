/*
 * DOMContextFactory.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 10, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import net.sourceforge.fraglets.zeig.zeigURLContextFactory;

import org.apache.log4j.Category;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.4 $
 */
public class DOMContextFactory extends zeigURLContextFactory implements InitialContextFactory {

    /**
     * @see javax.naming.spi.InitialContextFactory#getInitialContext(java.util.Hashtable)
     */
    public Context getInitialContext(Hashtable environment) throws NamingException {
        Category.getInstance(DOMContext.class).debug("initializing DOMContext");
        return (Context)createURLContext(environment)
            .lookup((String)environment.get(Context.PROVIDER_URL));
    }

}
