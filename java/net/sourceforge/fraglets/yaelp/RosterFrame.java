/*
 * RosterFrame.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 1. Mai 2001, 11:39
 */

package de.rennecke.yaelp;

import com.jclark.xml.sax.CommentDriver;
import com.jclark.xsl.sax.Destination;
import com.jclark.xsl.sax.FileDestination;
import com.jclark.xsl.sax.OutputMethodHandlerImpl;
import com.jclark.xsl.sax.XSLProcessor;
import com.jclark.xsl.sax.XSLProcessorImpl;
import java.util.Comparator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** This class implements a simple GUI to invoke the log recognizer and
 * display the results.
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
 * @author  kre
 * @version $Revision: 1.2 $
 */
public class RosterFrame extends javax.swing.JFrame {
    /** The file chooser used to select files to parse and export.
     */    
    protected javax.swing.JFileChooser chooser;
    /** The recognizer used to parse the input files.
     */    
    protected Recognizer recognizer = new Recognizer();
    /** The current avatar filter, if any.
     */    
    protected AvatarFilter avatarFilter;
    
    protected String aboutText;
    
    protected Comparator order;
    
    protected java.net.URL style;
    
    public static final String FILTER_PROPERTY = "RosterFrame.filter";
    
    /** Interface for a filter on the roster.
     */    
    public static interface AvatarFilter {
        /** Determine whether the given avatar is accepted by this filter.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is accepted by this filter
         */        
        public boolean accept(Avatar avatar);
    }
    
    /** Simple or-combination of avatar filters.
     */    
    public static class AvatarFilterOr implements AvatarFilter {
        /** First filter.
         */        
        protected AvatarFilter f0;
        /** Second filter.
         */        
        protected AvatarFilter f1;
        /** Create an or-combination of the given filters.
         * @param f0 first filter to use
         * @param f1 second filter to use
         */        
        public AvatarFilterOr(AvatarFilter f0, AvatarFilter f1) {
            this.f0 = f0;
            this.f1 = f1;
        }
        /** Determine whether one of the filters accepts the given avatar.
         * @param avatar the avatar to examine
         * @return true iff the first or the second filter accept the given avatar
         */        
        public boolean accept(Avatar avatar) {
            return f0.accept(avatar) || f1.accept(avatar);
        }
    }
    
    /** Simple and-combination of avatar filters.
     */    
    public static class AvatarFilterAnd implements AvatarFilter {
        /** First filter.
         */        
        protected AvatarFilter f0;
        /** Second filter.
         */        
        protected AvatarFilter f1;
        /** Create an and-combination of the given filters.
         * @param f0 first filter to use
         * @param f1 second filter to use
         */        
        public AvatarFilterAnd(AvatarFilter f0, AvatarFilter f1) {
            this.f0 = f0;
            this.f1 = f1;
        }
        /** Determine whether both filters accept the given avatar.
         * @param avatar the avatar to examine
         * @return true iff the first and the second filter accept the given avatar
         */        
        public boolean accept(Avatar avatar) {
            return f0.accept(avatar) && f1.accept(avatar);
        }
    }
    
    /** Avatar filter based on guild.
     */    
    public static class AvatarFilterGuild implements AvatarFilter {
        /** The guild an avatar must be in to be accepted by this filter.
         */        
        protected Guild guild;
        /** Create a new avatar filter based on the given guild.
         * @param guild the guild avatars must be in to be accepted
         */        
        public AvatarFilterGuild(String guild) {
            this.guild = Guild.create(guild);
        }
        /** Determine whether the given avatar is in the guild.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is in the guild for this filter
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getGuild() == guild;
        }
    }
    
    
    /** Avatar filter based on level.
     */    
    public static class AvatarFilterLevel implements AvatarFilter {
        /** The minimum level for an avatar to be accepted.
         */        
        protected int minLevel;
        /** Create a new avatar filter based on the given level.
         * @param minLevel the minimum level for an avatar to be accepted
         */        
        public AvatarFilterLevel(int minLevel) {
            this.minLevel = minLevel;
        }
        /** Determine whether the given avatar is at least minLevel.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is at least minLevel
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getLevel() >= minLevel;
        }
    }
    
    /** Avatar filter based on class.
     */    
    public static class AvatarFilterClass implements AvatarFilter {
        /** Class the avatar has to be to be accepted.
         */        
        protected Word clazz;
        /** Create a new avatar filter based on the given class.
         * @param clazz the class for an avatar to be accepted
         */        
        public AvatarFilterClass(String clazz) {
            this.clazz = Word.create(clazz);
        }
        /** Determine whether the given avatar is of the required class.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is of class
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getClazz() == clazz;
        }
    }
    
    /** Avatar filter based on class.
     */    
    public static class AvatarFilterCulture implements AvatarFilter {
        /** The culture an avatar must belong to be accepted by this filter.
         */        
        protected Culture culture;
        /** Create a new avatar filter based on the given culture.
         * @param culture the culture for an avatar to be accepted
         */        
        public AvatarFilterCulture(String culture) {
            this.culture = Culture.create(culture);
        }
        /** Determine whether the given avatar belongs to culture.
         * @param avatar the avatar to examine
         * @return true iff the given avatar is in culture
         */        
        public boolean accept(Avatar avatar) {
            return avatar.getCulture() == culture;
        }
    }
    
