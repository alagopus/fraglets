/*
 * IntValueEditor.java - An editor bean to modify integer values.
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

package net.sourceforge.fraglets.beans;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;

/** <p>This bean implements yet another numeric value editor. It features
 * a label, a numeric text field for direct input, and a scroller for
 * adjusting the value with the pointer.
 *
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class IntValueEditor extends JPanel implements BoundedRangeModel, DocumentListener, Runnable {
  /** The event listener list to multiplex the ChangeEvents
   * for the BoundedRange interface.
   */
  protected transient EventListenerList listenerList =new EventListenerList();
  /** Shared event instance. The ChangeEvent contains no event specific
   * information.
   */
  protected transient ChangeEvent changeEvent =new ChangeEvent(this);
  /** Utility field used by bound properties.
   */
  protected transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport (this);
  /** Holds value of property minimum. */
  private int minimum = 0;
  /** Holds value of property value. */
  private int value = 0;
  /** Holds value of property extent. */
  private int extent = 0;
  /** Holds value of property maximum. */
  private int maximum = 100;
  /** Holds value of property valueIsAdjusting. */
  private transient boolean valueIsAdjusting;

  /** Creates new form NumericValueEditor */
  public IntValueEditor() {
    initComponents ();
    valueInput.getDocument().addDocumentListener (this);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    valueLabel = new javax.swing.JLabel ();
    valueInput = new javax.swing.JTextField ();
    valueAdjust = new javax.swing.JSlider ();
    setLayout (new java.awt.BorderLayout ());



    add (valueLabel, java.awt.BorderLayout.WEST);

    valueInput.setText ("0");
    valueInput.setNextFocusableComponent (valueAdjust);


    add (valueInput, java.awt.BorderLayout.CENTER);

    valueAdjust.setModel (this);
    valueAdjust.setToolTipText ("Drag to adjust value");
    valueAdjust.setValue (0);


    add (valueAdjust, java.awt.BorderLayout.SOUTH);

  }//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private transient javax.swing.JLabel valueLabel;
  private transient javax.swing.JTextField valueInput;
  private transient javax.swing.JSlider valueAdjust;
  // End of variables declaration//GEN-END:variables
  /** Add a PropertyChangeListener to the listener list.
   * @param l The listener to add.
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    propertyChangeSupport.addPropertyChangeListener (l);
  }
  /** Removes a PropertyChangeListener from the listener list.
   * @param l The listener to remove.
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    propertyChangeSupport.removePropertyChangeListener (l);
  }
  /** Getter for property minimum.
   * @return Value of property minimum.
   */
  public int getMinimum() {
    return minimum;
  }
  /** Setter for property minimum.  The default value is 0.
   * @param minimum New value of property minimum.
   */
  public void setMinimum(int minimum) {
    setRangeProperties (getValue(), getExtent(), minimum, getMaximum(), getValueIsAdjusting());
  }
  /** Getter for property value.
   * @return Value of property value.
   */
  public int getValue() {
    return value;
  }
  /** Setter for property value.  Default value is 10.
   * @param value New value of property value.
   */
  public void setValue(int value) {
    setRangeProperties (value, getExtent(), getMinimum(), getMaximum(), getValueIsAdjusting());
  }
  /** Getter for property extent.
   * @return Value of property extent.
   */
  public int getExtent() {
    return extent;
  }
  /** Setter for property extent.  Default value is 0.
   * @param extent New value of property extent.
   */
  public void setExtent(int extent) {
    setRangeProperties (getValue(), extent, getMinimum(), getMaximum(), getValueIsAdjusting());
  }
  /** Getter for property maximum.
   * @return Value of property maximum.
   */
  public int getMaximum() {
    return maximum;
  }
  /** Setter for property maximum.  This method, other than
   * <code>setRangeProperties</code>, adjusts the minimum
   * property to allow the new maximum value.  Default
   * value is 100.
   * @param maximum New value of property maximum.
   */
  public void setMaximum(int maximum) {
    int min = getMinimum();
    if (min > maximum) {
      min = maximum;
    }
    setRangeProperties (getValue(), getExtent(), min, maximum, getValueIsAdjusting());
  }
  /** Getter for property valueIsAdjusting.
   * @return Value of property valueIsAdjusting.
   */
  public boolean getValueIsAdjusting() {
    return valueIsAdjusting;
  }
  /** Setter for property valueIsAdjusting. This property is not
   * bound and thus will not fire any change events.
   * @param valueIsAdjusting New value of property valueIsAdjusting.
   */
  public void setValueIsAdjusting(boolean valueIsAdjusting) {
    this.valueIsAdjusting = valueIsAdjusting;
  }


  // BoundedRangeModel protocol implementation
  /** Adds a ChangeListener to the set of change listeners defined on the
   * BoundedRange interface.
   * @param l the ChangeListener to be added
   */
  public void addChangeListener(ChangeListener l) {
    listenerList.add(ChangeListener.class, l);
  }
  /** Removes a ChangeListener from the set of change event listeners defined on
   * the BoundedRange interface.
   * @param l the ChangeListener to be removed
   */
  public void removeChangeListener(ChangeListener l) {
      listenerList.remove(ChangeListener.class, l);
  }
  /** Notify all listeners that have registered interest
   * for notification on this event type.  The event
   * instance is lazily created using the parameters
   * passed into the fire method.  This implementation was
   * adopted from the javx.swing.event.EventListenerList
   * documentation. */
  protected void fireChangeEvent() {
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==ChangeListener.class) {
        ((ChangeListener)listeners[i+1]).stateChanged (changeEvent);
      }
    }
  }
  /** This method sets all of the model's data with a single
   * method call.  The method results in a single change event
   * and some property change events being generated.  The
   * provided values are adjusted to meet the constraints,
   * with priorities being minimum, maximum, extent, value,
   * in that order.  Note that more property change events may
   * be generated due to that adjustment.
   * @param value the new value
   * @param extent the new extent
   * @param min the new minimum
   * @param max the new maximum
   * @param adjusting the new value for isValueAdjusting
   */
  public void setRangeProperties (int value, int extent, int min, int max, boolean adjusting) {
    // Adjust the given values to constraints
    if (max < min) {
      max = min;
    }
    if (min + extent > max) {
      extent = max - min;
    }
    if (value < min) {
      value = min;
    } else if (value + extent > max) {
      value = max - extent;
    }

    // Fire the appropriate property change events
    boolean changed = false;
    if (this.minimum != min) {
      int old = this.minimum;
      this.minimum = min;
      propertyChangeSupport.firePropertyChange ("minimum", new Integer (old), new Integer (min));
      changed = true;
    }
    if (this.maximum != max) {
      int old = this.maximum;
      this.maximum = max;
      propertyChangeSupport.firePropertyChange ("maximum", new Integer (old), new Integer (max));
      changed = true;
    }
    if (this.extent != extent) {
      int old = this.extent;
      this.extent = extent;
      propertyChangeSupport.firePropertyChange ("extent", new Integer (old), new Integer (extent));
      changed = true;
    }
    if (this.value != value) {
      int old = this.value;
      this.value = value;
      propertyChangeSupport.firePropertyChange ("value", new Integer (old), new Integer (value));
      changed = true;
      
      String text = String.valueOf (value);
      if (!text.equals (valueInput.getText())) {
        valueInput.setText (text);
      }
    }

    if (changed) {
      fireChangeEvent();
    }
  }
  

  /** Getter for property label.
   * @return Value of property label.
   */
  public String getLabel() {
    return valueLabel.getText();
  }
  /** Setter for property label.
   * @param label New value of property label.
   */
  public void setLabel(String label) {
    String oldLabel = getLabel();
    valueLabel.setText (label);
    valueLabel.setVisible (label != null);
    propertyChangeSupport.firePropertyChange ("label", oldLabel,label);
  }
  private void writeObject (ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject (getLabel());
  }
  private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    setLabel ((String)in.readObject ());
  }

  
  /** Handle document change events for the text field.
   * @param ev the document change event
   */
  public void changedUpdate(DocumentEvent ev) {
    if (!getValueIsAdjusting()) {
      SwingUtilities.invokeLater (this);
    }
  }
  /** Handle document change events for the text field.
   * @param ev the document change event
   */
  public void removeUpdate(DocumentEvent ev) {
    if (!getValueIsAdjusting()) {
      SwingUtilities.invokeLater (this);
    }
  }
  /** Handle document change events for the text field.
   * @param ev the document change event
   */
  public void insertUpdate(DocumentEvent ev) {
    if (!getValueIsAdjusting()) {
      SwingUtilities.invokeLater (this);
    }
  }
  /** Handle document change events for the text field.
   */
  public void run() {
    try {
      String text = valueInput.getText();
      int newValue = text.length() == 0 ? 0 : Integer.parseInt (text);
      if (newValue != getValue()) {
        valueAdjust.setValue (newValue);
        
        // Check for constraint violation
        if (newValue != getValue()) {
          valueInput.setText (String.valueOf (getValue()));
        }
      }
    } catch (NumberFormatException ex) {
      valueInput.setText (String.valueOf (getValue()));
      valueInput.getToolkit().beep();
    }
  }
}
