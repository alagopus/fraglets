/*
 * RosterFrame.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

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
import java.util.HashMap;
import javax.swing.JFileChooser;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.Arrays;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.TableCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.IOException;
import java.io.BufferedInputStream;
import javax.swing.Box;
import java.io.OutputStream;
import javax.swing.JEditorPane;
import java.io.StringWriter;
import java.net.URL;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import net.sourceforge.fraglets.targa.TGADecoder;
import net.sourceforge.fraglets.yaelp.action.AboutAction;
import net.sourceforge.fraglets.yaelp.action.ActionContext;
import net.sourceforge.fraglets.yaelp.action.AfterFilterAction;
import net.sourceforge.fraglets.yaelp.action.ClassFilterAction;
import net.sourceforge.fraglets.yaelp.action.CultureFilterAction;
import net.sourceforge.fraglets.yaelp.action.GenericAction;
import net.sourceforge.fraglets.yaelp.action.GuildFilterAction;
import net.sourceforge.fraglets.yaelp.action.LevelFilterAction;
import net.sourceforge.fraglets.yaelp.action.PropertyEditorAction;
import net.sourceforge.fraglets.yaelp.action.PropertyFilterAction;
import net.sourceforge.fraglets.yaelp.bean.AvatarCellRenderer;
import net.sourceforge.fraglets.yaelp.bean.DateInput;
import net.sourceforge.fraglets.yaelp.bean.Defaults;
import net.sourceforge.fraglets.yaelp.bean.DocumentStream;
import net.sourceforge.fraglets.yaelp.bean.LevelLabel;
import net.sourceforge.fraglets.yaelp.bean.LevelSlider;
import net.sourceforge.fraglets.yaelp.bean.PropertyInput;
import net.sourceforge.fraglets.yaelp.bean.RosterTableModel;
import net.sourceforge.fraglets.yaelp.bean.TransferableTableSelection;
import net.sourceforge.fraglets.yaelp.model.Avatar;
import net.sourceforge.fraglets.yaelp.model.AvatarFilter;
import net.sourceforge.fraglets.yaelp.model.EqXMLParser;
import net.sourceforge.fraglets.yaelp.model.EqlogParser;
import net.sourceforge.fraglets.yaelp.model.Recognizer;
import net.sourceforge.fraglets.yaelp.model.Tricks;

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
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.33 $
 */
public class RosterFrame extends javax.swing.JFrame implements ActionContext {
    /** The file chooser map used to select files to parse and export.
     */
    protected HashMap chooserMap;
    /** The last chooser category used. */
    protected String lastChooser;
    /** The table model. */
    protected RosterTableModel model = new RosterTableModel();
    /** The recognizer used to parse the input files.
     */    
    protected Recognizer recognizer = new Recognizer();
    /** The current avatar filter, if any.
     */    
    protected AvatarFilter avatarFilter;
    
    protected String aboutText;
    
    protected StyleSelection styleSelection
        = new StyleSelection(getResource("roster.xsl"));
    
    /** Client property key for filter property. */
    public static final String FILTER_PROPERTY = "RosterFrame.filter";
    /** Client property key for style property. */
    public static final String STYLE_PROPERTY = "RosterFrame.style";
    
    
    public static class StyleSelection extends javax.swing.AbstractAction {
        private java.net.URL selectedStyle;
        public StyleSelection(java.net.URL initialSelection) {
            super("select style");
            this.selectedStyle = initialSelection;
        }
        public java.net.URL getSelectedStyle() {
            return selectedStyle;
        }
        public void setSelectedStyle(java.net.URL newStyle) {
            selectedStyle = newStyle;
        }
        public void actionPerformed(java.awt.event.ActionEvent ev) {
            Object source = ev.getSource();
            if (source instanceof javax.swing.JComponent) {
                Object value = ((javax.swing.JComponent)source)
                    .getClientProperty(STYLE_PROPERTY);
                if (value instanceof java.net.URL) {
                    selectedStyle = (java.net.URL)value;
                }
            }
        }
    }
    
    public class StringDestination extends StringWriter implements Destination {
        public String getEncoding() {
            return "UCS-2";
        }
        
        public OutputStream getOutputStream(String str, String str1) {
            return null;
        }
        
        public Writer getWriter(String str, String str1) {
            return this;
        }
        
        public boolean keepOpen() {
            return false;
        }
        
        public Destination resolve(String str) {
            return null;
        }
    }
    
    /** Creates new form RosterFrame */
    public RosterFrame(boolean appletFrame) {
        this();
        setAppletFrame(appletFrame);
    }
    
