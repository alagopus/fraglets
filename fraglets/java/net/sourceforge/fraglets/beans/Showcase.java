/*
 * Showcase.java
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

package net.sourceforge.shelf.beans;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.*;
import java.util.StringTokenizer;
import javax.swing.*;

import net.sourceforge.shelf.swing.JMenuAction;

/** This class showcases the beans in this package.
 *
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class Showcase extends JApplet {

  /** Action identifier. */
  public static final int ACTION_QUIT = 0;
  public static final int ACTION_NEW_BEAN = 1;
  public static final int ACTION_ABOUT = 2;
  public static final int ACTION_CREATE_INT_VALUE_EDITOR = 3;

  /** The about dialog text. */
  public static final String ABOUT_HEADER =
  "Bean Showcase, Copyright (C) 2000 Klaus Rennecke.\n"+
  "This class showcases the beans in this package.";
  public static final String ABOUT_TEXT = "";

  /** Holds value of property action. */
  private Action[] action = {
    new AbstractAction ("Quit") {
      public void actionPerformed (ActionEvent ev) {
        System.exit (0);
      }
    },
    new AbstractAction ("New Bean") {
      public void actionPerformed (ActionEvent ev) {
        addNewBean();
      }
    },
    new AbstractAction ("About") {
      public void actionPerformed (ActionEvent ev) {
        showAboutDialog();
      }
    },
    new LaunchBeanAction ("IntValueEditor")
  };
  /** Creates new form Showcase */
  public Showcase() {
    initComponents ();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    menuBar = new javax.swing.JMenuBar ();
    fileMenu = new javax.swing.JMenu ();
    jMenuItem2 = new JMenuAction(getAction(ACTION_NEW_BEAN));
    jSeparator1 = new javax.swing.JSeparator ();
    jMenuItem1 = new JMenuAction(getAction(ACTION_QUIT));
    beanMenu = new javax.swing.JMenu ();
    jMenuItem3 = new JMenuAction(getAction(ACTION_CREATE_INT_VALUE_EDITOR));
    helpMenu = new javax.swing.JMenu ();
    jMenuItem4 = new JMenuAction(getAction(ACTION_ABOUT));
    jScrollPane1 = new javax.swing.JScrollPane ();
    desktopPane = new javax.swing.JDesktopPane ();

      fileMenu.setText ("File");
      fileMenu.setMnemonic ('F');
  
    
        fileMenu.add (jMenuItem2);
    
        fileMenu.add (jSeparator1);
    
        fileMenu.add (jMenuItem1);
      menuBar.add (fileMenu);
      beanMenu.setText ("Bean");
      beanMenu.setMnemonic ('B');
  
    
        beanMenu.add (jMenuItem3);
      menuBar.add (beanMenu);
      helpMenu.setText ("Help");
      helpMenu.setMnemonic ('H');
  
    
        helpMenu.add (jMenuItem4);
      menuBar.add (helpMenu);


      desktopPane.setPreferredSize (new java.awt.Dimension(400, 280));
  
      jScrollPane1.setViewportView (desktopPane);
  

    getContentPane ().add (jScrollPane1, java.awt.BorderLayout.CENTER);

    setJMenuBar (menuBar);

  }//GEN-END:initComponents

  /** Exit the Application */
  private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
    System.exit (0);
  }//GEN-LAST:event_exitForm

  /**
   * @param args an optional list of additional bean class names.
   */
  public static void main (String args[]) {
    Showcase instance = new Showcase ();

    for (int i = 0; i < args.length; i++) {
      try {
        instance.addNewBean (args[i]);
      } catch (ClassNotFoundException ex) {
        System.err.println ("cannot find bean: " + ex);
      }
    }
    
    JFrame frame = new JFrame ("Beans Showcase");
    frame.getContentPane().add (BorderLayout.CENTER, instance);
    frame.addWindowListener (new WindowAdapter () {
      public void windowClosing (WindowEvent ev) {
        System.exit (0);
      }
    });
    frame.pack();
    frame.show();
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JMenu fileMenu;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenu beanMenu;
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JMenu helpMenu;
  private javax.swing.JMenuItem jMenuItem4;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JDesktopPane desktopPane;
  // End of variables declaration//GEN-END:variables
  /** Indexed getter for property action.
   * @param index Index of the property.
   * @return Value of the property at <CODE>index</CODE>.
   */
  public Action getAction(int index) {
    return action[index];
  }

  /** Show the about dialog. */
  public void showAboutDialog() {
    JTextArea text = new JTextArea();
    text.setText (ABOUT_TEXT);
    text.setEditable (false);
    JOptionPane.showMessageDialog (this, new Object[] { ABOUT_HEADER, new JScrollPane (text) });

    // YUCK: silly windows backing-store implementation
    // seems to break menu rendering behind the open dialog.
    repaint();
  }

  /** Query for a bean class name and add a new menu entry to
   * instanciate that bean. */
  protected void addNewBean() {
    String name = JOptionPane.showInputDialog(this, "Enter new bean class name", "Add new bean", JOptionPane.QUESTION_MESSAGE);
    if (name != null) {
      try {
        addNewBean (name);
      } catch (ClassNotFoundException ex) {
        JOptionPane.showMessageDialog(Showcase.this, "cannot find bean: " + ex, "Error adding bean", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void addNewBean (String name) throws ClassNotFoundException {
    // load class
    Class.forName (resolveName (name));
    beanMenu.add (new LaunchBeanAction (name));
  }

  /** Resolve a class name, treating it as relative if
   * no package is specified. */
  public String resolveName (String name) {
    if (name.indexOf ('.') < 0) {
      // no package: interpret as relative class name
      String prefix = getClass().getName();
      int dot = prefix.lastIndexOf ('.');
      if (dot >= 0) {
        name = prefix.substring (0, dot + 1) + name;
      }
    }
    return name;
  }

  public class LaunchBeanAction extends AbstractAction {
    public LaunchBeanAction (String name) {
      super (name);
      putValue (SMALL_ICON, findIcon (name));
    }

    public void actionPerformed (ActionEvent ev) {
      try {
        String name = getValue (NAME).toString();
        JInternalFrame frame = new JInternalFrame (name, true, true, true, true);
        frame.getContentPane().add (BorderLayout.CENTER, createBean (name));
        Icon icon = findIcon (name);
        if (icon != null) {
          frame.setFrameIcon (icon);
        }
        desktopPane.add (frame);
        frame.pack();
        frame.show();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(Showcase.this, "cannot instanciate bean: " + ex, "Bean instanciation error", JOptionPane.ERROR_MESSAGE);
      }
    }

    protected JComponent createBean (String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
      return (JComponent)Class.forName (resolveName (name)).newInstance();
    }
    
    /** Find icon for bean <var>name</var> */
    protected Icon findIcon (String name) {
      try {
        BeanInfo info = (BeanInfo)Class.forName (resolveName (name+"BeanInfo")).newInstance();
        Image image = info.getIcon (BeanInfo.ICON_COLOR_16x16);
        return image == null ? null : new ImageIcon (image);
      } catch (Exception ex) {
        // ClassNotFoundException, IllegalAccessException, InstanciationException
        return null;
      }
    }
  }
}
