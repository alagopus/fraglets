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

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.dialogs.MessageDialog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.*;

/**
 * The browser plugin class to be used in the desktop.
 * @author marion@users.souceforge.net
 * @version $Revision: 1.3 $
 */
public class BrowserPlugin extends AbstractUIPlugin {
    /** The plugin id. */
    public static final String ID = "net.sourceforge.fraglets.zeig.browser";
    //The shared instance.
    private static BrowserPlugin plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;

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
    
    public static void warn(String message, Throwable ex) {
        getDefault().getLog().log
            (new Status(IStatus.WARNING, ID, IStatus.OK, message, ex));
    }
    
    public static void error(String message, Throwable ex)
    {
        getDefault().getLog().log
            (new Status(IStatus.ERROR, ID, IStatus.OK, message, ex));
        MessageDialog.openError(getDefault().getWorkbench()
            .getActiveWorkbenchWindow().getShell(), ID, message);
    }
}
