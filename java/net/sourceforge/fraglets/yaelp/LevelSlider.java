/*
 * LevelSlider.java
 *
 * Created on 12. Juli 2002, 13:11
 */

package net.sourceforge.fraglets.yaelp;

import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import java.awt.Graphics;

/**
 *
 * @author  marion@users.sourceforge.net
 */
public class LevelSlider extends JSlider {
    /** Creates a new instance of LevelSlider */
    public LevelSlider() {
        this(1);
    }
    
    /** Creates a new instance of LevelSlider */
    public LevelSlider(int value) {
        this(0, 61, value);
    }
    
    /** Creates a new instance of LevelSlider */
    public LevelSlider(int min, int max, int value) {
        super(min, max, value);
        setPaintLabels(true);
        setMajorTickSpacing(5);
        setPaintTicks(true);
        setPaintTrack(false);
        setSnapToTicks(false);
    }
    
    public Hashtable createStandardLabels(int increment) {
        return createStandardLabels(increment, 10);
    }
    
    public Hashtable createStandardLabels(int increment, int start) {
        Hashtable result = new Hashtable();
        int end = 60;
        start = 10; // hack
        increment = 10; // hack
        while (start <= end) {
            result.put(new Integer(start), new JLabel(String.valueOf(start)));
            start += increment;
        }
        JLabel unlimited = new JLabel("...");
        result.put(new Integer(0), unlimited);
        result.put(new Integer(1), new JLabel("1"));
        result.put(new Integer(61), unlimited);
        return result;
    }
    
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        int value = getValue();
//        String label;
//        if (value < 1 || value > 60) {
//            label = "...";
//        } else {
//            label = String.valueOf(value);
//        }
//        g.drawString(label, 2, 2);
//    }
    
//    public int getValue() {
//        int value = super.getValue();
//        if (value <= getMinimum()) {
//            return Integer.MIN_VALUE;
//        } else if (value >= getMaximum()) {
//            return Integer.MAX_VALUE;
//        } else {
//            return value;
//        }
//    }
}
