/*
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 */
package net.sourceforge.fraglets.zeig.eclipse;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
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
    
    public static void log(String message, Throwable ex) {
        getDefault().getLog().log
            (new Status(IStatus.WARNING, ID, IStatus.OK, message, ex));
    }
}
