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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.internal.properties.PropertyStore;
import org.eclipse.core.internal.properties.ResourceName;
import org.eclipse.core.internal.properties.StoredProperty;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The browser plugin class to be used in the desktop.
 * @author marion@users.souceforge.net
 * @version $Revision: 1.4 $
 */
public class BrowserPlugin extends AbstractUIPlugin {
    /** The plugin id. */
    public static final String ID = "net.sourceforge.fraglets.zeig.browser";
    
    public static boolean OPTION_INFO;
    public static boolean OPTION_INFO_DIALOG;
    public static boolean OPTION_WARN;
    public static boolean OPTION_WARN_DIALOG;
    public static boolean OPTION_ERROR_DIALOG;
    
    
    //The shared instance.
    private static BrowserPlugin plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;
    // property stores
    private HashMap propertyStores;

    /**
     * The constructor.
     */
    public BrowserPlugin(IPluginDescriptor descriptor) {
        super(descriptor);
        plugin = this;
        try {
            resourceBundle =
                ResourceBundle.getBundle(
                    "net.sourceforge.fraglets.zeig.eclipse.BrowserResources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }

    /**
     * Returns the shared instance.
     */
    public static BrowserPlugin getDefault() {
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
        ResourceBundle bundle = BrowserPlugin.getDefault().getResourceBundle();
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
    
    public static String resource(String key, Object[] args) {
        try {
            key = getDefault().getResourceBundle().getString(key);
        } catch (MissingResourceException ex) {
            StringWriter w = new StringWriter();
            PrintWriter p = new PrintWriter(w);
            ex.printStackTrace(p);
            key = "Missing '"+key+"' in "+ID+" from "+w;
        }
        return MessageFormat.format(key, args);
    }
    
    public static String resource(String key) {
        return resource(key, (Object[])null);
    }
    
    public static String resource(String key, Object arg) {
        return resource(key, new Object[] {arg});
    }
    
    public static IProgressMonitor monitor(IProgressMonitor m) {
        return m == null ? new NullProgressMonitor() : m;
    }
    
    public static void info(String message, Throwable ex) {
        if (OPTION_INFO) {
            getDefault().getLog().log
                (new Status(IStatus.INFO, ID, IStatus.OK, message, ex));
            if (OPTION_INFO_DIALOG) {
                MessageDialog.openInformation(getDefault().getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), ID, message);
            }
        }
    }
    
    public static void warn(String message, Throwable ex) {
        if (OPTION_WARN) {
            getDefault().getLog().log
                (new Status(IStatus.WARNING, ID, IStatus.OK, message, ex));
            if (OPTION_WARN_DIALOG) {
                MessageDialog.openWarning(getDefault().getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), ID, message);
            }
        }
    }
    
    public static void error(String message, Throwable ex)
    {
        getDefault().getLog().log
            (new Status(IStatus.ERROR, ID, IStatus.OK, message, ex));
        if (OPTION_ERROR_DIALOG) {
            MessageDialog.openError(getDefault().getWorkbench()
                .getActiveWorkbenchWindow().getShell(), ID, message);
        }
    }
    
    public void setProperty(IResource target, QualifiedName key, String value) throws CoreException {
        ResourceName name = new ResourceName("", target.getProjectRelativePath());
        PropertyStore store = getPropertyStore(target);
        synchronized (store) {
            if (value == null) {
                store.remove(name, key);
            } else {
                store.set(name, new StoredProperty(key, value));
            }
            store.commit();
        }
    }
    
    public String getProperty(IResource target, QualifiedName key) throws CoreException {
        ResourceName name = new ResourceName("", target.getProjectRelativePath());
        PropertyStore store = getPropertyStore(target);
        synchronized (store) {
            StoredProperty result = store.get(name, key);
            return result == null ? null : result.getStringValue();
        }
    }
    
    protected PropertyStore getPropertyStore(IResource target) {
        IPath location = getStateLocation();
        switch (target.getType()) {
            default:
                target = target.getProject();
            case IResource.PROJECT:
                location = location.append(".project").append(target.getName()).append(".properties");
                break;
            case IResource.ROOT:
                location = location.append(".root").append(".properties");
                break;
        }
        if (propertyStores == null) {
            propertyStores = new HashMap();
        }
        PropertyStore store = (PropertyStore)propertyStores.get(location);
        if (store == null) {
            location.toFile().getParentFile().mkdirs();
            store = new PropertyStore(location);
            propertyStores.put(location, store);
        }
        return store;
    }
    
    /**
     * @see org.eclipse.core.runtime.Plugin#startup()
     */
    public void startup() throws CoreException {
        super.startup();
        String t = Boolean.TRUE.toString(); 
        OPTION_INFO = t.equalsIgnoreCase(Platform.getDebugOption(ID+"/info"));
        OPTION_INFO_DIALOG = t.equalsIgnoreCase(Platform.getDebugOption(ID+"/info/dialog"));
        OPTION_WARN = t.equalsIgnoreCase(Platform.getDebugOption(ID+"/warn"));
        OPTION_WARN_DIALOG = t.equalsIgnoreCase(Platform.getDebugOption(ID+"/warn/dialog"));
        OPTION_ERROR_DIALOG = t.equalsIgnoreCase(Platform.getDebugOption(ID+"/error/dialog"));
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#shutdown()
     */
    public void shutdown() throws CoreException {
        if (propertyStores != null) {
            // shut down property stores
            for (Iterator i = propertyStores.values().iterator(); i.hasNext();) {
                PropertyStore store = (PropertyStore)i.next();
                store.shutdown(null);
            }
        }
        super.shutdown();
    }

}
