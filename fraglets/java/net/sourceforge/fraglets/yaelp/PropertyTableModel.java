/*
 * PropertyTableModel.java
 * Copyright (C) 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Map;

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
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class PropertyTableModel extends javax.swing.table.AbstractTableModel implements PropertyChangeListener {
    /** The avatar instance we'return representing. */
    protected Avatar avatar;
    /** The property names from the avatar. */
    protected String names[];
    /** Creates a new instance of PropertyTableModel */
    public PropertyTableModel(Avatar avatar) {
        setAvatar(avatar);
    }
    public PropertyTableModel() {
    }
    
    public Avatar getAvatar() {
        return this.avatar;
    }
    
    public void setAvatar(Avatar avatar) {
        if (this.avatar != null) {
            this.avatar.CHANGE.removePropertyChangeListener(this);
        }
        this.avatar = avatar;
        this.names = null;
        if (this.avatar != null) {
            this.avatar.CHANGE.addPropertyChangeListener(this);
        }
        fireTableDataChanged();
    }
    
    public int getColumnCount() {
        return 3;
    }
    
    public int getRowCount() {
        return avatar == null ? 0 :
            avatar.getPropertyCount();
    }
    
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                return getName(row);
            case 1:
                return avatar.getProperty(getName(row));
            case 2:
                return new java.sql.Timestamp(avatar.getTimestamp(getName(row)));
            default:
                throw new IllegalArgumentException("invalid column: "+col);
        }
    }
    
    public String getColumnName(int col) {
        switch (col) {
            case 0: return "Name";
            case 1: return "Value";
            case 2: return "Timestamp";
            default:
                throw new IllegalArgumentException("invalid column: "+col);
        }
    }
    
    public boolean isCellEditable(int row, int col) {
        return col == 1 && (row >= 0 && row < getRowCount());
    }
    
    public void setValueAt(Object value, int row, int col) {
        switch (col) {
            case 1:
                avatar.setProperty(getName(row), value.toString(),
                    System.currentTimeMillis());
                break;
            default:
                throw new IllegalArgumentException
                    ("column not editable: "+col);
        }
    }
    
    public String getName(int row) {
        if (names == null) {
            names = new String[getRowCount()];
            Iterator i = avatar.getProperties();
            int scan = 0;
            while (i.hasNext()) {
                names[scan++] = ((Map.Entry)i.next()).getKey().toString();
            }
            if (scan != names.length) {
                throw new IllegalStateException
                    ("missing properties: "+(names.length-scan));
            } else {
                Arrays.sort(names);
            }
        }
        return names[row];
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
        if (ev.getOldValue() == avatar &&
            "Avatar.property".equals(ev.getPropertyName())) {
            this.names = null; // reset names
            fireTableDataChanged();
        }
    }
    
}
