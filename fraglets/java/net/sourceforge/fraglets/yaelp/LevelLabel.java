/*
 * LevelLabel.java
 *
 * Created on 12. Juli 2002, 13:56
 */

package net.sourceforge.fraglets.yaelp;

import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JSlider;

/**
 *
 * @author  marion@users.sourceforge.net
 */
public class LevelLabel extends JLabel implements ChangeListener {
    
    /** Holds value of property prefix. */
    private String prefix;
    
    /** Creates a new instance of LevelLabel */
    public LevelLabel(String prefix, JSlider slider) {
        super(prefix);
        setPrefix(prefix);
        if (slider != null) {
            slider.addChangeListener(this);
            setLabelFor(slider);
        }
    }
    
    /** Getter for property prefix.
     * @return Value of property prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }
    
    /** Setter for property prefix.
     * @param prefix New value of property prefix.
     */
    public void setPrefix(String prefix) {
        repaint();
        this.prefix = prefix;
    }
    
    public void stateChanged(ChangeEvent ev) {
        int value = ((JSlider)ev.getSource()).getValue();
        String text;
        if (0 < value && value <= 60) {
            text = String.valueOf(value);
        } else {
            text = "...";
        }
        super.setText(prefix+text);
    }
    
}
