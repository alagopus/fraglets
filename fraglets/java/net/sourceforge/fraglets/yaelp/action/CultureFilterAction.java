/*
 * CultureFilterAction.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on November 6, 2002, 10:04 AM
 */

package net.sourceforge.fraglets.yaelp.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.JOptionPane;
import net.sourceforge.fraglets.yaelp.bean.DateInput;
import net.sourceforge.fraglets.yaelp.model.Avatar;
import net.sourceforge.fraglets.yaelp.model.AvatarFilter;

/**
 * Action to create a culture filter.
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
public class CultureFilterAction extends GenericAction {
    public static final String ACTION_COMMAND = "cultureFilter";
    
    public CultureFilterAction() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
    }
    
    /** Invoked when an action occurs.
     * Pops up an option pane containing the about application text.
     */
    public void actionPerformed(ActionEvent e) {
        ActionContext ac = getActionContext(e);
        Object values[] = Avatar.Culture.getValues().toArray();
        Arrays.sort(values, Avatar.Culture.getComparator());
        Object input = JOptionPane.showInputDialog
            (getRootPane(e), ac.getResourceString("cultureFilterInput.label"),
             ac.getResourceString("cultureFilterInput.title"),
             JOptionPane.QUESTION_MESSAGE,
             null, values, null);
        if (input != null) {
            Avatar.Culture culture = (Avatar.Culture)input;
            String name = culture.getName();
            if (name == null || name.length() == 0) {
                name = ac.getResourceString("cultureFilter.unknown");
            }
            ac.appendFilter(name, new AvatarFilter.Culture(culture));
        }
    }
    
}
