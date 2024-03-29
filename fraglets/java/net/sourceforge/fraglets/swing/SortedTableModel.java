/*
 * SortedTableModel.java
 * Copyright (C) 2002 Klaus Rennecke.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.sourceforge.fraglets.swing;

import javax.swing.table.TableModel;
import java.util.Comparator;

/**
 * Interface for a sorted table model.
 * @author  marion@users.sourceforge.net
 */
public interface SortedTableModel extends TableModel {
    /**
     * Sort the column at index <var>column</var>, using the default
     * comparator. Sorting order is ascending when <var>ascending</var>
     * is true, else descending.
     * @param column the column index to sort
     * @param ascending whether to sort ascending
     */    
    public void sortColumn(int column, boolean ascending);
    
    /** Sort the column at index <var>column</var>, using the given
     * <var>comparator</var>. Sorting order is ascending when
     * <var>ascending</var> is true, else descending.
     * @param column the column index to sort
     * @param ascending whether to sort ascending 
     * @param comparator the comparator to use for sorting
     */    
    public void sortColumn(int column, boolean ascending, Comparator comparator);
}
