/*
 * ShowLicenseAction.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on November 6, 2002, 10:04 AM
 */

package net.sourceforge.fraglets.yaelp.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.sourceforge.fraglets.yaelp.bean.DateInput;
import net.sourceforge.fraglets.yaelp.model.Avatar;
import net.sourceforge.fraglets.yaelp.model.AvatarFilter;

/**
 * Action to show a license document.
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
public class ShowLicenseAction extends GenericAction {
    /** Holds value of property resource. */
    private String resource;
    
    /** Invoked when an action occurs.
     * Pops up an option pane containing the about application text.
     */
    public void actionPerformed(ActionEvent e) {
        try {
            ActionContext ac = getActionContext(e);
            JEditorPane editorPane = new JEditorPane(ac.getResource(getResource()));
            editorPane.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(editorPane);
            scrollPane.setPreferredSize(new Dimension(550, 250));
            javax.swing.JOptionPane.showMessageDialog
                (getRootPane(e), scrollPane, getTitle(),
                 JOptionPane.PLAIN_MESSAGE);
        } catch (java.io.IOException ex) {
            showException(e, ex);
        }
    }
    
    /** Getter for property resource.
     * @return Value of property resource.
     *
     */
    public String getResource() {
        return this.resource;
    }
    
    /** Setter for property resource.
     * @param resource New value of property resource.
     *
     */
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    /** Getter for property command.
     * @return Value of property command.
     *
     */
    public String getCommand() {
        return (String)getValue(ACTION_COMMAND_KEY);
    }
    
    /** Setter for property command.
     * @param command New value of property command.
     *
     */
    public void setCommand(String command) {
        putValue(ACTION_COMMAND_KEY, command);
    }
    
    /** Getter for property title.
     * @return Value of property title.
     *
     */
    public String getTitle() {
        return (String)getValue(NAME);
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     *
     */
    public void setTitle(String title) {
        putValue(NAME, title);
    }
    
}
