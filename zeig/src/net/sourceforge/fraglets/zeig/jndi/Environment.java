/*
 * Environment.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 20, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.jndi;

import java.util.Hashtable;
import java.util.Properties;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class Environment extends Properties {
    /**
     * 
     */
    public Environment() {
    }

    /**
     * @param defaults
     */
    public Environment(Properties defaults) {
        super(defaults);
    }

    public Hashtable copyInto(Hashtable t) {
        t.putAll(this);
        if (defaults instanceof Environment) {
            ((Environment)defaults).copyInto(t);
        } else if (defaults != null) {
            t.putAll(defaults);
        }
        return t;
    }
}
