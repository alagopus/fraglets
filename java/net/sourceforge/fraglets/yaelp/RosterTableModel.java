/*
 * RosterTableModel.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

import java.util.Comparator;

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
 * @version $Revision: 1.5 $
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
    /** Inverting order. */
    protected Inverter inverter;
    /** Inverting comparator. */
    public static class Inverter implements Comparator {
        private Comparator order;
        public Inverter(Comparator order) {
            this.order = order;
        }
        public boolean equals(Object other) {
            return other.getClass() == getClass();
        }
        public int compare(Object o1, Object o2) {
            return order.compare(o1, o2) * -1;
        }
        public Comparator getOrder() {
            return order;
        }
        public void setOrder(Comparator order) {
            this.order = order;
        }
        public Comparator invert(Comparator order) {
            if (order == this) {
                return getOrder();
            } else {
                setOrder(order);
                return this;
            }
        }
    }
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
    
    public Avatar getAvatar(int row) {
        return roster[row];
    }
    
    public Avatar[] getRoster() {
        return roster;
    }
    
    public void setRoster(Avatar roster[]) {
        this.roster = roster;
        if (order != null) {
            java.util.Arrays.sort(roster, order);
        }
        fireTableChanged(new javax.swing.event.TableModelEvent(this));
    }
    
    public Comparator getOrder() {
        return order;
    }
    
    public void setOrder(Comparator order) {
        if (order == null) {
            this.order = order;
        } else if (order.equals(this.order)) {
            invertOrder();
        } else {
            Comparator oldOrder = this.order;
            this.order = order;
            if (roster != null) {
                java.util.Arrays.sort(roster, order);
                fireTableChanged(new javax.swing.event.TableModelEvent(this));
            }
        }
    }
    
    public void invertOrder() {
        if (order == null) {
            return; // no order to invert
        } else if (inverter == null) {
            inverter = new Inverter(order);
            order = inverter;
        } else {
            order = inverter.invert(order);
        }
        if (roster != null) {
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
    
    public boolean isCellEditable(int row, int col) {
        switch(col) {
            case 1: case 2: case 3: case 4: case 5:
                return true;
            default:
                return false;
        }
    }
    
    public Class getColumnClass(int col) {
        switch(col) {
            case 0: return String.class;
            case 1: return Avatar.Culture.class;
            case 2: return Avatar.Class.class;
            case 3: return Integer.class;
            case 4: return Avatar.Guild.class;
            case 5: return Avatar.Zone.class;
            default: return Object.class;
        }
    }
    
    public void setValueAt(Object obj, int row, int col) {
        Avatar avatar = getAvatar(row);
        switch(col) {
            case 1:
                if (obj instanceof Avatar.Culture || obj == null) {
                    avatar.setCulture((Avatar.Culture)obj);
                } else {
                    avatar.setCulture(Avatar.Culture.create(obj.toString()));
                }
                break;
            case 2:
                if (obj instanceof Avatar.Class || obj == null) {
                    avatar.setClazz((Avatar.Class)obj);
                } else {
                    avatar.setClazz(Avatar.Class.create(obj.toString()));
                }
                break;
            case 3:
                if (obj instanceof Integer) {
                    avatar.setLevel(((Integer)obj).intValue());
                } else if (obj != null) {
                    avatar.setLevel(Integer.parseInt(obj.toString()));
                } else {
                    avatar.setLevel(0);
                }
                break;
            case 4: {
                long timestamp = System.currentTimeMillis();
                if (obj instanceof Avatar.Guild || obj == null) {
                    avatar.setGuild((Avatar.Guild)obj, timestamp);
                } else {
                    avatar.setGuild(Avatar.Guild.create(obj.toString()), timestamp);
                }
                break;
            }
            case 5:
                if (obj instanceof Avatar.Zone || obj == null) {
                    avatar.setZone((Avatar.Zone)obj);
                } else {
                    avatar.setZone(Avatar.Zone.create(obj.toString()));
                }
                break;
            default:
                throw new IllegalArgumentException("value not editable");
        }
    }
    
}
