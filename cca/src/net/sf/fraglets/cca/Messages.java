/*
 * $Id: Messages.java,v 1.1 2004-03-01 21:26:15 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package net.sf.fraglets.cca;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @since 01.03.2004
 * @author Klaus Rennecke
 * @version $Revision: 1.1 $
 */
public class Messages {

    private static final String BUNDLE_NAME = "net.sf.fraglets.cca.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE =
        ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * 
     */
    private Messages() {
    }
    
    /**
     * @param key
     * @return
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
