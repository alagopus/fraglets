/*
 * Import.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 20, 2003 by marion@users.souceforge.net
 */
package net.sourceforge.fraglets.zeig;

import java.io.File;
import java.io.FileNotFoundException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.cache.SensorCache;
import net.sourceforge.fraglets.zeig.jndi.DOMContext;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 * @author marion@users.souceforge.net
 * @version $Revision: 1.2 $
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
                CATEGORY.debug("creating subcontext " + file.getName());
                subCtx = ctx.createSubcontext(file.getName());
            }
            for (int i = 0; i < list.length; i++) {
                process(list[i], subCtx);
            }
        } else {
            try {
                CATEGORY.debug("reading " + file.getName());
                ctx.rebind(file.getName(), file);
            } catch (NamingException ex) {
                CATEGORY.error("failed to import", ex);
            }
        }
    }
    
    public static void main(String args[]) {
        try {
            for (int i = 0; i < args.length; i++) {
                process(new File(args[i]), new InitialContext(System.getProperties()));
            }
            SensorCache.logStatistics(Priority.INFO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static final Category CATEGORY = Category.getInstance(Import.class);
}
