/*
 * $Id: ANTBootstrapper.java,v 1.1 2004-03-01 19:36:35 marion Exp $
 *
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
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
package net.sf.fraglets.cca;

import java.util.Collections;

import net.sourceforge.cruisecontrol.Bootstrapper;
import net.sourceforge.cruisecontrol.CruiseControlException;
import net.sourceforge.cruisecontrol.builders.AntBuilder;

/**
 * Simple ANT bootstrapper using the standard ANT builder.
 * @since 01.03.2004
 * @author Klaus Rennecke
 * @version $Revision: 1.1 $
 */
public class ANTBootstrapper extends AntBuilder implements Bootstrapper {

    /**
     * @see net.sourceforge.cruisecontrol.Bootstrapper#bootstrap()
     */
    public void bootstrap() throws CruiseControlException {
        build(Collections.EMPTY_MAP);
    }

}
