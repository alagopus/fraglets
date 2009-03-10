/*
 * Import.java -
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
package net.sourceforge.fraglets.zeig;

import java.io.File;
import java.io.FileNotFoundException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.eclipse.CorePlugin;
import net.sourceforge.fraglets.zeig.jndi.DOMContext;

/**
 * @author marion@users.souceforge.net
 * @version $Revision: 1.4 $
 */
public class Import {

    public static void process(File file, Context ctx)
        throws NamingException, FileNotFoundException {
        ctx.addToEnvironment(
            DOMContext.VERSION_COMMENT,
            "imported from " + file);
        if (file.isDirectory()) {
            Context subCtx;
            File list[] = file.listFiles();
            try {
                subCtx = (Context)ctx.lookup(file.getName());
            } catch (ClassCastException ex) {
                // not a subcontext
                throw new NamingException(
                    "not a subcontext: " + file.getName());
            } catch (NameNotFoundException ex) {
                // ignore
                subCtx = ctx.createSubcontext(file.getName());
            }
            for (int i = 0; i < list.length; i++) {
                process(list[i], subCtx);
            }
        } else {
            try {
                ctx.rebind(file.getName(), file);
            } catch (NamingException ex) {
                CorePlugin.error("failed to import", ex);
            }
        }
    }
    
    public static void main(String args[]) {
        try {
            for (int i = 0; i < args.length; i++) {
                process(new File(args[i]), new InitialContext(System.getProperties()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
