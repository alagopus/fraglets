/*
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 */
package net.sourceforge.fraglets.zeig.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.zeigURLStreamHandler;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class CorePlugin extends AbstractUIPlugin {
    //The shared instance.
    private static CorePlugin plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;
    
    private URL classPath[];
    private zeigURLStreamHandler.URLStreamObjectFactory streamObjectFactory;

    /**
     * The constructor.
     */
    public CorePlugin(IPluginDescriptor descriptor) {
        super(descriptor);
        plugin = this;
        try {
            resourceBundle =
                ResourceBundle.getBundle(
                    "net.sourceforge.fraglets.zeig.eclipse.CoreResources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        
        ClassLoader pcl = getClass().getClassLoader();
        ArrayList cp = new ArrayList();
        URL urls[];
        while (pcl instanceof URLClassLoader) {
            urls = ((URLClassLoader)pcl).getURLs();
            for (int i = 0; i < urls.length; i++) {
                cp.add(urls[i]);
            }
            pcl = pcl.getParent();
        }
        classPath = (URL[])cp.toArray(new URL[cp.size()]);
        
//      for (int i = 0; i < classPath; i++) {
//          System.out.println("ClassLoader URL: "+classPath[i]);
//      }

        streamObjectFactory = new zeigURLStreamHandler.URLStreamObjectFactory();
    }

    /**
     * Returns the shared instance.
     */
    public static CorePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = CorePlugin.getDefault().getResourceBundle();
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public Context newContext(final Hashtable env) throws NamingException {
        return (Context)doNamingAction(new NamingAction() {
            public Object runNamingAction() throws NamingException {
                return new InitialContext(env);
            }
        });
    }
    
    public Object lookup(final Context ctx, final String name) throws NamingException {
        return doNamingAction(new NamingAction() {
            public Object runNamingAction() throws NamingException {
                return ctx.lookup(name);
            }
        });
    }
    
    public NamingEnumeration list(final Context ctx, final String name) throws NamingException {
        return (NamingEnumeration)doNamingAction(new NamingAction() {
            public Object runNamingAction() throws NamingException {
                return ctx.list(name);
            }
        });
    }
    
    public void bind(final Context ctx, final String name, final Object value) throws NamingException {
        doNamingAction(new NamingAction() {
            public Object runNamingAction() throws NamingException {
                ctx.bind(name, value);
                return null;
            }
        });
    }
    
    public void createSubContext(final Context ctx, final String name) throws NamingException {
        doNamingAction(new NamingAction() {
            public Object runNamingAction() throws NamingException {
                ctx.createSubcontext(name);
                return null;
            }
        });
    }
    
    public InputStream lookupStream(final Context ctx, final String name) throws NamingException {
        return (InputStream)doNamingAction(new NamingAction() {
            public Object runNamingAction() throws NamingException {
                try {
                    Object o = ctx.lookup(name);
                    return new zeigURLStreamHandler.URLStreamObjectFactory().toStream(o);
                } catch (IOException ex) {
                    return ex;
                }
            }
        });
    }
    
    
    
    public Object doNamingAction(NamingAction action) throws NamingException {
        Object result = AccessController.doPrivileged(action);
        if (result instanceof NamingException) {
            throw (NamingException)result;
        } else {
            return result;
        }
    }
    
    public abstract class NamingAction implements PrivilegedAction {
        protected abstract Object runNamingAction() throws NamingException;
        
        public final Object run() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader
                    (new URLClassLoader(classPath, cl));
                return runNamingAction();
            } catch (NamingException ex) {
                return ex;
            } finally {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }
}
