/*
 * SelectionButton.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on July 15, 2002, 3:39 AM
 */

package net.sourceforge.fraglets.yaelp;

import javax.swing.JButton;
import javax.swing.Icon;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.io.Reader;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.datatransfer.StringSelection;
import java.io.StringWriter;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * A button holding a string selection and providing visual feedback
 * for ownership status.
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
 */
public class SelectionButton extends JButton implements ActionListener, ClipboardOwner {
    
    /** Holds value of property owner. */
    private boolean owner;
    
    /** Creates a new instance of SelectionButton */
    public SelectionButton(String text, Icon icon) {
        super(text, icon);
        initSelectionButton();
    }
    /** Creates a new instance of SelectionButton */
    public SelectionButton(String text) {
        super(text);
        initSelectionButton();
    }
    /** Creates a new instance of SelectionButton */
    public SelectionButton(Icon icon) {
        super(icon);
        initSelectionButton();
    }
    /** Creates a new instance of SelectionButton */
    public SelectionButton() {
        initSelectionButton();
    }
    
    public void setSelection(String selection) {
        setSelection(selection == null ? null : new StringSelection(selection));
    }
    
    public void setSelection(Transferable selection) {
        try {
            getToolkit().getSystemClipboard().setContents(selection, this);
            setOwner(true);
        } catch (Exception ex) {
            showErrorMessage(ex);
        }
    }
    
    protected void initSelectionButton() {
        addActionListener(this);
        setOwner(false);
        setFocusPainted(false);
        setRequestFocusEnabled(false);
    }
    
    public void actionPerformed(ActionEvent ev) {
        try {
            Transferable transferable = getToolkit()
                .getSystemClipboard().getContents(this);
            Reader input = DataFlavor.stringFlavor
                .getReaderForText(transferable);
            StringWriter output = new StringWriter();
            try {
                char buffer[] = new char[1024];
                int n;
                while ((n = input.read(buffer)) > 0) {
                    output.write(buffer, 0, n);
                }
            } finally {
                input.close();
                output.close();
            }
            JTextArea message = new JTextArea(output.toString());
            JScrollPane scrollPane = new javax.swing.JScrollPane(message);
            scrollPane.setPreferredSize(new java.awt.Dimension(550, 250));
            JOptionPane.showMessageDialog(getRootPane(), scrollPane,
                "Clipboard Contents", JOptionPane.PLAIN_MESSAGE);
            // hack to fix dialog backing store bug
            javax.swing.RepaintManager.currentManager(this)
                .markCompletelyDirty(getRootPane());
        } catch (Exception ex) {
            showErrorMessage(ex);
        }
    }
    
    protected void showErrorMessage(Object failure) {
        if (failure instanceof Throwable) {
            String message = ((Throwable)failure).getMessage();
            if (message != null && message.length() > 0) {
                failure = message;
            }
        }
        JOptionPane.showMessageDialog(getRootPane(), failure,
            "Error", JOptionPane.ERROR_MESSAGE);
        // hack to fix dialog backing store bug
        javax.swing.RepaintManager.currentManager(this)
            .markCompletelyDirty(getRootPane());
    }
    
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
        setOwner(false);
    }
    
    /** Getter for property owner.
     * @return Value of property owner.
     */
    public boolean isOwner() {
        return this.owner;
    }
    
    /** Setter for property owner.
     * @param owner New value of property owner.
     */
    protected void setOwner(boolean owner) {
        this.owner = owner;
        setBackground(owner ?
            UIManager.getColor("activeCaption") :
            UIManager.getColor("Button.background"));
        setForeground(owner ?
            UIManager.getColor("activeCaptionText") :
            UIManager.getColor("Button.foreground"));
    }
    
}