    /** Headers for the roster table.
     */    
    protected static final String headers[] = {
        "Name", "Culture", "Class", "Level", "Guild", "Zone", "Time"
    };

    /** Creates new form RosterFrame */
    public RosterFrame() {
        initComponents();
        doNameOrder();
        level1FilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilterLevel(1));
        defaultFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilterGuild("Mad Wanderer"));
        alliedFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilterGuild("Sovereign Storm"));
        friendFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilterGuild("Veterans Legion"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        parseFileItem = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JSeparator();
        exportTableItem = new javax.swing.JMenuItem();
        exportXMLItem = new javax.swing.JMenuItem();
        exportHTMLItem = new javax.swing.JMenuItem();
        separator2 = new javax.swing.JSeparator();
        quitItem = new javax.swing.JMenuItem();
        filterMenu = new javax.swing.JMenu();
        level1FilterItem = new javax.swing.JCheckBoxMenuItem();
        separator3 = new javax.swing.JSeparator();
        defaultFilterItem = new javax.swing.JCheckBoxMenuItem();
        alliedFilterItem = new javax.swing.JCheckBoxMenuItem();
        friendFilterItem = new javax.swing.JCheckBoxMenuItem();
        separator4 = new javax.swing.JSeparator();
        newFilterMenu = new javax.swing.JMenu();
        newGuildFilterItem = new javax.swing.JMenuItem();
        levelFilterItem = new javax.swing.JMenuItem();
        classFilterItem = new javax.swing.JMenuItem();
        cultureFilterItem = new javax.swing.JMenuItem();
        styleMenu = new javax.swing.JMenu();
        defaultStyleItem = new javax.swing.JRadioButtonMenuItem();
        mwStyleItem = new javax.swing.JRadioButtonMenuItem();
        ssStyleItem = new javax.swing.JRadioButtonMenuItem();
        vlStyleItem = new javax.swing.JRadioButtonMenuItem();
        separator5 = new javax.swing.JSeparator();
        newStyleItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();
        displayHTMLItem = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutItem = new javax.swing.JMenuItem();
        licenseItem = new javax.swing.JMenuItem();
        xpLicenseItem = new javax.swing.JMenuItem();
        xtLicenseItem = new javax.swing.JMenuItem();
        styleButtonGroup = new javax.swing.ButtonGroup();
        tableScroll = new javax.swing.JScrollPane();
        rosterTable = new javax.swing.JTable();
        status = new javax.swing.JTextField();
        
        fileMenu.setText("File");
        parseFileItem.setText("Parse files ...");
        parseFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseFileItemActionPerformed(evt);
            }
        });
        
        fileMenu.add(parseFileItem);
        fileMenu.add(separator1);
        exportTableItem.setText("Export table ...");
        exportTableItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportTableItemActionPerformed(evt);
            }
        });
        
        fileMenu.add(exportTableItem);
        exportXMLItem.setText("Export XML ...");
        exportXMLItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportXMLItemActionPerformed(evt);
            }
        });
        
        fileMenu.add(exportXMLItem);
        exportHTMLItem.setText("Export HTML ...");
        exportHTMLItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportHTMLItemActionPerformed(evt);
            }
        });
        
        fileMenu.add(exportHTMLItem);
        fileMenu.add(separator2);
        quitItem.setText("Quit");
        quitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitItemActionPerformed(evt);
            }
        });
        
        fileMenu.add(quitItem);
        menuBar.add(fileMenu);
        filterMenu.setText("Filter");
        level1FilterItem.setText("no anonymous");
        level1FilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });
        
        filterMenu.add(level1FilterItem);
        filterMenu.add(separator3);
        defaultFilterItem.setText("Mad Wanderer");
        defaultFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });
        
        filterMenu.add(defaultFilterItem);
        alliedFilterItem.setText("Sovereign Storm");
        alliedFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });
        
        filterMenu.add(alliedFilterItem);
        friendFilterItem.setText("Veterans Legion");
        friendFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });
        
        filterMenu.add(friendFilterItem);
        filterMenu.add(separator4);
        newFilterMenu.setText("new Filter");
        newGuildFilterItem.setText("guild filter ...");
        newGuildFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGuildFilterItemActionPerformed(evt);
            }
        });
        
        newFilterMenu.add(newGuildFilterItem);
        levelFilterItem.setText("level filter ...");
        levelFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelFilterItemActionPerformed(evt);
            }
        });
        
        newFilterMenu.add(levelFilterItem);
        classFilterItem.setText("class filter ...");
        classFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classFilterItemActionPerformed(evt);
            }
        });
        
        newFilterMenu.add(classFilterItem);
        cultureFilterItem.setText("culture filter ...");
        cultureFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cultureFilterItemActionPerformed(evt);
            }
        });
        
        newFilterMenu.add(cultureFilterItem);
        filterMenu.add(newFilterMenu);
        menuBar.add(filterMenu);
        styleMenu.setText("Style");
        defaultStyleItem.setSelected(true);
        defaultStyleItem.setText("default (page)");
        defaultStyleItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultStyleItemActionPerformed(evt);
            }
        });
        
        styleMenu.add(defaultStyleItem);
        mwStyleItem.setText("Mad Wanderer");
        mwStyleItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mwStyleItemActionPerformed(evt);
            }
        });
        
        styleMenu.add(mwStyleItem);
        ssStyleItem.setText("Sovereign Storm");
        ssStyleItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ssStyleItemActionPerformed(evt);
            }
        });
        
        styleMenu.add(ssStyleItem);
        vlStyleItem.setText("Veterans Legion");
        vlStyleItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vlStyleItemActionPerformed(evt);
            }
        });
        
        styleMenu.add(vlStyleItem);
        styleMenu.add(separator5);
        newStyleItem.setText("new style ...");
        newStyleItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newStyleItemActionPerformed(evt);
            }
        });
        
        styleMenu.add(newStyleItem);
        menuBar.add(styleMenu);
        optionsMenu.setText("Options");
        displayHTMLItem.setText("display HTML");
        optionsMenu.add(displayHTMLItem);
        menuBar.add(optionsMenu);
        helpMenu.setText("Help");
        aboutItem.setText("About ...");
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });
        
        helpMenu.add(aboutItem);
        licenseItem.setText("GNU License");
        licenseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                licenseItemActionPerformed(evt);
            }
        });
        
        helpMenu.add(licenseItem);
        xpLicenseItem.setText("XP License");
        xpLicenseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xpLicenseItemActionPerformed(evt);
            }
        });
        
        helpMenu.add(xpLicenseItem);
        xtLicenseItem.setText("XT License");
        xtLicenseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xtLicenseItemActionPerformed(evt);
            }
        });
        
        helpMenu.add(xtLicenseItem);
        menuBar.add(helpMenu);
        
        setTitle("YAELP log file parser");
        setIconImage(getToolkit().getImage(getClass().getResource("logo.gif")));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        rosterTable.setPreferredScrollableViewportSize(new java.awt.Dimension(650, 200));
        tableScroll.setViewportView(rosterTable);
        
        getContentPane().add(tableScroll, java.awt.BorderLayout.CENTER);
        
        status.setToolTipText("");
        status.setEditable(false);
        status.setText("Parse some files to add entries.");
        status.setBackground(java.awt.Color.lightGray);
        getContentPane().add(status, java.awt.BorderLayout.SOUTH);
        
        setJMenuBar(menuBar);
        pack();
    }//GEN-END:initComponents

    private void cultureFilterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cultureFilterItemActionPerformed
        // Add your handling code here:
        String input = javax.swing.JOptionPane.showInputDialog(this, "Culture");
        if (input != null) {
            doNewFilter(input, new AvatarFilterCulture(input));
        }
    }//GEN-LAST:event_cultureFilterItemActionPerformed

    private void classFilterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classFilterItemActionPerformed
        // Add your handling code here:
        String input = javax.swing.JOptionPane.showInputDialog(this, "Class");
        if (input != null) {
            doNewFilter(input, new AvatarFilterClass(input));
        }
    }//GEN-LAST:event_classFilterItemActionPerformed

    private void levelFilterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_levelFilterItemActionPerformed
        // Add your handling code here:
        String input = javax.swing.JOptionPane.showInputDialog(this, "Minimum Level");
        if (input != null) try {
            int level = Integer.parseInt(input);
            doNewFilter("min level "+level, new AvatarFilterLevel(level));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            doException(new NumberFormatException("invalid number: "+ex.getMessage()));
        }
    }//GEN-LAST:event_levelFilterItemActionPerformed

    private void newGuildFilterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGuildFilterItemActionPerformed
        // Add your handling code here:
        String input = javax.swing.JOptionPane.showInputDialog(this, "Guild name");
        if (input != null) {
            doNewFilter(input, new AvatarFilterGuild(input));
        }
    }//GEN-LAST:event_newGuildFilterItemActionPerformed

    private void newStyleItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newStyleItemActionPerformed
        // Add your handling code here:
        if (chooser == null) {
            chooser = new javax.swing.JFileChooser();
        }
        chooser.setApproveButtonText("Use Style");
        chooser.setDialogTitle("Use New Style");
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setSelectedFile(null);
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File file) {
                return file.isDirectory() ||
                    file.getName().endsWith(".xsl");
            }
            public String getDescription() {
                return "XSL Style Files (*.xsl)";
            }
        });
        if (chooser.showOpenDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doNewStyle(chooser.getSelectedFile());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_newStyleItemActionPerformed

    private void xtLicenseItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xtLicenseItemActionPerformed
        // Add your handling code here:
        doShowDocument("XSL Transformation License", "copying_xt.txt");
    }//GEN-LAST:event_xtLicenseItemActionPerformed

    private void xpLicenseItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xpLicenseItemActionPerformed
        // Add your handling code here:
        doShowDocument("XML Parser License", "copying_xp.txt");
    }//GEN-LAST:event_xpLicenseItemActionPerformed

    private void vlStyleItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vlStyleItemActionPerformed
        // Add your handling code here:
        style = getClass().getResource("roster_vl.xsl");
    }//GEN-LAST:event_vlStyleItemActionPerformed

    private void ssStyleItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ssStyleItemActionPerformed
        // Add your handling code here:
        style = getClass().getResource("roster_ss.xsl");
    }//GEN-LAST:event_ssStyleItemActionPerformed

    private void mwStyleItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mwStyleItemActionPerformed
        // Add your handling code here:
        style = getClass().getResource("roster_mw.xsl");
    }//GEN-LAST:event_mwStyleItemActionPerformed

    private void defaultStyleItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultStyleItemActionPerformed
        // Add your handling code here:
        style = getClass().getResource("roster.xsl");
    }//GEN-LAST:event_defaultStyleItemActionPerformed

    private void filterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterItemActionPerformed
        // Add your handling code here:
        refreshFilter();
    }//GEN-LAST:event_filterItemActionPerformed

    private void exportHTMLItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportHTMLItemActionPerformed
        // Add your handling code here:
        if (chooser == null) {
            chooser = new javax.swing.JFileChooser();
        }
        chooser.setApproveButtonText("Export");
        chooser.setDialogTitle("Export HTML");
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setSelectedFile(null);
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File file) {
                return file.isDirectory() || file.getName().endsWith(".html");
            }
            public String getDescription() {
                return "HTML Files (*.html)";
            }
        });
        if (chooser.showSaveDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doExportHTML(chooser.getSelectedFile());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_exportHTMLItemActionPerformed

    private void exportXMLItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportXMLItemActionPerformed
        // Add your handling code here:
        if (chooser == null) {
            chooser = new javax.swing.JFileChooser();
        }
        chooser.setApproveButtonText("Export");
        chooser.setDialogTitle("Export XML");
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setSelectedFile(null);
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File file) {
                return file.isDirectory() || file.getName().endsWith(".xml");
            }
            public String getDescription() {
                return "XML Files (*.xml)";
            }
        });
        if (chooser.showSaveDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doExportXML(chooser.getSelectedFile());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_exportXMLItemActionPerformed

    private void licenseItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenseItemActionPerformed
        // Add your handling code here:
        doShowDocument("YAELP License", "gpl.txt");
    }//GEN-LAST:event_licenseItemActionPerformed

    private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemActionPerformed
        // Add your handling code here:
        javax.swing.JOptionPane.showMessageDialog
            (RosterFrame.this, getAboutText(), "About YAELP", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        fixBackingStore();
    }//GEN-LAST:event_aboutItemActionPerformed

    private void exportTableItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportTableItemActionPerformed
        // Add your handling code here:
        if (chooser == null) {
            chooser = new javax.swing.JFileChooser();
        }
        chooser.setApproveButtonText("Export");
        chooser.setDialogTitle("Export table");
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setSelectedFile(null);
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File file) {
                return file.isDirectory() ||
                    file.getName().endsWith(".csv") ||
                    file.getName().endsWith(".txt");
            }
            public String getDescription() {
                return "Table Files (*.csv, *.txt)";
            }
        });
        if (chooser.showSaveDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doExport(chooser.getSelectedFile());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_exportTableItemActionPerformed

    private void quitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_quitItemActionPerformed

    private void parseFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parseFileItemActionPerformed
        // Add your handling code here:
        if (chooser == null) {
            chooser = new javax.swing.JFileChooser();
        }
        chooser.setApproveButtonText("Parse");
        chooser.setDialogTitle("Parse files");
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        chooser.setSelectedFile(null);
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File file) {
                return file.isDirectory() ||
                    file.getName().endsWith(".txt") ||
                    file.getName().endsWith(".txt.gz");
            }
            public String getDescription() {
                return "Log Files (*.txt, *.txt.gz)";
            }
        });
        if (chooser.showOpenDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doParse(chooser.getSelectedFiles());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_parseFileItemActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /** Refresh the filter expression based on the current filter
     * selections.
     */    
    protected void refreshFilter() {
        avatarFilter = null;
        int scan;
        // or-connect all spot filters
        AvatarFilter guildFilter = null;
        AvatarFilter classFilter = null;
        AvatarFilter cultureFilter = null;
        scan = filterMenu.getItemCount();
        while (--scan >= 0) {
            javax.swing.JMenuItem item = filterMenu.getItem(scan);
            if (item != null && item.isSelected()) {
                Object filter = item.getClientProperty(FILTER_PROPERTY);
                if (filter instanceof AvatarFilterGuild) {
                    guildFilter = appendFilterOr(guildFilter, (AvatarFilterGuild)filter);
                } else if (filter instanceof AvatarFilterClass) {
                    classFilter = appendFilterOr(classFilter, (AvatarFilterClass)filter);
                } else if (filter instanceof AvatarFilterCulture) {
                    cultureFilter = appendFilterOr(cultureFilter, (AvatarFilterCulture)filter);
                } else if (filter instanceof AvatarFilter) {
                    avatarFilter = appendFilterAnd(avatarFilter, (AvatarFilter)filter);
                }
            }
        }
        // and-connect the results
        avatarFilter = appendFilterAnd(avatarFilter, guildFilter);
        avatarFilter = appendFilterAnd(avatarFilter, classFilter);
        avatarFilter = appendFilterAnd(avatarFilter, cultureFilter);
        setRoster(recognizer.getAvatars(), recognizer.getLines());
    }
    
    protected static AvatarFilter appendFilterOr(AvatarFilter list, AvatarFilter filter) {
        if (list == null) {
            return filter;
        } else if (filter == null) {
            return list;
        } else {
            return new AvatarFilterOr(list, filter);
        }
    }
    
    protected static AvatarFilter appendFilterAnd(AvatarFilter list, AvatarFilter filter) {
        if (list == null) {
            return filter;
        } else if (filter == null) {
            return list;
        } else {
            return new AvatarFilterAnd(list, filter);
        }
    }
    
    protected void fixBackingStore() {
        // hack to fix dialog backing store bug
        javax.swing.RepaintManager.currentManager(this)
            .markCompletelyDirty(getRootPane());
    }
    
    /** Initialize the roster table with a new list of avatars.
     * @param avatars the avatars to be displayed in the roster
     * @param lines lines parsed to create the provided list
     */    
    protected void setRoster(Avatar avatars[], int lines) {
        if (avatarFilter != null) {
            int end = avatars.length;
            int i0 = 0;
            int i1 = 0;
            while (i0 < end) {
                if (avatarFilter.accept(avatars[i0])) {
                    if (i1 < i0) {
                        avatars[i1] = avatars[i0];
                    }
                    i1++;
                }
                i0++;
            }
            if (i1 < end) {
                Avatar shrink[] = new Avatar[i1];
                System.arraycopy(avatars, 0, shrink, 0, i1);
                avatars = shrink;
            }
        }
        
        if (order != null) {
            java.util.Arrays.sort(avatars, order);
        }
        
        // convert to array form
        Object data[][] = new Object[avatars.length][7];
        for (int i = 0; i < avatars.length; i++) {
            Avatar avatar = avatars[i];
            data[i][0] = avatar.getName();
            data[i][1] = avatar.getCulture();
            data[i][2] = avatar.getClazz();
            data[i][3] = String.valueOf(avatar.getLevel());
            data[i][4] = avatar.getGuild();
            data[i][5] = avatar.getZone();
            data[i][6] = new java.sql.Date(avatar.getTimestamp());
        }
        rosterTable.setModel(new javax.swing.table.DefaultTableModel(data, headers));
        rosterTable.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent ev) {
                javax.swing.table.TableColumnModel tcm = rosterTable.getColumnModel();
                int index = tcm.getColumnIndexAtX(ev.getX());
                if (index >= 0) {
                    rosterTable.getTableHeader().removeMouseListener(this);
                    doOrder(tcm.getColumn(index).getIdentifier().toString());
                    refreshFilter();
                }
            }
        });
        status.setText(data.length + " characters, " + lines + " lines parsed.");
    }
    
    protected void doNameOrder() {
        // sort by name
        order = new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                return ((Avatar)a).getName().toString()
                .compareTo(((Avatar)b).getName().toString());
            }
        };
    }
    
    protected void doClassOrder() {
        // sort by class
        order = new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                return stringValue(((Avatar)a).getClazz())
                .compareTo(stringValue(((Avatar)b).getClazz()));
            }
        };
    }
    
    protected void doCultureOrder() {
        // sort by culture
        order = new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                return stringValue(((Avatar)a).getCulture())
                .compareTo(stringValue(((Avatar)b).getCulture()));
            }
        };
    }
    
    protected void doLevelOrder() {
        // sort by level
        order = new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                int delta = ((Avatar)a).getLevel() - ((Avatar)b).getLevel();
                if (delta < 0) {
                    return 1;
                } else if (delta > 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }
    
    protected void doTimeOrder() {
        // sort by time
        order = new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                long delta = ((Avatar)a).getTimestamp() - ((Avatar)b).getTimestamp();
                if (delta < 0) {
                    return 1;
                } else if (delta > 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }
    
    protected void doOrder(String order) {
        order = order.toLowerCase(); // HACK
        if (order.equals("name")) {
            doNameOrder();
        } else if (order.equals("class")) {
            doClassOrder();
        } else if (order.equals("culture")) {
            doCultureOrder();
        } else if (order.equals("level")) {
            doLevelOrder();
        } else if (order.equals("time")) {
            doTimeOrder();
        }
    }
    /** Parse a list of files for who lines.
     * @param files array of files to parse
     */    
    protected void doParse(java.io.File files[]) {
        try {
            for (int i = 0; i < files.length; i++) {
                EqlogParser.parseFile(files[i], recognizer);
            }
            setRoster(recognizer.getAvatars(), recognizer.getLines());
        } catch (Exception ex) {
            ex.printStackTrace();
            doException(ex);
        }
    }
    
    protected void doShowDocument(String title, String resource) {
        doShowDocument(title, getClass().getResource(resource));
    }
    
    protected void doShowDocument(String title, java.net.URL text) {
        try {
            javax.swing.JEditorPane editorPane =
                new javax.swing.JEditorPane(text);
            editorPane.setEditable(false);
            javax.swing.JScrollPane scrollPane =
                new javax.swing.JScrollPane(editorPane);
            scrollPane.setPreferredSize(new java.awt.Dimension(550, 250));
            javax.swing.JOptionPane.showMessageDialog
                (this, scrollPane, title, javax.swing.JOptionPane.INFORMATION_MESSAGE);
            fixBackingStore();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            doException(ex);
        }
    }

    /** Export the current roster to a file.
     * @param file file to write the exported roster into
     */    
    protected void doExport(java.io.File file) {
        if (!doWriteConfirmation(file)) {
            return; // nothing selected, canceled.
        }
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            java.io.PrintStream out = new java.io.PrintStream(fos);
            
            // create header
            javax.swing.table.TableColumnModel header = rosterTable.getColumnModel();
            int cols = header.getColumnCount();
            for (int i = 0; i < cols; i++) {
                if (i > 0) {
                    out.print(", ");
                }
                out.print(quote(header.getColumn(i).getHeaderValue().toString()));
            }
            out.println();
            
            // create table contents
            javax.swing.table.TableModel model = rosterTable.getModel();
            int rows = model.getRowCount();
            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < cols; i++) {
                    if (i > 0) {
                        out.print(", ");
                    }
                    Object value = model.getValueAt(j, i);
                    if (value != null) {
                        out.print(quote(value.toString()));
                    } else {
                        out.print("\"\"");
                    }
                }
                out.println();
            }
            
            out.close();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            doException(ex);
        }
    }
    
    protected void doNewFilter(String name, AvatarFilter filter) {
        javax.swing.JCheckBoxMenuItem menuItem =
            new javax.swing.JCheckBoxMenuItem(name, true);
        menuItem.putClientProperty(FILTER_PROPERTY, filter);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshFilter();
            }
        });
        filterMenu.insert(menuItem, filterMenu.getItemCount()-1);
        refreshFilter();
    }
    
    protected void doNewStyle(java.io.File file) {
        if (file == null) {
            return; // canceled
        }
        if (file.exists()) try {
            final java.net.URL styleURL =
                new java.net.URL("file:///"+file.getAbsolutePath());
            javax.swing.JRadioButtonMenuItem menuItem =
                new javax.swing.JRadioButtonMenuItem(file.getName());
            menuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    style = styleURL;
                }
            });
            styleButtonGroup.add(menuItem);
            styleMenu.insert(menuItem, styleMenu.getItemCount()-1);
            menuItem.setSelected(true);
            style = styleURL;
        } catch (Exception ex) {
            ex.printStackTrace();
            doException(ex);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "File does not exist");
        }
    }
    
    /** Export the current roster to an XML file.
     * @param file file to write the exported roster into
     */    
    protected void doExportXML(java.io.File file) {
        if (!doWriteConfirmation(file)) {
            return; // nothing selected, canceled.
        }
        try {
            java.io.FileWriter out = new java.io.FileWriter(file);
            writeXML(out);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            doException(ex);
        }
    }
    
    protected XSLProcessor processor;
    protected OutputMethodHandlerImpl handler;
    protected java.net.URL loadedStyle;
    
    /** Export the current roster to a HTML file.
     * @param file file to write the exported roster into
     */    
    protected void doExportHTML(java.io.File file) {
        if (!doWriteConfirmation(file)) {
            return; // nothing selected, canceled.
        }
        try {
            if (style == null) {
                style = getClass().getResource("roster.xsl");
            }
            if (processor == null) {
                processor = new XSLProcessorImpl();
                processor.setParser(new CommentDriver());
                processor.setErrorHandler(new ErrorHandler() {
                    public void warning(SAXParseException e) {
                        doException(e);
                    }
                    public void error(SAXParseException e) {
                        doException(e);
                    }
                    public void fatalError(SAXParseException e) throws SAXException {
                        throw e;
                    }
                });
                handler = new OutputMethodHandlerImpl(processor);
                processor.setOutputMethodHandler(handler);
            }
            if (loadedStyle == null || !loadedStyle.equals(style)) {
                java.io.InputStream xsl = style.openStream();
                processor.loadStylesheet(new InputSource(xsl));
                xsl.close();
                loadedStyle = style;
            }
            Destination out = new FileDestination(file);
            handler.setDestination(out);
            final java.io.PipedWriter pw = new java.io.PipedWriter();
            new Thread() {
                public void run() {
                    try {
                        writeXML(pw);
                        pw.close();
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
            processor.parse(new InputSource(new java.io.PipedReader(pw)));
            
            if (displayHTMLItem.isSelected()) {
                doShowDocument("HTML Export",
                    new java.net.URL("file:///"+file.getAbsolutePath()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            doException(ex);
        }
    }
    
    protected void writeXML(java.io.Writer out) throws java.io.IOException {
        java.io.PrintWriter pw = new java.io.PrintWriter(out);
        pw.println("<roster title=\"Roster\">"); // FIXME: allow setting title
        // create tag names
        javax.swing.table.TableColumnModel header = rosterTable.getColumnModel();
        int cols = header.getColumnCount();
        String tags[] = new String[cols];
        pw.println(" <heading>");
        for (int i = 0; i < cols; i++) {
            // HACK: we don't set identifiers explicitly yet,
            // so we use lower case header values.
            javax.swing.table.TableColumn column = header.getColumn(i);
            tags[i] = column.getIdentifier().toString().toLowerCase();
            pw.print("  <column id=\"");
            pw.print(tags[i]);
            pw.print("\">");
            pw.print(column.getHeaderValue());
            pw.println("</column>");
        }
        pw.println(" </heading>");
        
        // create XML
        int rows = rosterTable.getRowCount();
        for (int j = 0; j < rows; j++) {
            pw.println(" <avatar>");
            for (int i = 0; i < cols; i++) {
                pw.print("  <");
                pw.print(tags[i]);
                Object value = rosterTable.getValueAt(j, i);
                if (value != null) {
                    pw.print(">");
                    pw.print(pcdata(value.toString()));
                    pw.print("</");
                    pw.print(tags[i]);
                    pw.println(">");
                } else {
                    pw.println("/>");
                }
            }
            pw.println(" </avatar>");
        }
        pw.println("</roster>");
        pw.flush();
    }
    
    protected boolean doWriteConfirmation(java.io.File file) {
        if (file == null) {
            return false; // already canceled
        } else if (file.exists()) {
            return javax.swing.JOptionPane.OK_OPTION ==
                javax.swing.JOptionPane.showConfirmDialog
                    (this, "Overwrite "+file+"?", "Overwrite Confirmation",
                     javax.swing.JOptionPane.OK_CANCEL_OPTION);
        }
        return true;
    }
    
    protected void doException(Throwable ex) {
        String message = ex.getMessage();
        if (message == null) {
            message = ex.toString();
        }
        javax.swing.JOptionPane.showMessageDialog
        (RosterFrame.this, message, "Exception", javax.swing.JOptionPane.ERROR_MESSAGE);
        fixBackingStore();
    }
    
    public String getAboutText() {
        if (aboutText == null) try {
            //StringBuffer buffer = new StringBuffer("\n");
            java.io.InputStream stream =
            getClass().getResource("gpl.txt").openStream();
            stream.skip(15816);             // HACK
            byte text[] = new byte[720];    // HACK
            int count = 0;
            while (count < text.length) {
                int n = stream.read(text, count, text.length-count);
                if (n > 0) {
                    count += n;
                } else if (n == -1) {
                    break;
                }
            }
            aboutText =
            "YAELP log file parser, $Revision: 1.2 $.\n"+
            "Copyright © 2001 Klaus Rennecke.\n"+
            "XML parser Copyright © 1997, 1998 James Clark.\n"+
            "XSL transformation Copyright © 1998, 1999 James Clark.\n"+
            "\n"+
            new String(text);
        } catch (java.io.IOException ex) {
            aboutText = "<ERROR: text missing>";
            ex.printStackTrace();
            doException(ex);
        }
        return aboutText;
    }
    
    /** Quote a string, escaping quotes in the original string in the process.
     * @param str the string to quote
     * @return the quoted string
     */
    public static String quote(String str) {
        StringBuffer buffer = new StringBuffer();
        buffer.append('"');
        if (str.indexOf('"') >= 0) {
            java.util.StringTokenizer tok = new java.util.StringTokenizer(str, "\"", true);
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                if (token.equals("\"")) {
                    buffer.append("\\\"");
                } else {
                    buffer.append(token);
                }
            }
        } else {
            buffer.append(str);
        }
        buffer.append('"');
        return buffer.toString();
    }
    
    /** Quote a string, ready to use in a #PCDATA section of XML
     * @param str the string to quote
     * @return the quoted string
     */
    public static String pcdata(String str) {
        StringBuffer buffer = new StringBuffer();
        java.util.StringTokenizer tok = new java.util.StringTokenizer(str, "&<>", true);
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            if (token.length() == 1) {
                switch(token.charAt(0)) {
                    case '<':
                        buffer.append("&lt;");
                        break;
                    case '>':
                        buffer.append("&gt;");
                        break;
                    case '&':
                        buffer.append("&amp;");
                        break;
                    default:
                        buffer.append(token);
                }
            } else {
                buffer.append(token);
            }
        }
        return buffer.toString();
    }
    
    public static String stringValue(Object o) {
        if (o == null) {
            return "";
        } else {
            return o.toString();
        }
    }
    
    /** Start the application with a RosterFrame.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        RosterFrame frame = new RosterFrame();
        System.setOut(new java.io.PrintStream
        (new DocumentStream(frame.status)));
        frame.show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem parseFileItem;
    private javax.swing.JSeparator separator1;
    private javax.swing.JMenuItem exportTableItem;
    private javax.swing.JMenuItem exportXMLItem;
    private javax.swing.JMenuItem exportHTMLItem;
    private javax.swing.JSeparator separator2;
    private javax.swing.JMenuItem quitItem;
    private javax.swing.JMenu filterMenu;
    private javax.swing.JCheckBoxMenuItem level1FilterItem;
    private javax.swing.JSeparator separator3;
    private javax.swing.JCheckBoxMenuItem defaultFilterItem;
    private javax.swing.JCheckBoxMenuItem alliedFilterItem;
    private javax.swing.JCheckBoxMenuItem friendFilterItem;
    private javax.swing.JSeparator separator4;
    private javax.swing.JMenu newFilterMenu;
    private javax.swing.JMenuItem newGuildFilterItem;
    private javax.swing.JMenuItem levelFilterItem;
    private javax.swing.JMenuItem classFilterItem;
    private javax.swing.JMenuItem cultureFilterItem;
    private javax.swing.JMenu styleMenu;
    private javax.swing.JRadioButtonMenuItem defaultStyleItem;
    private javax.swing.JRadioButtonMenuItem mwStyleItem;
    private javax.swing.JRadioButtonMenuItem ssStyleItem;
    private javax.swing.JRadioButtonMenuItem vlStyleItem;
    private javax.swing.JSeparator separator5;
    private javax.swing.JMenuItem newStyleItem;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JCheckBoxMenuItem displayHTMLItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JMenuItem licenseItem;
    private javax.swing.JMenuItem xpLicenseItem;
    private javax.swing.JMenuItem xtLicenseItem;
    private javax.swing.ButtonGroup styleButtonGroup;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable rosterTable;
    private javax.swing.JTextField status;
    // End of variables declaration//GEN-END:variables

}
