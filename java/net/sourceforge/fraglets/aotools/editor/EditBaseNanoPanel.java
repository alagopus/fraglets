/*
 * EditBaseNanoPanel.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 3. August 2001, 18:08
 */

package net.sourceforge.fraglets.aotools.editor;

import net.sourceforge.fraglets.aotools.codec.NanoListEncoder;
import net.sourceforge.fraglets.aotools.model.*;

/**
 *
 * @author sas
 * @version $Revision: 1.3 $
 */
public class EditBaseNanoPanel extends javax.swing.JPanel {

    /** Creates new form EditBaseNanoPanel */
    public EditBaseNanoPanel() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        southP = new javax.swing.JPanel();
        saveB = new javax.swing.JButton();
        loadB = new javax.swing.JButton();
        printB = new javax.swing.JButton();
        quitB = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        nanoList = new javax.swing.JList();
        nanoP = new javax.swing.JPanel();
        baseClusterP = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTf = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        skillTf = new javax.swing.JTextField();
        typeChoice = new javax.swing.JComboBox(BaseNanoCluster.NANO_TYPES);
        locChoice = new javax.swing.JComboBox(Body.SLOT_NAMES);
        jSeparator1 = new javax.swing.JSeparator();
        sortListP = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        sortType = new javax.swing.JComboBox(BaseNanoCluster.NANO_TYPES);
        sortLoc = new javax.swing.JComboBox(Body.SLOT_NAMES);
        showAllB = new javax.swing.JButton();
        sortB = new javax.swing.JButton();
        sortP = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox(BaseNanoCluster.NANO_TYPES);
        jComboBox2 = new javax.swing.JComboBox(Body.SLOT_NAMES);
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        westP = new javax.swing.JPanel();
        newB = new javax.swing.JButton();
        addB = new javax.swing.JButton();
        deleteB = new javax.swing.JButton();
        
        setLayout(new java.awt.BorderLayout());
        
