/*
 * $Id: Resources.java,v 1.2 2004-04-04 23:39:21 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.1
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.fraglets.crm114j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @version $Id: Resources.java,v 1.2 2004-04-04 23:39:21 marion Exp $
 */
public class Resources {

    private static final String BUNDLE_NAME = "net.sf.fraglets.crm114j.clb"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE =
        ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * 
     */
    private Resources() {
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
