/*
 * PropertyEditor.java
 * Copyright (C) 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
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
 * @version $Revision: 1.2 $
 */
public class PropertyEditor extends javax.swing.JDialog {
    PropertyTableModel model = new PropertyTableModel();
    
    /** Creates new form PropertyEditor */
    public PropertyEditor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        propertyScroll = new javax.swing.JScrollPane();
        propertyTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setTitle("Property Editor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        propertyTable.setModel(model);
        propertyScroll.setViewportView(propertyTable);

        getContentPane().add(propertyScroll, java.awt.BorderLayout.CENTER);

        addButton.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("addPropertyButton.text"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(addButton);

        closeButton.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("closePropertyEditor.text"));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(closeButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        final JTextField nameField = new JTextField();
        final JTextField valueField = new JTextField();
        Object stuff[] = new Object[] {
            new JLabel("Name:"),
            nameField,
            new JLabel("Value:"),
            valueField,
        };
        nameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent ev) {
                String value = getAvatar().getProperty(nameField.getText());
                if (value != null) {
                    valueField.setText(value);
                }
            }
        });
        int result = JOptionPane.showConfirmDialog(this, stuff,
            "Add Property", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String value = valueField.getText();
            if (name.length() > 0) {
                getAvatar().setProperty(name, value, System.currentTimeMillis());
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // Add your handling code here:
        closeDialog(null);
    }//GEN-LAST:event_closeButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /** Getter for property avatar.
     * @return Value of property avatar.
     */
    public Avatar getAvatar() {
        return model.getAvatar();
    }
    
    /** Setter for property avatar.
     * @param avatar New value of property avatar.
     */
    public void setAvatar(Avatar avatar) {
        model.setAvatar(avatar);
    }
    
    public void dispose() {
        super.dispose();
        setAvatar(null);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable propertyTable;
    private javax.swing.JButton addButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane propertyScroll;
    // End of variables declaration//GEN-END:variables
}
