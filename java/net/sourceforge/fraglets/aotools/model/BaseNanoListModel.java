/*
 * BaseNanoListModel.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 3. August 2001, 18:31
 */

package net.sourceforge.fraglets.aotools.model;

/**
 *
 * @author  sas
 * @version 
 */
public class BaseNanoListModel extends javax.swing.AbstractListModel {

    /** Creates new BaseNanoListModel */
    public BaseNanoListModel() {
    }
    public int getSize() { 
        return BaseNanoList.getBaseNanoList().size();
    }
    public Object getElementAt(int i) { 
        return BaseNanoList.getBaseNanoList().get(i); 
    } 
    public void update(){
        fireContentsChanged(this, 0, getSize()-1);
    }
    
}
