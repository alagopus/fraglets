/*
 * Defaults.java
 *
 * Created on November 6, 2002, 11:26 AM
 */

package net.sourceforge.fraglets.yaelp.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Utility class to set default values on various components.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * @author  marion@users.sourceforge.net
 */
public class Defaults {
    
    /** Creates a new instance of Defaults */
    protected Defaults() {
    }
    
    public static String getString(String key) {
        return getBundle().getString(key);
    }
    
    public static KeyStroke getKeyStroke(String key) {
        return KeyStroke.getKeyStroke(getString(key));
    }
    
    public static Integer getKeyCode(String key) {
        return new Integer((int)getString(key).charAt(0));
    }
    
    public static Icon getIcon(String key) {
        return new ImageIcon(getBundle().getClass().getResource(getString(key)));
    }
    
    public static Action configure(Action action) {
        String prefix = (String)action.getValue(Action.ACTION_COMMAND_KEY)
            + "Action.";
        try {
        action.putValue(action.ACCELERATOR_KEY, getKeyStroke(prefix + "acceleratorKey"));
        } catch (MissingResourceException ex) {}
        try {
        action.putValue(action.LONG_DESCRIPTION, getString(prefix + "longDescription"));
        } catch (MissingResourceException ex) {}
        try {
        action.putValue(action.MNEMONIC_KEY, getKeyCode(prefix + "mnemonicKey"));
        } catch (MissingResourceException ex) {}
        try {
        action.putValue(action.NAME, getString(prefix + "name"));
        } catch (MissingResourceException ex) {}
        try {
        action.putValue(action.SHORT_DESCRIPTION, getString(prefix + "shortDescription"));
        } catch (MissingResourceException ex) {}
        try {
        action.putValue(action.SMALL_ICON, getIcon(prefix + "smallIcon"));
        } catch (MissingResourceException ex) {}
        return action;
    }
    
    public static void configure(JComponent c) {
        configureBean(c, c.getName());
    }
    
    public static void configureBean(Object bean, String name) {
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            Enumeration enum = getBundle().getKeys();
            String prefix = name + '.';
            while (enum.hasMoreElements()) {
                String key = enum.nextElement().toString();
                if (key.startsWith(prefix)) {
                    setBeanProperty(bean, info, key.substring(prefix.length()), key);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO
        }
    }
    
    public static void setBeanProperty(Object bean, BeanInfo info, String name, String key)
        throws IntrospectionException, IllegalAccessException, InvocationTargetException
    {
        if (info == null) {
            info = Introspector.getBeanInfo(bean.getClass());
        }
        PropertyDescriptor descriptors[] = info.getPropertyDescriptors();
        Object value = getBundle().getObject(key);
        if (name.equals("mnemonic")) {
            // TODO define proper editor for mnemonic properties
            value = new Integer(value.toString().charAt(0)); // HACK
        }
        int scan = descriptors.length;
        while (--scan >= 0) {
            PropertyDescriptor descriptor = descriptors[scan];
            if (name.equals(descriptor.getName())) {
                Method setter = descriptor.getWriteMethod();
                Object args[] = new Object[1];
                PropertyEditor editor = null;
                if (value instanceof String) {
                    editor = PropertyEditorManager
                        .findEditor(descriptor.getPropertyType());
                }
                if (editor != null) {
                    editor.setAsText((String)value);
                    args[0] = editor.getValue();
                } else {
                    args[0] = value;
                }
                setter.invoke(bean, args);
            }
        }
    }
    
    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources");
    }
}
