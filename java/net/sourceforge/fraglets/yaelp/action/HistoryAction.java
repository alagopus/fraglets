/*
 * HistoryAction.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on November 25, 2002
 */

package net.sourceforge.fraglets.yaelp.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.sourceforge.fraglets.yaelp.model.Avatar;

/**
 * Action performed when an avatar history was requested.
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
public class HistoryAction extends GenericAction {
    public static final String ACTION_COMMAND = "history";
    
    public HistoryAction() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
    }
    
    /** Invoked when an action occurs.
     * Pops up an option pane containing the about application text.
     */
    public void actionPerformed(ActionEvent e) {
        ActionContext ac = getActionContext(e);
        Object selection = ac.getCurrentSelection();
        if (selection instanceof Avatar) {
            Avatar avatar = (Avatar)selection;
            Object args[] = new Object[] { ac.getApplicationName(), avatar.getName() };
            String title = formatString(ac, "historyTitle.format", args);
            JEditorPane editorPane = new JEditorPane("text/plain", avatar.getHistory());
            editorPane.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(editorPane);
            scrollPane.setPreferredSize(new Dimension(550, 250));
            javax.swing.JOptionPane.showMessageDialog
                (getRootPane(e), scrollPane, title,
                 JOptionPane.PLAIN_MESSAGE);
        }
    }
    
}
