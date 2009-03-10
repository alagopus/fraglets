/*
 * Environment.java -
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

import java.util.Hashtable;
import java.util.Properties;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
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
