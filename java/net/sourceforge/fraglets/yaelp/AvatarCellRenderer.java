/*
 * AvatarCellRenderer.java
 *
 * Created on 30. Juni 2002, 17:49
 */

package net.sourceforge.fraglets.yaelp;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Font;
import java.awt.Component;

/**
 *
 * @author  marion@users.sourceforge.net
 */
public class AvatarCellRenderer extends DefaultTableCellRenderer {
    AvatarFilter mainFilter = new AvatarFilter.Main();
    
    /** Creates a new instance of AvatarCellRenderer */
    public AvatarCellRenderer() {
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean selected, boolean focus, int row, int col) {
        Component result = super.getTableCellRendererComponent(table, value,
            selected, focus, row, col);
        setBold(result, col == 0 && isLogin(table, row));
        return result;
    }
    
    protected boolean isLogin(JTable table, int row) {
        RosterTableModel model = (RosterTableModel)table.getModel();
        return mainFilter.accept(model.getAvatar(row));
    }
    
    protected Font fontPlain;
    protected Font fontBold;
    
    protected void setBold(Component component, boolean bold) {
        if (fontPlain == null) {
            fontPlain = component.getFont();
        }
        if (bold) {
            if (fontBold == null) {
                fontBold = fontPlain.deriveFont(Font.BOLD);
            }
            component.setFont(fontBold);
        } else {
            component.setFont(fontPlain);
        }
    }
}
