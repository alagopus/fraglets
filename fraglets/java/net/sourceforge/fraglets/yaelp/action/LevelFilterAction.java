/*
 * LevelFilterAction.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on November 6, 2002, 10:04 AM
 */

package net.sourceforge.fraglets.yaelp.action;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import net.sourceforge.fraglets.yaelp.bean.DateInput;
import net.sourceforge.fraglets.yaelp.bean.LevelLabel;
import net.sourceforge.fraglets.yaelp.bean.LevelSlider;
import net.sourceforge.fraglets.yaelp.model.AvatarFilter;

/**
 * Action to create a level filter.
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
public class LevelFilterAction extends GenericAction {
    public static final String ACTION_COMMAND = "levelFilter";
    
    public LevelFilterAction() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
    }
    
    /** Invoked when an action occurs.
     * Pops up an option pane containing the about application text.
     */
    public void actionPerformed(ActionEvent e) {
        ActionContext ac = getActionContext(e);
        LevelSlider minLevelSlider = new LevelSlider(0, 66, 0);
        LevelSlider maxLevelSlider = new LevelSlider(0, 66, 66);
        Object stuff[] = new Object[] {
            new LevelLabel(ac.getResourceString("levelFilterInput.minimumLabel"),
            minLevelSlider), minLevelSlider,
            new LevelLabel(ac.getResourceString("levelFilterInput.maximumLabel"),
            maxLevelSlider), maxLevelSlider,
        };
        int result = JOptionPane.showConfirmDialog(getRootPane(e), stuff,
            ac.getResourceString("levelFilterInput.title"),
            JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int min = minLevelSlider.getValue();
            int max = maxLevelSlider.getValue();
            StringBuffer buffer = new StringBuffer();
            if (min > 0) {
                buffer.append(formatString(ac, "levelFilter.minimumFormat",
                    new Integer(min)));
            } else {
                min = Integer.MIN_VALUE;
            }
            if (max < 61) {
                if (buffer.length() > 0) buffer.append(", ");
                buffer.append(formatString(ac, "levelFilter.maximumFormat",
                    new Integer(max)));
            } else {
                max = Integer.MAX_VALUE;
            }
            if (buffer.length() > 0) {
                ac.appendFilter(buffer.toString(), new AvatarFilter.Level(min, max));
            } else {
                JOptionPane.showMessageDialog(getRootPane(e),
                    ac.getResourceString("levelFilterInput.warnNoLimit"),
                    ac.getResourceString("levelFilterInput.title"),
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
}
