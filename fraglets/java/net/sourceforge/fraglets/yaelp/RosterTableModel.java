/*
 * RosterTableModel.java
 *
 * Created on 18. Juni 2001, 03:05
 */

package de.rennecke.yaelp;

import java.util.Comparator;

/**
 *
 * @author  Klaus Rennecke
 * @version 
 */
public class RosterTableModel extends javax.swing.table.AbstractTableModel {
    /** Headers for the roster table. */    
    public static final String headers[] = {
        "Name", "Culture", "Class", "Level", "Guild", "Zone", "Time"
    };
    /** The table data. */
    protected Avatar roster[];
    /** Table order. */
    protected Comparator order;
    /** Creates new RosterTableModel */
    public RosterTableModel() {
    }
    /** Creates new RosterTableModel */
    public RosterTableModel(Avatar roster[]) {
        this.roster = roster;
    }

    public Object getValueAt(int row, int col) {
        switch(col) {
            case 0: return roster[row].getName();
            case 1: return roster[row].getCulture();
            case 2: return roster[row].getClazz();
            case 3: return String.valueOf(roster[row].getLevel());
            case 4: return roster[row].getGuild();
            case 5: return roster[row].getZone();
            case 6: return new java.sql.Date(roster[row].getTimestamp());
            default:
                throw new IndexOutOfBoundsException("invalid column: "+col);
        }
    }
    
    public void setRoster(Avatar roster[]) {
        this.roster = roster;
        if (order != null) {
            java.util.Arrays.sort(roster, order);
        }
        fireTableChanged(new javax.swing.event.TableModelEvent(this));
    }
    
    public void setOrder(Comparator order) {
        Comparator oldOrder = this.order;
        this.order = order;
        if (order != null && order != oldOrder && !order.equals(oldOrder) &&
            roster != null) {
            java.util.Arrays.sort(roster, order);
            fireTableChanged(new javax.swing.event.TableModelEvent(this));
        }
    }
    
    public int getRowCount() {
        return roster != null ? roster.length : 0;
    }
    
    public String getColumnName(int index) {
        return headers[index];
    }
    
    public int getColumnCount() {
        return headers.length;
    }
    
}
