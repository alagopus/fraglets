/*
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
package net.sourceforge.fraglets.zeig.eclipse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.zeigURLContext;
import net.sourceforge.fraglets.zeig.zeigURLStreamHandler;
import net.sourceforge.fraglets.zeig.cache.LazyCache;
import net.sourceforge.fraglets.zeig.cache.SensorCache;
import net.sourceforge.fraglets.zeig.cache.SimpleCache;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.w3c.dom.Document;

/**
 * The core plugin class to be used by the interface plugins.
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.6 $
 */
public class CorePlugin extends AbstractUIPlugin {
    /** The plugin id. */
    public static final String ID = "net.sourceforge.fraglets.zeig.core";
    
    public static boolean OPTION_WARN;
    public static boolean OPTION_INFO;
    
    //The shared instance.
    private static CorePlugin plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;
    
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
    
    public static void info(String message, Throwable ex) {
        if (OPTION_INFO) {
            getDefault().getLog().log
                (new Status(IStatus.INFO, ID, IStatus.OK, message, ex));
        }
    }
    
    public static void warn(String message, Throwable ex) {
        if (OPTION_WARN) {
            getDefault().getLog().log
                (new Status(IStatus.WARNING, ID, IStatus.OK, message, ex));
        }
    }
    
    public static void error(String message, Throwable ex)
    {
        getDefault().getLog().log
            (new Status(IStatus.ERROR, ID, IStatus.OK, message, ex));
    }
    
    public static SimpleCache newCache(String id) {
        if (OPTION_INFO) {
            return new SensorCache(id);
        } else {
            return new LazyCache();
        }
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
                    TransformingURIResolver resolver = new TransformingURIResolver(zeigURLContext.getInstance(ctx));
                    Object result = resolver.lookup(ctx, name);
                    XMLSerializer serializer = new XMLSerializer();
                    OutputFormat of = new OutputFormat();
                    of.setMediaType("text/xml");
                    of.setEncoding("UTF-8");
                    of.setIndenting(true);
                    serializer.setOutputFormat(of);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    serializer.setOutputByteStream(buffer);
                    serializer.asDOMSerializer().serialize((Document)result);
                    return new ByteArrayInputStream(buffer.toByteArray());
                } catch (Exception ex) {
                    return ex;
                }
            }
        });
    }
    
    
    
    Object doNamingAction(NamingAction action) throws NamingException {
        Object result = AccessController.doPrivileged(action);
        if (result instanceof NamingException) {
            throw (NamingException)result;
        } else {
            return result;
        }
    }
    
    abstract class NamingAction implements PrivilegedAction {
        protected abstract Object runNamingAction() throws NamingException;
        
        public final Object run() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
              Thread.currentThread().setContextClassLoader
                  (CorePlugin.class.getClassLoader());
                return runNamingAction();
            } catch (NamingException ex) {
                return ex;
            } catch (Error ex) {
                ex.printStackTrace(System.err);
                throw ex;
            } finally {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }
    
    /**
     * @see org.eclipse.core.runtime.Plugin#startup()
     */
    public void startup() throws CoreException {
        super.startup();
        String t = Boolean.TRUE.toString(); 
        OPTION_WARN = t.equalsIgnoreCase(Platform.getDebugOption(ID+"/warn"));
        OPTION_INFO = t.equalsIgnoreCase(Platform.getDebugOption(ID+"/info"));
    }
}
