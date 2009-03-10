/*
 * GenericAction.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on November 6, 2002, 10:23 AM
 */

package net.sourceforge.fraglets.yaelp.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import org.xml.sax.SAXParseException;

/**
 * Generic abstract action within an action context.
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
public abstract class GenericAction extends AbstractAction {
    public static final String ACTION_CONTEXT_KEY = ActionContext.class.getName();
    
    public ActionContext getActionContext(ActionEvent e) {
        return (ActionContext)getRootPane(e).getClientProperty(ACTION_CONTEXT_KEY);
    }
    
    public static void setActionContext(Component c, ActionContext context) {
        getRootPane(c).putClientProperty(ACTION_CONTEXT_KEY, context);
    }
    
    /** Get the root pane component for the given event.
     * @param e the event to get the component for
     * @return the root pane
     */    
    public static JRootPane getRootPane(ActionEvent e) {
        return getRootPane((Component)e.getSource());
    }
    public static JRootPane getRootPane(Component c) {
        for (; c != null; c = c.getParent()) {
            if (c instanceof RootPaneContainer) {
                return ((RootPaneContainer)c).getRootPane();
            } else if (c instanceof JRootPane) {
                return (JRootPane)c;
            } else if (c instanceof JPopupMenu) {
                c = ((JPopupMenu)c).getInvoker();
            }
        }
        throw new RuntimeException("no root pane found");
    }
    
    protected String formatString(ActionContext ac, String key, Object args) {
        if (!(args instanceof Object[])) {
            args = new Object[] { args };
        }
        return MessageFormat.format(ac.getResourceString(key), (Object[])args);
    }

    protected void showException(ActionEvent e, Throwable ex) {
        String message = ex.getLocalizedMessage();
        if (message == null) {
            message = ex.toString();
        }
        if (ex instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException)ex;
            message += "(" + spe.getSystemId()
                + " line " + spe.getLineNumber() + ")";
        }
        JOptionPane.showMessageDialog
        (getRootPane(e), message, "Exception", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    
}
