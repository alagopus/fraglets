/*
 * $Id: CategoryLogMessages.java,v 1.1 2004-04-03 23:36:47 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package net.sf.fraglets.crm114j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @version $Id: CategoryLogMessages.java,v 1.1 2004-04-03 23:36:47 marion Exp $
 */
public class CategoryLogMessages {

    private static final String BUNDLE_NAME = "net.sf.fraglets.crm114j.clb"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE =
        ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * 
     */
    private CategoryLogMessages() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param key
     * @return
     */
    public static String getString(String key) {
        // TODO Auto-generated method stub
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    public static ResourceBundle getResourceBundle() {
        return RESOURCE_BUNDLE;
    }
}