        southP.setBorder(new javax.swing.border.TitledBorder("Action"));
        southP.setBackground(java.awt.Color.lightGray);
        saveB.setText("Save");
        saveB.setOpaque(false);
        saveB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBActionPerformed(evt);
            }
        });
        
        southP.add(saveB);
        
        loadB.setText("Load");
        loadB.setOpaque(false);
        loadB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBActionPerformed(evt);
            }
        });
        
        southP.add(loadB);
        
        printB.setText("Print");
        printB.setOpaque(false);
        printB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBActionPerformed(evt);
            }
        });
        
        southP.add(printB);
        
        quitB.setText("Quit");
        quitB.setOpaque(false);
        quitB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitBActionPerformed(evt);
            }
        });
        
        southP.add(quitB);
        
        add(southP, java.awt.BorderLayout.SOUTH);
        
        jScrollPane2.setPreferredSize(new java.awt.Dimension(160, 131));
        nanoList.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        nanoList.setModel(BaseNanoList.getBaseNanoList().getListModel());
        nanoList.setPreferredSize(new java.awt.Dimension(150, 1000));
        nanoList.setMaximumSize(new java.awt.Dimension(100, 10000));
        nanoList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        nanoList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                nanoListValueChanged(evt);
            }
        });
        
        jScrollPane2.setViewportView(nanoList);
        
        add(jScrollPane2, java.awt.BorderLayout.CENTER);
        
        nanoP.setLayout(new javax.swing.BoxLayout(nanoP, javax.swing.BoxLayout.Y_AXIS));
        
        nanoP.setBackground(java.awt.Color.lightGray);
        baseClusterP.setLayout(new java.awt.GridLayout(0, 2, 2, 2));
        
        baseClusterP.setBorder(new javax.swing.border.TitledBorder("Nano Cluster Stats"));
        baseClusterP.setBackground(java.awt.Color.lightGray);
        jLabel1.setText("Name");
        baseClusterP.add(jLabel1);
        
        nameTf.setColumns(20);
        baseClusterP.add(nameTf);
        
        jLabel2.setText("Skill");
        baseClusterP.add(jLabel2);
        
        skillTf.setColumns(20);
        baseClusterP.add(skillTf);
        
        baseClusterP.add(typeChoice);
        
        baseClusterP.add(locChoice);
        
        nanoP.add(baseClusterP);
        
        nanoP.add(jSeparator1);
        
        add(nanoP, java.awt.BorderLayout.NORTH);
        
        sortListP.setLayout(new java.awt.GridLayout(0, 1));
        
        sortListP.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        jLabel3.setText("Sort by");
        sortListP.add(jLabel3);
        
        sortType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortTypeActionPerformed(evt);
            }
        });
        
        sortListP.add(sortType);
        
        sortLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortLocActionPerformed(evt);
            }
        });
        
        sortListP.add(sortLoc);
        
        showAllB.setText("Show all");
        showAllB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllBActionPerformed(evt);
            }
        });
        
        sortListP.add(showAllB);
        
        sortB.setText("Sort");
        sortB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortBActionPerformed(evt);
            }
        });
        
        sortListP.add(sortB);
        
        add(sortListP, java.awt.BorderLayout.EAST);
        
        jPanel2.setLayout(new java.awt.GridLayout(0, 1));
        
        jPanel2.setBorder(new javax.swing.border.TitledBorder("Sort List"));
        jPanel2.add(jComboBox1);
        
        jPanel2.add(jComboBox2);
        
        jButton1.setText("Show all");
        jPanel2.add(jButton1);
        
        jButton2.setText("Sort");
        jPanel2.add(jButton2);
        
        sortP.add(jPanel2);
        
        add(sortP, java.awt.BorderLayout.EAST);
        
        westP.setLayout(new java.awt.GridLayout(0, 1));
        
        westP.setBorder(new javax.swing.border.TitledBorder("Manage List"));
        newB.setText("new");
        newB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBActionPerformed(evt);
            }
        });
        
        westP.add(newB);
        
        addB.setText("add");
        addB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBActionPerformed(evt);
            }
        });
        
        westP.add(addB);
        
        deleteB.setText("delete");
        deleteB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBActionPerformed(evt);
            }
        });
        
        westP.add(deleteB);
        
        jPanel3.add(westP);
        
        add(jPanel3, java.awt.BorderLayout.WEST);
        
    }//GEN-END:initComponents

    private void printBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().print();
    }//GEN-LAST:event_printBActionPerformed

    private void sortBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortBActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().sort();
    }//GEN-LAST:event_sortBActionPerformed

    private void quitBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitBActionPerformed
        // Add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_quitBActionPerformed

    private void showAllBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllBActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().showAll();
    }//GEN-LAST:event_showAllBActionPerformed

    private void sortLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortLocActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().showLoc(sortLoc.getSelectedIndex());
        
    }//GEN-LAST:event_sortLocActionPerformed

    private void sortTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortTypeActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().showType(sortType.getSelectedIndex());
    }//GEN-LAST:event_sortTypeActionPerformed

    private void addBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().addNano(getNano());
    }//GEN-LAST:event_addBActionPerformed
    
    private BaseNanoCluster getNano(){
        return new BaseNanoCluster(nameTf.getText(), skillTf.getText(),
        typeChoice.getSelectedIndex(), locChoice.getSelectedIndex());
    }
    
    private void setNano(BaseNanoCluster c){
        nameTf.setText(c.getName());
        skillTf.setText(c.getSkill());
        typeChoice.setSelectedIndex(c.getType());
        locChoice.setSelectedIndex(c.getBodyLoc());
    }
    private void clearNano(){
        nameTf.setText("");
        skillTf.setText("");
        typeChoice.setSelectedIndex(0);
        locChoice.setSelectedIndex(0);  
    }
    private void loadBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().load();
    }//GEN-LAST:event_loadBActionPerformed

    private void deleteBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBActionPerformed
        // Add your handling code here:
        BaseNanoList.getBaseNanoList().removeNano(getNano());
    }//GEN-LAST:event_deleteBActionPerformed

    private void newBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBActionPerformed
        // Add your handling code here:
        clearNano();
    }//GEN-LAST:event_newBActionPerformed

    private void saveBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBActionPerformed
        // Add your handling code here:
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        if (chooser.showSaveDialog(this) != chooser.APPROVE_OPTION)
            return;
        java.io.File file = chooser.getSelectedFile();
        if (file.exists()) {
            if (javax.swing.JOptionPane.showConfirmDialog(this,
                "Overwrite "+file+"?", "Save Base Nano List",
                javax.swing.JOptionPane.OK_CANCEL_OPTION) !=
                javax.swing.JOptionPane.OK_OPTION)
                return;
        }
        try {
            java.io.FileOutputStream out =
                new java.io.FileOutputStream(chooser.getSelectedFile());
            NanoListEncoder encoder = new NanoListEncoder(out);
            encoder.encodeBaseNanoList(BaseNanoList.getBaseNanoList());
            encoder.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Save Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        // BaseNanoList.getBaseNanoList().save();
    }//GEN-LAST:event_saveBActionPerformed

    private void nanoListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_nanoListValueChanged
        // Add your handling code here:
        BaseNanoCluster c = BaseNanoList.getBaseNanoList().get(nanoList.getSelectedIndex());
        setNano(c);
    }//GEN-LAST:event_nanoListValueChanged

    /** Start the editor.
     * @param args no arguments so far
     */    
    public static void main(String []args){
        javax.swing.JFrame f = new javax.swing.JFrame("Edit Base Nano List");
        f.getContentPane().add(new EditBaseNanoPanel(), java.awt.BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);
    
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel southP;
    private javax.swing.JButton saveB;
    private javax.swing.JButton loadB;
    private javax.swing.JButton printB;
    private javax.swing.JButton quitB;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList nanoList;
    private javax.swing.JPanel nanoP;
    private javax.swing.JPanel baseClusterP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField nameTf;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField skillTf;
    private javax.swing.JComboBox typeChoice;
    private javax.swing.JComboBox locChoice;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel sortListP;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox sortType;
    private javax.swing.JComboBox sortLoc;
    private javax.swing.JButton showAllB;
    private javax.swing.JButton sortB;
    private javax.swing.JPanel sortP;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel westP;
    private javax.swing.JButton newB;
    private javax.swing.JButton addB;
    private javax.swing.JButton deleteB;
    // End of variables declaration//GEN-END:variables

}