    /** Creates new form RosterFrame */
    public RosterFrame() {
        initComponents();
        GenericAction.setActionContext(this, this);

        // initialize sorting
        doNameOrder();
        rosterTable.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent ev) {
                if (isPopupTrigger(ev)) {
                    contextMenu.show((java.awt.Component)ev.getSource(),
                                     ev.getX(), ev.getY());
                } else {
                    javax.swing.table.JTableHeader header = rosterTable.getTableHeader();
                    int index = header.columnAtPoint(ev.getPoint());
                    if (index >= 0) {
                        doOrder(rosterTable.getColumnModel().getColumn(index)
                                .getIdentifier().toString());
                    }
                }
            }
        });
        rosterTable.setSelectionMode
            (javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rosterTable.setDefaultRenderer(Object.class, new AvatarCellRenderer());
        TransferableTableSelection.createTableGestureListener(rosterTable);
        
        // set editors
        Avatar.CHANGE.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent ev) {
                updateEditor(ev.getNewValue());
            }
        });
        initEditors();
        
        // make this filter item bold to match element rendering
        mainFilterItem.setFont(mainFilterItem.getFont().deriveFont(Font.BOLD));

        // attach filters
        mainFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilter.Main());
        level1FilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilter.Level(1));
        defaultFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilter.Guild("New Elements of Power"));
        alliedFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilter.Guild("Veterans Legion"));
        friendFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilter.Guild("Jade Dynasty"));
        memberFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilter.Property("Rank", "a regular member"));
        officerFilterItem.putClientProperty(FILTER_PROPERTY,
            new AvatarFilter.Property("Rank", "an officer"));
        
        // attach styles
        defaultStyleItem.putClientProperty(STYLE_PROPERTY,
            getResource("roster.xsl"));
        neopStyleItem.putClientProperty(STYLE_PROPERTY,
            getResource("roster_neop.xsl"));
        mwStyleItem.putClientProperty(STYLE_PROPERTY,
            getResource("roster_mw.xsl"));
        ssStyleItem.putClientProperty(STYLE_PROPERTY,
            getResource("roster_ss.xsl"));
        vlStyleItem.putClientProperty(STYLE_PROPERTY,
            getResource("roster_vl.xsl"));
        
        // HACK: Since the Netbeans 3.2 IDE drops all button
        // group assignments on CVS checkin, we do it here.
        // Likewise, we can't set the button name after the action
        // so the text would get mangled.
        styleButtonGroup.add(defaultStyleItem);
        defaultStyleItem.addActionListener(styleSelection);
        styleButtonGroup.add(neopStyleItem);
        neopStyleItem.addActionListener(styleSelection);
        styleButtonGroup.add(mwStyleItem);
        mwStyleItem.addActionListener(styleSelection);
        styleButtonGroup.add(ssStyleItem);
        ssStyleItem.addActionListener(styleSelection);
        styleButtonGroup.add(vlStyleItem);
        vlStyleItem.addActionListener(styleSelection);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        net.sourceforge.fraglets.yaelp.action.ShowLicenseAction xpLicenseAction;
        net.sourceforge.fraglets.yaelp.action.ShowLicenseAction xtLicenseAction;
        javax.swing.JMenu newFilterMenu;
        net.sourceforge.fraglets.yaelp.action.ShowLicenseAction gplLicenseAction;
        javax.swing.JMenu helpMenu;

        styleButtonGroup = new javax.swing.ButtonGroup();
        contextMenu = new javax.swing.JPopupMenu();
        jSeparator3 = new javax.swing.JSeparator();
        newEntryItem = new javax.swing.JMenuItem();
        hideMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        dingMenuItem = new javax.swing.JMenuItem();
        dohMenuItem = new javax.swing.JMenuItem();
        propertyInput = new net.sourceforge.fraglets.yaelp.bean.PropertyInput();
        propertyEditor = new net.sourceforge.fraglets.yaelp.bean.PropertyEditor();
        gplLicenseAction = new net.sourceforge.fraglets.yaelp.action.ShowLicenseAction();
        xpLicenseAction = new net.sourceforge.fraglets.yaelp.action.ShowLicenseAction();
        xtLicenseAction = new net.sourceforge.fraglets.yaelp.action.ShowLicenseAction();
        tableScroll = new javax.swing.JScrollPane();
        rosterTable = new javax.swing.JTable();
        statusPanel = new javax.swing.JPanel();
        status = new javax.swing.JTextField();
        selectionButton = new net.sourceforge.fraglets.yaelp.bean.SelectionButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        parseFileItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        saveFileItem = new javax.swing.JMenuItem();
        importFileItem = new javax.swing.JMenuItem();
        loadFileItem = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JSeparator();
        exportTableItem = new javax.swing.JMenuItem();
        exportXMLItem = new javax.swing.JMenuItem();
        exportHTMLItem = new javax.swing.JMenuItem();
        separator2 = new javax.swing.JSeparator();
        quitItem = new javax.swing.JMenuItem();
        filterMenu = new javax.swing.JMenu();
        noFilterItem = new javax.swing.JMenuItem();
        invertFilterItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mainFilterItem = new javax.swing.JCheckBoxMenuItem();
        level1FilterItem = new javax.swing.JCheckBoxMenuItem();
        separator3 = new javax.swing.JSeparator();
        defaultFilterItem = new javax.swing.JCheckBoxMenuItem();
        alliedFilterItem = new javax.swing.JCheckBoxMenuItem();
        friendFilterItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        memberFilterItem = new javax.swing.JCheckBoxMenuItem();
        officerFilterItem = new javax.swing.JCheckBoxMenuItem();
        separator4 = new javax.swing.JSeparator();
        newFilterMenu = new javax.swing.JMenu();
        styleMenu = new javax.swing.JMenu();
        defaultStyleItem = new javax.swing.JRadioButtonMenuItem();
        separator5 = new javax.swing.JSeparator();
        neopStyleItem = new javax.swing.JRadioButtonMenuItem();
        vlStyleItem = new javax.swing.JRadioButtonMenuItem();
        mwStyleItem = new javax.swing.JRadioButtonMenuItem();
        ssStyleItem = new javax.swing.JRadioButtonMenuItem();
        separator6 = new javax.swing.JSeparator();
        newStyleMenu = new javax.swing.JMenu();
        styleWizardItem = new javax.swing.JMenuItem();
        styleFileItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();
        displayHTMLItem = new javax.swing.JCheckBoxMenuItem();
        exportToClipboardItem = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();

        contextMenu.add(Defaults.configure(new PropertyEditorAction()));
        contextMenu.setLabel("Edit");
        contextMenu.add(jSeparator3);

        newEntryItem.setText("new entry");
        newEntryItem.setToolTipText("Create a new entry.");
        newEntryItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newEntryItemActionPerformed(evt);
            }
        });

        contextMenu.add(newEntryItem);

        hideMenuItem.setText("hide selected");
        hideMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideMenuItemActionPerformed(evt);
            }
        });

        contextMenu.add(hideMenuItem);

        contextMenu.add(jSeparator1);

        dingMenuItem.setText("DING!");
        dingMenuItem.setToolTipText("Increase level by one.");
        dingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dingMenuItemActionPerformed(evt);
            }
        });

        contextMenu.add(dingMenuItem);

        dohMenuItem.setText("DOH :(");
        dohMenuItem.setToolTipText("Decrease level by one.");
        dohMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dohMenuItemActionPerformed(evt);
            }
        });

        contextMenu.add(dohMenuItem);

        propertyEditor.setModal(true);
        gplLicenseAction.setCommand("gplLicense");
        gplLicenseAction.setResource("gpl.txt");
        gplLicenseAction.setTitle("YAELP License");
        xpLicenseAction.setCommand("xpLicense");
        xpLicenseAction.setResource("copying_xp.txt");
        xpLicenseAction.setTitle("XP License");
        xtLicenseAction.setCommand("xtLicense");
        xtLicenseAction.setResource("copying_xt.txt");
        xtLicenseAction.setTitle("XT License");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("YAELP log file parser");
        setIconImage(getToolkit().getImage(getClass().getResource("logo.gif")));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        rosterTable.setModel(model);
        rosterTable.setPreferredScrollableViewportSize(new java.awt.Dimension(650, 200));
        rosterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rosterTableMouseClicked(evt);
            }
        });

        tableScroll.setViewportView(rosterTable);

        getContentPane().add(tableScroll, java.awt.BorderLayout.CENTER);

        statusPanel.setLayout(new javax.swing.BoxLayout(statusPanel, javax.swing.BoxLayout.X_AXIS));

        status.setBackground(java.awt.Color.lightGray);
        status.setEditable(false);
        status.setText("Parse some files to add entries.");
        status.setToolTipText("");
        statusPanel.add(status);

        selectionButton.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 5, 1, 5)));
        selectionButton.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("selectionButton.text"));
        selectionButton.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("selectionButton.tooltip"));
        statusPanel.add(selectionButton);

        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        fileMenu.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("fileMenu.text"));
        parseFileItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("parseFileItem.text"));
        parseFileItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("parseFileItem.tooltip"));
        parseFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseFileItemActionPerformed(evt);
            }
        });

        fileMenu.add(parseFileItem);

        fileMenu.add(jSeparator5);

        saveFileItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("saveFileItem.text"));
        saveFileItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("saveFileItem.tooltip"));
        saveFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveFileItem);

        importFileItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("importFileItem.text"));
        importFileItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("importFileItem.tooltip"));
        importFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFileItemActionPerformed(evt);
            }
        });

        fileMenu.add(importFileItem);

        loadFileItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("loadFileItem.text"));
        loadFileItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("loadFileItem.tooltip"));
        loadFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFileItemActionPerformed(evt);
            }
        });

        fileMenu.add(loadFileItem);

        fileMenu.add(separator1);

        exportTableItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportTableItem.text"));
        exportTableItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportTableItem.tooltip"));
        exportTableItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportTableItemActionPerformed(evt);
            }
        });

        fileMenu.add(exportTableItem);

        exportXMLItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportXMLItem.text"));
        exportXMLItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportXMLItem.tooltip"));
        exportXMLItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportXMLItemActionPerformed(evt);
            }
        });

        fileMenu.add(exportXMLItem);

        exportHTMLItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportHTMLItem.text"));
        exportHTMLItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportHTMLItem.tooltip"));
        exportHTMLItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportHTMLItemActionPerformed(evt);
            }
        });

        fileMenu.add(exportHTMLItem);

        fileMenu.add(separator2);

        quitItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("quitItem.text"));
        quitItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("quitItem.tooltip"));
        quitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitItemActionPerformed(evt);
            }
        });

        fileMenu.add(quitItem);

        menuBar.add(fileMenu);

        filterMenu.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("filterMenu.text"));
        filterMenu.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("filterMenu.tooltip"));
        noFilterItem.setText("no filter");
        noFilterItem.setToolTipText("Disable all filters.");
        noFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noFilterItemActionPerformed(evt);
            }
        });

        filterMenu.add(noFilterItem);

        invertFilterItem.setText("invert filter");
        invertFilterItem.setToolTipText("Invert all selected filter items.");
        invertFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertFilterItemActionPerformed(evt);
            }
        });

        filterMenu.add(invertFilterItem);

        filterMenu.add(jSeparator2);

        mainFilterItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("mainFilterItem.text"));
        mainFilterItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("mainFilterItem.tooltip"));
        mainFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });

        filterMenu.add(mainFilterItem);

        level1FilterItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("level1FilterItem.text"));
        level1FilterItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("level1FilterItem.tooltip"));
        level1FilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });

        filterMenu.add(level1FilterItem);

        filterMenu.add(separator3);

        defaultFilterItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("defaultFilterItem.text"));
        defaultFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });

        filterMenu.add(defaultFilterItem);

        alliedFilterItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("alliedFilterItem.text"));
        alliedFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });

        filterMenu.add(alliedFilterItem);

        friendFilterItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("friendFilterItem.text"));
        friendFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });

        filterMenu.add(friendFilterItem);

        filterMenu.add(jSeparator4);

        memberFilterItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("memberFilterItem.text"));
        memberFilterItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("memberFilterItem.tooltip"));
        memberFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });

        filterMenu.add(memberFilterItem);

        officerFilterItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("officerFilterItem.text"));
        officerFilterItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("officerFilterItem.tooltip"));
        officerFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterItemActionPerformed(evt);
            }
        });

        filterMenu.add(officerFilterItem);

        filterMenu.add(separator4);

        newFilterMenu.setText("newFilterMenu");
        newFilterMenu.setName("newFilterMenu");
        Defaults.configure(newFilterMenu);
        newFilterMenu.add(Defaults.configure(new GuildFilterAction()));
        newFilterMenu.add(Defaults.configure(new LevelFilterAction()));
        newFilterMenu.add(Defaults.configure(new PropertyFilterAction()));
        newFilterMenu.add(Defaults.configure(new ClassFilterAction()));
        newFilterMenu.add(Defaults.configure(new CultureFilterAction()));
        newFilterMenu.add(Defaults.configure(new AfterFilterAction()));
        filterMenu.add(newFilterMenu);

        menuBar.add(filterMenu);

        styleMenu.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("styleMenu.text"));
        styleMenu.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("styleMenu.tooltip"));
        defaultStyleItem.setSelected(true);
        defaultStyleItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("defaultStyleItem.text"));
        defaultStyleItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("defaultStyleItem.tooltip"));
        styleMenu.add(defaultStyleItem);

        styleMenu.add(separator5);

        neopStyleItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("neopStyleItem.text"));
        neopStyleItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("neopStyleItem.tooltip"));
        styleMenu.add(neopStyleItem);

        vlStyleItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("vlStyleItem.text"));
        vlStyleItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("vlStyleItem.tooltip"));
        styleMenu.add(vlStyleItem);

        mwStyleItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("mwStyleItem.text"));
        mwStyleItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("mwStyleItem.tooltip"));
        styleMenu.add(mwStyleItem);

        ssStyleItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("ssStyleItem.text"));
        ssStyleItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("ssStyleItem.tooltip"));
        styleMenu.add(ssStyleItem);

        styleMenu.add(separator6);

        newStyleMenu.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("newStyleMenu.text"));
        styleWizardItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("styleWizardItem.text"));
        styleWizardItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("styleWizardItem.tooltip"));
        styleWizardItem.setEnabled(false);
        newStyleMenu.add(styleWizardItem);

        styleFileItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("styleFileItem.text"));
        styleFileItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("styleFileItem.tooltip"));
        styleFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleFileItemActionPerformed(evt);
            }
        });

        newStyleMenu.add(styleFileItem);

        styleMenu.add(newStyleMenu);

        menuBar.add(styleMenu);

        optionsMenu.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("optionsMenu.text"));
        displayHTMLItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("displayHTMLItem.text"));
        displayHTMLItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("displayHTMLItem.tooltip"));
        optionsMenu.add(displayHTMLItem);

        exportToClipboardItem.setText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportToClipboardItem.text"));
        exportToClipboardItem.setToolTipText(java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/YaelpResources").getString("exportToClipboardItem.tooltip"));
        optionsMenu.add(exportToClipboardItem);

        menuBar.add(optionsMenu);

        helpMenu.setText("help");
        helpMenu.setName("helpMenu");
        Defaults.configure(helpMenu);
        helpMenu.add(Defaults.configure(new AboutAction()));
        helpMenu.add(gplLicenseAction);
        helpMenu.add(xpLicenseAction);
        helpMenu.add(xtLicenseAction);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }//GEN-END:initComponents

    private void invertFilterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertFilterItemActionPerformed
        // Add your handling code here:
        invertFilter();
    }//GEN-LAST:event_invertFilterItemActionPerformed

    private void loadFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFileItemActionPerformed
        // Add your handling code here:
        if (recognizer.getAvatarCount() > 0 &&
            JOptionPane.showConfirmDialog(this,
            "This will clear the current list. Continue?", "Load Roster",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)
            != JOptionPane.OK_OPTION) {
            return; // canceled
        }
        JFileChooser chooser = getChooser("yxr");
        chooser.setApproveButtonText("Load");
        chooser.setDialogTitle("Load Roster");
        if (chooser.showOpenDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doImport(chooser.getSelectedFile(), true);
        } else {
            fixBackingStore();
        }
        chooser.setSelectedFile(null);
    }//GEN-LAST:event_loadFileItemActionPerformed

    private void importFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFileItemActionPerformed
        // Add your handling code here:
        JFileChooser chooser = getChooser("yxr");
        chooser.setApproveButtonText("Import");
        chooser.setDialogTitle("Import Roster");
        if (chooser.showOpenDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doImport(chooser.getSelectedFile(), false);
        } else {
            fixBackingStore();
        }
        chooser.setSelectedFile(null);
    }//GEN-LAST:event_importFileItemActionPerformed

    private void saveFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileItemActionPerformed
        // Add your handling code here:
        JFileChooser chooser = getChooser("yxr");
        chooser.setApproveButtonText("Save");
        chooser.setDialogTitle("Save Roster");
        if (chooser.showSaveDialog(this) == chooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.getName().indexOf('.') == -1) {
                file = new File(file.getParent(), file.getName()+".yxr");
            }
            fixBackingStore();
            doExportXML(file, true);
            setChanged(false);
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_saveFileItemActionPerformed

    private void dohMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dohMenuItemActionPerformed
        int rows[] = rosterTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            Avatar avatar = model.getAvatar(rows[i]);
            int level = avatar.getLevel();
            if (level > 1) {
                avatar.setLevel(level - 1);
                model.fireTableCellUpdated(rows[i], 3); // HACK
            }
        }
    }//GEN-LAST:event_dohMenuItemActionPerformed

    private void dingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dingMenuItemActionPerformed
        int rows[] = rosterTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            Avatar avatar = model.getAvatar(rows[i]);
            int level = avatar.getLevel();
            if (level > 0) {
                avatar.setLevel(level + 1);
                model.fireTableCellUpdated(rows[i], 3); // HACK
            }
        }
    }//GEN-LAST:event_dingMenuItemActionPerformed

    private void noFilterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noFilterItemActionPerformed
        int scan = filterMenu.getItemCount();
        while (--scan >= 0) {
            JMenuItem item = filterMenu.getItem(scan);
            if (item != null && item.isSelected()) {
                item.setSelected(false);
            }
        }
        refreshFilter();
    }//GEN-LAST:event_noFilterItemActionPerformed

    private void newEntryItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newEntryItemActionPerformed
        String name = JOptionPane.showInputDialog
            (this, "Character name", "New Entry",
             JOptionPane.QUESTION_MESSAGE);
        fixBackingStore();
        if (name != null && name.length() > 0) {
            Avatar avatar = recognizer.updateAvatar
                (0, name, 0, null, null, null, null);
            if (avatarFilter != null && !avatarFilter.accept(avatar)) {
                // hidden by filter
                JOptionPane.showMessageDialog
                    (this, "New entry is hidden, disabling all filters.",
                     "New Entry", JOptionPane.INFORMATION_MESSAGE);
                noFilterItemActionPerformed(evt);
            } else if (avatar.getTimestamp() == 0) {
                // really a new entry, will have to refresh roster
                avatar.setTimestamp(System.currentTimeMillis());
                setRoster(recognizer.getAvatars(), recognizer.getLines());
            }
            // search position in current roster
            Avatar roster[] = model.getRoster();
            int index = Arrays.binarySearch(roster, avatar, model.getOrder());
            if (index < 0) {
                doException(new RuntimeException("new entry not ound in roster"));
            } else {
                rosterTable.setRowSelectionInterval(index, index);
                rosterTable.scrollRectToVisible(rosterTable.getCellRect(index, 0, true));
            }
        }
    }//GEN-LAST:event_newEntryItemActionPerformed

    private void hideMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideMenuItemActionPerformed
        // find the set filter menu entry
        AvatarFilter.Set setFilter = null;
        JMenuItem filterItem = null;
        int scan = filterMenu.getItemCount();
        while (--scan >= 0) {
            javax.swing.JMenuItem item = filterMenu.getItem(scan);
            if (item != null) {
                Object filter = item.getClientProperty(FILTER_PROPERTY);
                if (filter instanceof AvatarFilter.Not) {
                    filter = ((AvatarFilter.Not)filter).getFilter();
                }
                if (filter instanceof AvatarFilter.Set) {
                    setFilter = (AvatarFilter.Set)filter;
                    filterItem = item;
                    if (!item.isSelected()) {
                        setFilter.getSet().clear();
                    }
                    break; // found
                }
            }
        }
        if (setFilter == null) {
            setFilter = new AvatarFilter.Set();
        }
        int rows[] = rosterTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            setFilter.add(model.getAvatar(rows[i]));
        }
        if (rosterTable.isEditing()) {
            rosterTable.getCellEditor().cancelCellEditing();
        }
        if (filterItem != null) {
            filterItem.setSelected(true);
            refreshFilter();
        } else {
            doNewFilter("hide selection", new AvatarFilter.Not(setFilter));
        }
    }//GEN-LAST:event_hideMenuItemActionPerformed

    private void rosterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rosterTableMouseClicked
        if (isPopupTrigger(evt)) {
            // FIXMEFIXMEFACTOR
//            JMenuItem propertyMenuItem = (JMenuItem)contextMenu.getComponent(0);
//            propertyMenuItem.setEnabled(rosterTable.getSelectedRow() >= 0);
            contextMenu.show(rosterTable, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_rosterTableMouseClicked

    private void styleFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styleFileItemActionPerformed
        JFileChooser chooser = getChooser("xsl");
        if (chooser.showOpenDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doNewStyle(chooser.getSelectedFile());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_styleFileItemActionPerformed

    private void filterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterItemActionPerformed
        refreshFilter();
    }//GEN-LAST:event_filterItemActionPerformed

    private void exportHTMLItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportHTMLItemActionPerformed
        if (exportToClipboardItem.isSelected()) {
            selectionButton.setOwner(false);
            RepaintManager.currentManager(this).paintDirtyRegions();
            doExportHTML(null);
            return;
        }
        JFileChooser chooser = getChooser("html");
        if (chooser.showSaveDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doExportHTML(chooser.getSelectedFile());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_exportHTMLItemActionPerformed

    private void exportXMLItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportXMLItemActionPerformed
        JFileChooser chooser = getChooser("xml");
        if (chooser.showSaveDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doExportXML(chooser.getSelectedFile(), false);
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_exportXMLItemActionPerformed

    private void exportTableItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportTableItemActionPerformed
        JFileChooser chooser = getChooser("csv");
        if (chooser.showSaveDialog(this) == chooser.APPROVE_OPTION) {
            fixBackingStore();
            doExport(chooser.getSelectedFile());
        } else {
            fixBackingStore();
        }
    }//GEN-LAST:event_exportTableItemActionPerformed

    private void quitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitItemActionPerformed
        exitForm(null);
    }//GEN-LAST:event_quitItemActionPerformed

    private void parseFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parseFileItemActionPerformed
        JFileChooser chooser = getChooser("txt");
        chooser.setApproveButtonText("Parse");
        chooser.setDialogTitle("Parse files");
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        chooser.setSelectedFile(null);
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    String name = file.getName();
                    if (name.startsWith("eqlog_")) {
                        return name.endsWith(".txt") ||
                            name.endsWith(".txt.gz");
                    } else {
                        return false;
                    }
                }
            }
            public String getDescription() {
                return "Log Files (eqlog_*.txt, eqlog_*.txt.gz)";
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
        if (isChanged() &&
            JOptionPane.showConfirmDialog(this,
            "Some changes are not saved. Really quit?", "Quit YAELP",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)
            != JOptionPane.OK_OPTION) {
            return; // canceled
        }
        if (isAppletFrame()) {
            dispose();
        } else {
            System.exit(0);
        }
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
        HashMap propertyFilters = null;
        scan = filterMenu.getItemCount();
        while (--scan >= 0) {
            javax.swing.JMenuItem item = filterMenu.getItem(scan);
            if (item != null && item.isSelected()) {
                Object filter = item.getClientProperty(FILTER_PROPERTY);
                if (filter instanceof AvatarFilter.Guild) {
                    guildFilter = appendFilterOr(guildFilter, (AvatarFilter.Guild)filter);
                } else if (filter instanceof AvatarFilter.Class) {
                    classFilter = appendFilterOr(classFilter, (AvatarFilter.Class)filter);
                } else if (filter instanceof AvatarFilter.Culture) {
                    cultureFilter = appendFilterOr(cultureFilter, (AvatarFilter.Culture)filter);
                } else if (filter instanceof AvatarFilter.Property) {
                    propertyFilters = appendFilterOr(propertyFilters, (AvatarFilter.Property)filter);
                } else if (filter instanceof AvatarFilter) {
                    avatarFilter = appendFilterAnd(avatarFilter, (AvatarFilter)filter);
                } else {
                    System.err.println("unrecognized filter: "+filter);
                }
            }
        }
        // and-connect the results
        avatarFilter = appendFilterAnd(avatarFilter, guildFilter);
        avatarFilter = appendFilterAnd(avatarFilter, classFilter);
        avatarFilter = appendFilterAnd(avatarFilter, cultureFilter);
        avatarFilter = appendFilterAnd(avatarFilter, propertyFilters);
        setRoster(recognizer.getAvatars(), recognizer.getLines());
    }
    
    /** Invert the selected filters. */    
    protected void invertFilter() {
        int scan;
        scan = filterMenu.getItemCount();
        while (--scan >= 0) {
            javax.swing.JMenuItem item = filterMenu.getItem(scan);
            if (item != null && item.isSelected()) {
                Object filter = item.getClientProperty(FILTER_PROPERTY);
                String name = item.getText();
                if (filter instanceof AvatarFilter.Not) {
                    filter = ((AvatarFilter.Not)filter).getFilter();
                    if (name.startsWith("Not ")) {
                        name = name.substring(4);
                    } else if (name.startsWith("hide ")) {
                        name = "show "+name.substring(5);
                    } else {
                        name = "Not "+name;
                    }
                } else if (filter instanceof AvatarFilter) {
                    filter = new AvatarFilter.Not((AvatarFilter)filter);
                    if (name.startsWith("Not ")) {
                        name = name.substring(4);
                    } else if (name.startsWith("show ")) {
                        name = "hide "+name.substring(5);
                    } else {
                        name = "Not "+name;
                    }
                } else {
                    System.err.println("unrecognized filter: "+filter);
                }
                item.setText(name);
                item.putClientProperty(FILTER_PROPERTY, filter);
            }
        }
        refreshFilter();
    }
    
    /** Initialize editors for enumerated values. */
    protected void initEditors() {
        LineBorder blackBorder = new LineBorder(Color.black);
        Class editingClass[] = {
            Avatar.Class.class,
            Avatar.Culture.class,
            Avatar.Guild.class,
            Avatar.Zone.class,
        };
        
        for (int i = 0; i < editingClass.length; i++) {
            JComboBox selection = new JComboBox();
            selection.setBorder(blackBorder);
            selection.setEditable(true);
            selection.setFont(rosterTable.getFont());
            rosterTable.setDefaultEditor(editingClass[i],
                new DefaultCellEditor(selection));
        }
    }
    
    /** Update the editor for the new instance. */
    protected void updateEditor(Object newInstance) {
        setChanged(true);
        TableCellEditor editor = rosterTable
            .getDefaultEditor(newInstance.getClass());
        java.awt.Component editorComponent = editor
            .getTableCellEditorComponent(rosterTable, newInstance, false, 0, 0);
        if (!(editorComponent instanceof JComboBox)) {
            return;
        }
        DefaultComboBoxModel editorModel = (DefaultComboBoxModel)
            ((JComboBox)editorComponent).getModel();
        if (newInstance instanceof Comparable) {
            Comparable c = (Comparable)newInstance;
            int end = editorModel.getSize();
            int scan = 0;
            while (scan < end && c.compareTo(editorModel.getElementAt(scan)) > 0)
                scan++;
            editorModel.insertElementAt(newInstance, scan);
        } else {
            editorModel.addElement(newInstance);
        }
    }
    
    protected static AvatarFilter appendFilterOr(AvatarFilter list, AvatarFilter filter) {
        if (list == null) {
            return filter;
        } else if (filter == null) {
            return list;
        } else {
            return new AvatarFilter.Or(list, filter);
        }
    }
    
    protected static HashMap appendFilterOr(HashMap map, AvatarFilter.Property filter) {
        if (filter == null) {
            return map;
        } else if (map == null) {
            map = new HashMap();
            map.put(filter.getName(), filter);
        } else {
            String key = filter.getName();
            AvatarFilter list = (AvatarFilter)map.get(key);
            if (list == null) {
                list = filter;
            } else {
                list = new AvatarFilter.Or(list, filter);
            }
            map.put(key, list);
        }
        return map;
    }
    
    protected static AvatarFilter appendFilterAnd(AvatarFilter list, AvatarFilter filter) {
        if (list == null) {
            return filter;
        } else if (filter == null) {
            return list;
        } else {
            return new AvatarFilter.And(list, filter);
        }
    }
    
    protected static AvatarFilter appendFilterAnd(AvatarFilter list, HashMap map) {
        if (map == null) {
            return list;
        } else {
            Iterator i = map.values().iterator();
            while (i.hasNext()) {
                AvatarFilter filter = (AvatarFilter)i.next();
                if (list == null) {
                    list = filter;
                } else {
                    list = new AvatarFilter.And(list, filter);
                }
            }
            return list;
        }
    }
    
    protected void fixBackingStore() {
        // hack to fix dialog backing store bug
//        javax.swing.RepaintManager.currentManager(this)
//            .markCompletelyDirty(getRootPane());
    }
    
    /** Initialize the roster table with a new list of avatars.
     * @param avatars the avatars to be displayed in the roster
     * @param lines lines parsed to create the provided list
     */    
    protected void setRoster(Avatar avatars[], int lines) {
        int hidden = 0;
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
            hidden = i0 - i1;
        }
        
        if (model == null) {
            model = new RosterTableModel(avatars);
            rosterTable.setModel(model);
        } else {
            model.setRoster(avatars);
        }
        status.setText(
            model.getRowCount() + " characters, " +
            (hidden>0 ? hidden + " hidden, " : "") +
            lines + " lines parsed");
    }
    
    protected void doNameOrder() {
        // sort by name
        model.setOrder(new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                return ((Avatar)a).getName()
                    .compareTo(((Avatar)b).getName());
            }
        });
    }
    
    protected void doClassOrder() {
        // sort by class
        model.setOrder(new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                Avatar aa = (Avatar)a, ab = (Avatar)b;
                int delta = stringValue(aa.getClazz())
                    .compareTo(stringValue(ab.getClazz()));
                return delta != 0 ? delta :
                    aa.getName().compareTo(ab.getName());
            }
        });
    }
    
    protected void doCultureOrder() {
        // sort by culture
        model.setOrder(new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                Avatar aa = (Avatar)a, ab = (Avatar)b;
                int delta = stringValue(aa.getCulture())
                    .compareTo(stringValue(ab.getCulture()));
                return delta != 0 ? delta :
                    aa.getName().compareTo(ab.getName());
            }
        });
    }
    
    protected void doGuildOrder() {
        // sort by guild
        model.setOrder(new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                Avatar aa = (Avatar)a, ab = (Avatar)b;
                int delta = stringValue(aa.getGuild())
                    .compareTo(stringValue(ab.getGuild()));
                return delta != 0 ? delta :
                    aa.getName().compareTo(ab.getName());
            }
        });
    }
    
    protected void doZoneOrder() {
        // sort by guild
        model.setOrder(new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                Avatar aa = (Avatar)a, ab = (Avatar)b;
                int delta = stringValue(aa.getZone())
                    .compareTo(stringValue(ab.getZone()));
                return delta != 0 ? delta :
                    aa.getName().compareTo(ab.getName());
            }
        });
    }
    
    protected void doLevelOrder() {
        // sort by level
        model.setOrder(new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                Avatar aa = (Avatar)a, ab = (Avatar)b;
                int delta = aa.getLevel() - ab.getLevel();
                if (delta < 0) {
                    return 1;
                } else if (delta > 0) {
                    return -1;
                } else {
                    return aa.getName().compareTo(ab.getName());
                }
            }
        });
    }
    
    protected void doTimeOrder() {
        // sort by time
        model.setOrder(new java.util.Comparator() {
            public boolean equals(Object other) {
                return other != null &&
                other.getClass() == this.getClass();
            }
            public int compare(Object a, Object b) {
                Avatar aa = (Avatar)a, ab = (Avatar)b;
                long delta = aa.getTimestamp() - ab.getTimestamp();
                if (delta < 0) {
                    return 1;
                } else if (delta > 0) {
                    return -1;
                } else {
                    return aa.getName().compareTo(ab.getName());
                }
            }
        });
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
        } else if (order.equals("guild")) {
            doGuildOrder();
        } else if (order.equals("zone")) {
            doZoneOrder();
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
                recognizer.nextFile();
                EqlogParser.parseFile(files[i], recognizer);
            }
            setRoster(recognizer.getAvatars(), recognizer.getLines());
        } catch (Exception ex) {
            ex.printStackTrace();
            doException(ex);
        }
    }
    
    /** Import an XML file.
     * @param file file to import
     * @param clear whether to start with a new recognizer
     */    
    protected void doImport(File file, boolean clear) {
        try {
            
            Recognizer r = clear ? new Recognizer() : this.recognizer;
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            try {
                try {
                    in.mark(20); // actually need only 4
                    in = new GZIPInputStream(in);
                } catch (IOException ex) {
                    // apparently no compressed
                    in.reset();
                }
                r.nextFile();
                EqXMLParser.parseFile(in, r);
            } finally {
                in.close();
            }
            if (clear) {
                this.recognizer = r;
                setChanged(false);
            }
            setRoster(r.getAvatars(), r.getLines());
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
            doShowDocument(title, new JEditorPane(text));
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            doException(ex);
        }
    }
    
    protected void doShowDocument(String title, String type, String text) {
        doShowDocument(title, new JEditorPane(type, text));
    }
    
    protected void doShowDocument(String title, JEditorPane editorPane) {
        editorPane.setEditable(false);
        javax.swing.JScrollPane scrollPane =
            new javax.swing.JScrollPane(editorPane);
        scrollPane.setPreferredSize(new java.awt.Dimension(550, 250));
        javax.swing.JOptionPane.showMessageDialog
            (this, scrollPane, title, javax.swing.JOptionPane.INFORMATION_MESSAGE);
        fixBackingStore();
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
            menuItem.addActionListener(styleSelection);
            styleButtonGroup.add(menuItem);
            styleMenu.insert(menuItem, styleMenu.getItemCount()-1);
            menuItem.setSelected(true);
            styleSelection.setSelectedStyle(styleURL);
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
    protected void doExportXML(java.io.File file, boolean compress) {
        if (!doWriteConfirmation(file)) {
            return; // nothing selected, canceled.
        }
        try {
            Writer out;
            if (compress) {
                out = new OutputStreamWriter(new GZIPOutputStream
                    (new FileOutputStream(file)));
            } else {
                out = new FileWriter(file);
            }
            try {
                writeXML(out);
            } finally {
                out.close();
            }
            if (compress) {
                // hack assume we saved now
                setChanged(false);
            }
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
        if (!exportToClipboardItem.isSelected() &&
            !doWriteConfirmation(file)) {
            return; // nothing selected, canceled.
        }
        try {
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
            java.net.URL style = styleSelection.getSelectedStyle();
            if (loadedStyle == null || !loadedStyle.equals(style) ||
                style.getProtocol().equals("file")) {
//                java.net.URLConnection connection = style.openConnection();
//                connection.setUseCaches(false);
//                java.io.InputStream xsl = connection.getInputStream();
                try {
//                    processor.loadStylesheet(new InputSource(xsl));
                    processor.loadStylesheet(new InputSource(style.toExternalForm()));
                } finally {
//                    xsl.close();
                }
                loadedStyle = style;
            }
            Destination out;
            if (exportToClipboardItem.isSelected()) {
                out = new StringDestination();
            } else {
                out = new FileDestination(file);
            }
            handler.setDestination(out);
            final java.io.PipedWriter pw = new java.io.PipedWriter();
            new Thread() {
                public void run() {
                    try {
                        writeXML(pw);
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        try { pw.close(); }
                        catch (IOException ex) {} // ignored
                    }
                }
            }.start();
            Thread.yield();
            processor.parse(new InputSource(new java.io.PipedReader(pw)));
            
            if (exportToClipboardItem.isSelected()) {
                selectionButton.setSelection(out.toString());
            }
            
            if (displayHTMLItem.isSelected()) {
                if (exportToClipboardItem.isSelected()) {
                    doShowDocument("HTML Export Result", "text/html", out.toString());
                } else {
                    doShowDocument("HTML Export Result",
                        new java.net.URL("file:///"+file.getAbsolutePath()));
                }
            }
        } catch (Exception ex) {
            processor = null; // reset processor
            ex.printStackTrace();
            doException(ex);
        }
    }
    
    protected void writeXML(java.io.Writer out) throws java.io.IOException {
        java.io.PrintWriter pw = new java.io.PrintWriter(out, false);
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
            Avatar avatar = model.getAvatar(j);
            pw.print(" <avatar time=\"");
            pw.print(avatar.getTimestamp());
            pw.println("\">");
            for (int i = 0; i < cols; i++) {
                pw.print("  <");
                pw.print(tags[i]);
                if (tags[i].equals("guild")) {
                    pw.print(" time=\"");
                    pw.print(avatar.getGuildTimestamp());
                    pw.print('"');
                }
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
            Iterator i = avatar.getProperties();
            if (i != null) {
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    Avatar.TimestampEntry value =
                        (Avatar.TimestampEntry)entry.getValue();
                    pw.print("  <property name=\"");
                    pw.print(entry.getKey());
                    pw.print("\" value=\"");
                    pw.print(quote(value.toString(), "&<>\""));
                    pw.print("\" time=\"");
                    pw.print(value.timestamp);
                    pw.println("\"/>");
                }
            }
//            pw.print("  <history>");
//            pw.print(quote(avatar.getHistory(), "&<>"));
//            pw.println("</history>");
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
        String message = ex.getLocalizedMessage();
        if (message == null) {
            message = ex.toString();
        }
        if (ex instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException)ex;
            message += "(" + spe.getSystemId()
                + " line " + spe.getLineNumber() + ")";
        }
        javax.swing.JOptionPane.showMessageDialog
        (RosterFrame.this, message, "Exception", javax.swing.JOptionPane.ERROR_MESSAGE);
        fixBackingStore();
    }
    
    /** Fix for the broken MouseEvent.isPopupTrigger(). */
    public static boolean isPopupTrigger(java.awt.event.MouseEvent evt) {
        return evt.isPopupTrigger() ||
            (evt.getModifiers() & evt.BUTTON3_MASK) > 0;
    }
    
    protected javax.swing.JFileChooser getChooser(String category) {
        if (chooserMap == null) {
            chooserMap = new HashMap();
        }
        JFileChooser chooser = (JFileChooser)chooserMap.get(category);
        if (chooser == null) {
            chooser = new javax.swing.JFileChooser();
            chooserMap.put(category, chooser);
            if (category.equals("txt")) {
                File installation = Tricks.findInstallation();
                if (installation != null) {
                    chooser.setCurrentDirectory(installation);
                }
            } else if (lastChooser != null) {
                chooser.setCurrentDirectory(((JFileChooser)chooserMap
                    .get(lastChooser)).getCurrentDirectory());
            }
            if (category.equals("yxr")) {
                chooser.setFileSelectionMode(chooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setSelectedFile(null);
                chooser.resetChoosableFileFilters();
                chooser.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".yxr");
                    }
                    public String getDescription() {
                        return "YAELP Files (*.yxr)";
                    }
                });
            } else if (category.equals("html")) {
                chooser.setApproveButtonText("Export");
                chooser.setDialogTitle("Export HTML");
                chooser.setFileSelectionMode(chooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setSelectedFile(null);
                chooser.resetChoosableFileFilters();
                chooser.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".html");
                    }
                    public String getDescription() {
                        return "HTML Files (*.html)";
                    }
                });
            } else if (category.equals("csv")) {
                chooser.setApproveButtonText("Export");
                chooser.setDialogTitle("Export table");
                chooser.setFileSelectionMode(chooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setSelectedFile(null);
                chooser.resetChoosableFileFilters();
                chooser.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory() ||
                            file.getName().endsWith(".csv") ||
                            file.getName().endsWith(".txt");
                    }
                    public String getDescription() {
                        return "Table Files (*.csv, *.txt)";
                    }
                });
            } else if (category.equals("xml")) {
                chooser.setApproveButtonText("Export");
                chooser.setDialogTitle("Export XML");
                chooser.setFileSelectionMode(chooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setSelectedFile(null);
                chooser.resetChoosableFileFilters();
                chooser.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".xml");
                    }
                    public String getDescription() {
                        return "XML Files (*.xml)";
                    }
                });
            } else if (category.equals("xsl")) {
                chooser.setApproveButtonText("Use Style");
                chooser.setDialogTitle("Use New Style");
                chooser.setFileSelectionMode(chooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setSelectedFile(null);
                chooser.resetChoosableFileFilters();
                chooser.addChoosableFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory() ||
                            file.getName().endsWith(".xsl");
                    }
                    public String getDescription() {
                        return "XSL Style Files (*.xsl)";
                    }
                });
            }
        }
        lastChooser = category;
        return chooser;
    }
    
    public URL getResource(String name) {
        return RosterFrame.class.getResource(name);
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
            String version = java.util.ResourceBundle.getBundle("net/sourceforge/fraglets/yaelp/version").getString("version");
            aboutText =
            "YAELP log file parser, version "+version+".\n"+
            "Copyright � 2001, 2002 Klaus Rennecke.\n"+
            "XML parser Copyright � 1997, 1998 James Clark.\n"+
            "XSL transformation Copyright � 1998, 1999 James Clark.\n"+
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
        return quote(str, "&<>");
    }
    
    /** Quote a string, ready to use in a #PCDATA section of XML
     * @param str the string to quote
     * @param delim the delimiters to quote in the string
     * @return the quoted string
     */
    public static String quote(String str, String delim) {
        StringBuffer buffer = new StringBuffer();
        java.util.StringTokenizer tok = new java.util.StringTokenizer(str, delim, true);
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
                    case '"':
                        buffer.append("&quot;");
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
        try {
            javax.swing.UIManager.setLookAndFeel
                (javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // oh well ...
        }
        RosterFrame frame = new RosterFrame();
        System.setOut(new java.io.PrintStream
        (new DocumentStream(frame.status)));
        frame.show();
        for (int i = 0; i < args.length; i++) {
            frame.doImport(new File(args[i]), i == 0);
        }
        
    }
    
    /** Getter for property changed.
     * @return Value of property changed.
     */
    public boolean isChanged() {
        return this.changed ||
            (model != null && model.isChanged()) ||
            (recognizer != null && recognizer.isChanged());
    }    
    
    /** Setter for property changed.
     * @param changed New value of property changed.
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
        if (!changed) {
            if (recognizer != null) {
                recognizer.setChanged(false);
            }
            if (model != null) {
                model.setChanged(false);
            }
        }
    }
    
    /** Getter for property appletFrame.
     * @return Value of property appletFrame.
     */
    public boolean isAppletFrame() {
        return this.appletFrame;
    }
    
    /** Setter for property appletFrame.
     * @param appletFrame New value of property appletFrame.
     */
    public void setAppletFrame(boolean appletFrame) {
        this.appletFrame = appletFrame;
    }
    
    public void appendFilter(String name, AvatarFilter filter) {
        doNewFilter(name, filter);
    }
    
    public String getApplicationName() {
        return "YAELP log file parser";
    }
    
    public Object getCurrentSelection() {
        int row = rosterTable.getSelectedRow();
        if (row < 0) {
            return null; // nothing selected
        }
        return model.getAvatar(row);
    }
    
    public String getResourceString(String key) {
        return Defaults.getString(key);
    }
    
    public String getVersion() {
        return java.util.ResourceBundle
            .getBundle("net/sourceforge/fraglets/yaelp/version")
            .getString("version");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup styleButtonGroup;
    private javax.swing.JCheckBoxMenuItem level1FilterItem;
    private javax.swing.JMenuItem noFilterItem;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JMenu newStyleMenu;
    private javax.swing.JMenuItem dingMenuItem;
    private javax.swing.JMenuItem exportTableItem;
    private javax.swing.JMenuItem invertFilterItem;
    private javax.swing.JCheckBoxMenuItem officerFilterItem;
    private javax.swing.JSeparator separator4;
    private javax.swing.JMenuItem loadFileItem;
    private net.sourceforge.fraglets.yaelp.bean.PropertyInput propertyInput;
    private net.sourceforge.fraglets.yaelp.bean.PropertyEditor propertyEditor;
    private javax.swing.JMenu styleMenu;
    private net.sourceforge.fraglets.yaelp.bean.SelectionButton selectionButton;
    private javax.swing.JSeparator separator6;
    private javax.swing.JPopupMenu contextMenu;
    private javax.swing.JRadioButtonMenuItem mwStyleItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTable rosterTable;
    private javax.swing.JMenuItem hideMenuItem;
    private javax.swing.JMenuItem quitItem;
    private javax.swing.JSeparator separator3;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JRadioButtonMenuItem neopStyleItem;
    private javax.swing.JSeparator separator2;
    private javax.swing.JSeparator separator1;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JCheckBoxMenuItem alliedFilterItem;
    private javax.swing.JMenuItem dohMenuItem;
    private javax.swing.JCheckBoxMenuItem defaultFilterItem;
    private javax.swing.JCheckBoxMenuItem mainFilterItem;
    private javax.swing.JCheckBoxMenuItem memberFilterItem;
    private javax.swing.JMenuItem styleWizardItem;
    private javax.swing.JSeparator separator5;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JRadioButtonMenuItem ssStyleItem;
    private javax.swing.JMenuItem styleFileItem;
    private javax.swing.JMenuItem exportXMLItem;
    private javax.swing.JCheckBoxMenuItem displayHTMLItem;
    private javax.swing.JMenuItem exportHTMLItem;
    private javax.swing.JMenuItem importFileItem;
    private javax.swing.JTextField status;
    private javax.swing.JMenuItem saveFileItem;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButtonMenuItem defaultStyleItem;
    private javax.swing.JRadioButtonMenuItem vlStyleItem;
    private javax.swing.JMenuItem parseFileItem;
    private javax.swing.JMenu filterMenu;
    private javax.swing.JCheckBoxMenuItem exportToClipboardItem;
    private javax.swing.JMenuItem newEntryItem;
    private javax.swing.JCheckBoxMenuItem friendFilterItem;
    // End of variables declaration//GEN-END:variables

    /** Holds value of property changed. */
    private boolean changed;    

    /** Holds value of property appletFrame. */
    private boolean appletFrame;
    
}
