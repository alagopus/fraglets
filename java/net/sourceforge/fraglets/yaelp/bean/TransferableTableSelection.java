/*
 * TransferableTableSelection.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on July 19, 2002, 5:02 AM
 */

package net.sourceforge.fraglets.yaelp.bean;

import javax.swing.table.TableModel;
import javax.swing.ListSelectionModel;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Comparator;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import com.jclark.xml.output.UTF8XMLWriter;
import java.net.URL;
import org.xml.sax.SAXException;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureEvent;
import javax.swing.JTable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DnDConstants;


/**
 * A Transferable initialized from a table selection, used to implement
 * drag-and-drop on tables.
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
 * @version $Revision: 1.1 $
 */
public class TransferableTableSelection implements Transferable {
    public static final Comparator flavorComparator = new FlavorComparator();
    
    public static final DataFlavor textXMLFlavor;
    
    public static final DataFlavor textHTMLFlavor;
    
    public static final DataFlavor textCSVFlavor;
    
    public static final DataFlavor textPlainFlavor;
    
    protected static final DataFlavor defaultFlavors[];
    
    static
    {
        try {
            textXMLFlavor = new DataFlavor("text/xml;quality=1.0;charset=UTF-8");
            textHTMLFlavor = new DataFlavor("text/html;quality=0.8;charset=UTF-8");
            textCSVFlavor = new DataFlavor("text/csv;quality=0.5;charset=UTF-8");
            textPlainFlavor = new DataFlavor("text/plain;quality=0.5;charset=UTF-8");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex.toString());
        }
    
        DataFlavor flavors[] = new DataFlavor[] {
            textXMLFlavor,
            textHTMLFlavor,
            textCSVFlavor,
            textPlainFlavor,
        };
        Arrays.sort(flavors, flavorComparator);
        defaultFlavors = flavors;
    }
    
    /** Holds value of property data. */
    private byte[] data;
    
    /** Creates a new instance of TransferableTableSelection */
    public TransferableTableSelection(TableModel model, ListSelectionModel selection) {
        this.data = createSelectionData(model, selection);
    }
    
    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        System.err.println("getTransferData flavor="+dataFlavor);
        if (textXMLFlavor.equals(dataFlavor)) {
            return openInputStream();
        } else if (textCSVFlavor.equals(dataFlavor) || textPlainFlavor.equals(dataFlavor)) {
            return transform(openInputStream(), getStyle("selection_csv.xsl"), dataFlavor.getParameter("charset"));
        } else if (textHTMLFlavor.equals(dataFlavor)) {
            return transform(openInputStream(), getStyle("selection_html.xsl"), dataFlavor.getParameter("charset"));
        } else {
            throw new UnsupportedFlavorException(dataFlavor);
        }
    }
    
    public static DragGestureListener createTableGestureListener(JTable table) {
        return new TableGestureListener(table);
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return defaultFlavors;
    }
    
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        System.err.println("isDataFlavorSupported flavor: "+dataFlavor);
        return Arrays.binarySearch(getTransferDataFlavors(), dataFlavor, flavorComparator) >= 0;
    }
    
    /** Getter for property data.
     * @return Value of property data.
     */
    public byte[] getData() {
        return this.data;
    }
    
    /**
     * Open an input stream from the selection data.
     * @return the input stream */    
    public InputStream openInputStream() {
        return new ByteArrayInputStream(getData());
    }
    
    public static byte[] createSelectionData(TableModel model, ListSelectionModel selection) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            UTF8XMLWriter writer = new UTF8XMLWriter(buffer, UTF8XMLWriter.MINIMIZE_EMPTY_ELEMENTS);
            writer.processingInstruction("xml", "version=\"1.0\" encoding=\"UTF-8\"");
            int rows = selection.getMaxSelectionIndex();
            int cols = model.getColumnCount();
            writer.startElement("selection");
            for (int row = selection.getMinSelectionIndex(); row <= rows; row++) {
                if (!selection.isSelectedIndex(row)) {
                    continue;
                }
                writer.startElement("row");
                for (int col = 0; col < cols; col++) {
                    writer.startElement("col");
                    Object value = model.getValueAt(row, col);
                    if (value != null) {
                        writer.write(value.toString());
                    }
                    writer.endElement("col");
                }
                writer.endElement("row");
            }
            writer.endElement("selection");
            writer.close();
            return buffer.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.toString());
        }
    }
    
    public static URL getStyle(String resourceName) {
        return TransferableTableSelection.class.getResource(resourceName);
    }
    
    public static InputStream transform(InputStream xml, URL style, String encoding) throws IOException {
        try {
            System.err.println("transform with encoding: "+encoding);
            return new TransformationPipe(encoding)
                .loadStylesheet(style)
                .transform(xml);
        } catch (SAXException ex) {
            ex.printStackTrace();
            throw new IOException(ex.toString()); // cheap FIXME
        }
    }
    
    public static class FlavorComparator implements Comparator {
        
        /**
         * Compare two objects of type DataFlavor, by mime type.
         * @param obj0 first object
         * @param obj1 second object
         * @return 0 if obj0 compares equal to obj1, less than 0 if
         *         obj0 compares less to obj1, greater than 0 if
         *         obj0 compares greater to obj1. */
        public int compare(Object obj0, Object obj1) {
            return compare((DataFlavor)obj0, (DataFlavor)obj1);
        }
        
        /**
         * Compare two objects of type DataFlavor, by mime type.
         * @param flavor0 first data flavor
         * @param flavor1 second data flavor
         * @return 0 if flavor0 compares equal to flavor1, less than 0 if
         *         flavor0 compares less to flavor1, greater than 0 if
         *         flavor0 compares greater to flavor1. */
        public int compare(DataFlavor flavor0, DataFlavor flavor1) {
            if (flavor0 == flavor1) {
                return 0;
            } else if (flavor0 == null) {
                return -1;
            } else if (flavor1 == null) {
                return 1;
            } else {
                return flavor0.getMimeType().compareTo(flavor1.getMimeType());
            }
        }
    }
    
    public static class TableGestureListener implements DragGestureListener {
        
        /** Holds value of property table. */
        private javax.swing.JTable table;
        
        public TableGestureListener(JTable table) {
            setTable(table);
        }
        
        public void dragGestureRecognized(DragGestureEvent ev) {
            if (ev.getComponent() == getTable()) {
                ev.startDrag(null, new TransferableTableSelection
                    (table.getModel(), table.getSelectionModel()), null);
            }
        }
        
        /** Getter for property table.
         * @return Value of property table.
         */
        public javax.swing.JTable getTable() {
            return this.table;
        }
        
        /** Setter for property table.
         * @param table New value of property table.
         */
        public void setTable(javax.swing.JTable table) {
            this.table = table;
            if (this.table != null) {
                DragSource.getDefaultDragSource()
                    .createDefaultDragGestureRecognizer(this.table,
                    DnDConstants.ACTION_COPY, this);
            }
        }
        
    }
}
