/*
 * DocumentStream.java
 * Copyright (C) 2001 Klaus Rennecke, all rights reserved.
 * Created on 1. Mai 2001, 18:55
 */

package net.sourceforge.fraglets.yaelp;

/** This class implements an OutputStream which prints to a JTextComponent's
 * Document.
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
 * @author Klaus Rennecke
 * @version $Revision: 1.4 $
 */
public class DocumentStream extends java.io.OutputStream {

    /** The target document to write to. */    
    protected javax.swing.text.Document target;
    
    /** The component associated with the document, if any. */    
    protected javax.swing.text.JTextComponent component;
    
    /** Clear the document on next output. */    
    protected boolean clear = true;
    
    /** The system encoding. */
    protected String encoding =
        new java.io.OutputStreamWriter(System.out).getEncoding(); //HACK
    
    /** Creates new DocumentStream
     * @param target the target document to write to
     */
    public DocumentStream(javax.swing.text.Document target) {
        this.target = target;
    }

    /** Creates new DocumentStream
     * @param target the target component whose document to write to
     */
    public DocumentStream(javax.swing.text.JTextComponent target) {
        this.target = target.getDocument();
        this.component = target;
    }

    /** OutputStream protocol implementation.
     * @param c the character to write
     */
    public void write(int c) {
        try {
            switch(c) {
                case '\n':
                case '\r':
                    clear = true;
                    if (component != null) {
                        javax.swing.RepaintManager.currentManager(component)
                            .paintDirtyRegions();
                    }
                    break;
                default:
                    if (clear) {
                        target.remove(0, target.getLength());
                        clear = false;
                    }
                    target.insertString(target.getLength(),
                                        String.valueOf((char)c), null);
                    break;
            }
        } catch (javax.swing.text.BadLocationException ex) {
            throw new RuntimeException(ex.toString());
        }
    }
    
    /** OutputStream protocol implementation. */
    public void close() {
        target = null;
    }
    
    /** OutputStream protocol implementation.
     * @param values bytes to write
     * @param off offset into values
     * @param len portion of values
     * @throws IOException on error
     */
    public void write(byte[] values, int off, int len) throws java.io.IOException {
        try {
            if (clear) {
                target.remove(0, target.getLength());
                clear = false;
            }
            int scan = off + len;
            while (--scan >= off) {
                switch (values[scan] & 0xff) {
                    case '\n':
                    case '\r':
                        clear = true;
                        continue;
                }
                break;
            }
            int mark = ++scan;
            while (--scan >= off) {
                switch (values[scan] & 0xff) {
                    case '\n':
                    case '\r':
                        break;
                    default:
                        continue;
                }
                break;
            }
            if (++scan < mark) {
                target.insertString(target.getLength(),
                    new String(values, scan, mark-scan, encoding), null);
            }
            if (clear && component != null) {
                javax.swing.RepaintManager.currentManager(component)
                    .paintDirtyRegions();
            }
        } catch (javax.swing.text.BadLocationException ex) {
            throw new RuntimeException(ex.toString());
        }
    }
}
