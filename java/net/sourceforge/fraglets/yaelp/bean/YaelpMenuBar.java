/*
 * YaelpMenuBar.java
 *
 * Created on November 6, 2002, 10:52 AM
 */

package net.sourceforge.fraglets.yaelp.bean;

/**
 *
 * @author  marion@users.sourceforge.net
 */
public class YaelpMenuBar extends javax.swing.JMenuBar {
    
    /** Creates new form BeanForm */
    public YaelpMenuBar() {
        initComponents();
        Defaults.configure(this);
        add(fileMenu);
        add(helpMenu);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        aboutAction = new net.sourceforge.fraglets.yaelp.action.AboutAction();
        Defaults.configure(aboutAction);
        fileMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        aboutItem = new javax.swing.JMenuItem();

        fileMenu.setName("fileMenu");
        Defaults.configure(fileMenu);
        helpMenu.setName("helpMenu");
        Defaults.configure(helpMenu);
        aboutItem.setAction(aboutAction);
        helpMenu.add(aboutItem);

        setName("menuBar");
    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu fileMenu;
    private net.sourceforge.fraglets.yaelp.action.AboutAction aboutAction;
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JMenu helpMenu;
    // End of variables declaration//GEN-END:variables
    
}