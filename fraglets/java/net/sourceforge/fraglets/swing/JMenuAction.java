/*
 * JMenuAction.java Created on 30. März 2000, 05:10
 * Copyright (C) 2000 Klaus Rennecke.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  */

package net.sourceforge.fraglets.swing;

import java.beans.*;
import javax.swing.*;

/** A JMenuItem with values driven from an Action.
 * Although this functionality is already implemented by JMenu,
 * there are several reasons why it is not appropriate in some cases.
 * Most notably is the danger of memory retention because of the
 * property change listener dependency from the action. A more obvious
 * reason is the lack of a setter method to later change the menu item
 * action, but this is now implemented in java SDK 1.3.
 *
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class JMenuAction extends JMenuItem implements PropertyChangeListener
{
  /** The action we represent. */
  protected Action action;

  /** Creates new JMenuAction */
  public JMenuAction () {
    setHorizontalTextPosition(SwingConstants.RIGHT); 
    setVerticalTextPosition(SwingConstants.CENTER);
  }

  /** Creates new JMenuAction
   * @param action The initial action for this JMenuAction
   */
  public JMenuAction (Action action) {
    this();
    setAction (action);
  }

  /** Set the action which this JMenuAction should represent.
   * The JMenuAction is registered as PropertyChangeListener
   * as long as the JMenuAction is used in the UI.
   *
   * @param newValue the new action to represent
   */
  public void setAction(Action newValue) {
    if (action != null) {
      removeActionListener (action);
      action.removePropertyChangeListener (this);
      action = null;
    }

    action = newValue;

    if (action != null) {
      if (getParent() != null) {
        action.addPropertyChangeListener (this);
      }
      // One might think it would be desirable to update also only when
      // used in the UI (parent != null), but we need to set at least
      // the menu item text early to allow correct layout calculation.
      updateFrom (action);
      addActionListener (action);
    }
  }

  /** Get the action represented by this JMenuAction.
   * @return the represented action or null
   */
  public Action getAction() {
    return action;
  }
  
  /** Notification that this component is now used in the UI.
   * This is the place where the JMenuAction is registered
   * as PropertyChangeListener on the represented Action.
   */
  public void addNotify() {
    super.addNotify();
    if (action != null) {
      action.addPropertyChangeListener (this);
      updateFrom (action);
    }
  }
  
  /** Notification that this component is no longer used in the UI. */
  public void removeNotify() {
    super.removeNotify();
    if (action != null) {
      action.removePropertyChangeListener (this);
    }
  }

  /** Utility method to process a property change in the action.
   * @param property the name of the changed property
   * @param oldValue old value of the property (currently unused)
   * @param newValue new value of the property
   */
  protected void actionPropertyChange(String property,Object oldValue,Object newValue) {
    if (property.equals ("enabled")) {
      setEnabled (((Boolean)newValue).booleanValue());
    } else if (property.equals (Action.NAME)) {
      setText ((String)newValue);
    } else if (property.equals (Action.SMALL_ICON)) {
      setIcon((Icon)newValue);
    } else if (property.equals (Action.SHORT_DESCRIPTION)) {
      setToolTipText((String)newValue);
    } else if (property.equals (Action.ACTION_COMMAND_KEY)) {
      setActionCommand((String)newValue);
    } else if (property.equals (Action.MNEMONIC_KEY)) {
      setMnemonic(((Integer)newValue).intValue());
    } else if (property.equals (Action.ACCELERATOR_KEY)) {
      setAccelerator((KeyStroke)newValue);
    }
  }

  /** Process a property change event. This method implements
   * the PropertyChangeListener protocol.
   * @param ev the property change event describing the property change
   */
  public void propertyChange(final java.beans.PropertyChangeEvent ev) {
    if (ev.getSource() == action) {
      actionPropertyChange (ev.getPropertyName(), ev.getOldValue(), ev.getNewValue());
    }
  }

  /** Update local state from the current action properties.
   * @param action the action from which to take values
   */
  protected void updateFrom (Action action) {
    setEnabled (action.isEnabled());
    updateFrom (action, action.NAME);
    updateFrom (action, action.SMALL_ICON);
    updateFrom (action, action.SHORT_DESCRIPTION);
    updateFrom (action, action.ACTION_COMMAND_KEY);
    updateFrom (action, action.MNEMONIC_KEY);
    updateFrom (action, action.ACCELERATOR_KEY);
  }

  /** Update local state from the action property <var>name</var>.
   * @param action the action from which to take values
   * @param name the property name of the value to update
   */
  protected void updateFrom (Action action, String name) {
    Object newValue = action.getValue(name);
    if (newValue != null) {
      actionPropertyChange (name, null, newValue);
    }
  }
}
