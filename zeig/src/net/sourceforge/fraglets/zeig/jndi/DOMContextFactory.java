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

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class DOMContextFactory implements InitialContextFactory {

    /**
     * @see javax.naming.spi.InitialContextFactory#getInitialContext(java.util.Hashtable)
     */
    public Context getInitialContext(Hashtable environment) throws NamingException {
        return new DOMContext(environment);
    }

}
