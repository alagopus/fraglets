/*
 * Query.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.taskdef;

// IMPORTANT! You may need to mount ant.jar before this class will
// compile. So mount the JAR modules/ext/ant.jar (NOT modules/ant.jar)
// from your IDE installation directory in your Filesystems before
// continuing to ensure that it is in your classpath.

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import org.apache.tools.ant.BuildException;
import javax.swing.JPasswordField;

/**
 * Simple ant task to display an interactive query dialog asking the
 * user for additional information.  This may be used to set properties
 * during build time e.g. for passwords.
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
 * @version $Revision: 1.1 $
 */
public class Query extends Task {
    
    /** Holds value of property prompt. */
    private String prompt;
    
    /** Holds value of property title. */
    private String title;
    
    /** Holds value of property property. */
    private String property;
    
    /** Holds value of property secret. */
    private boolean secret;
    
    /** Execute task.  Show a dialog and record the answer in a property. */
    public void execute() throws BuildException {
        String answer;
        if (secret) {
            JPasswordField field = new JPasswordField(20);
            int result = JOptionPane.showConfirmDialog(new JFrame(),
                new Object[] { prompt, field }, title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                // FIXME this does not destroy the contents later.
                answer = new String(field.getPassword());
            } else {
                answer = null;
            }
        } else {
            answer = JOptionPane.showInputDialog(new JFrame(),
                prompt, title, JOptionPane.QUESTION_MESSAGE);
        }
        
        if (answer == null) {
            throw new BuildException("User canceled", location);
        }
        
        project.setProperty(property, answer);
    }
    
    /** Getter for property prompt.
     * @return Value of property prompt.
     */
    public String getPrompt() {
        return this.prompt;
    }
    
    /** Setter for property prompt.
     * @param prompt New value of property prompt.
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    /** Getter for property title.
     * @return Value of property title.
     */
    public String getTitle() {
        return this.title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /** Getter for property property.
     * @return Value of property property.
     */
    public String getProperty() {
        return this.property;
    }
    
    /** Setter for property property.
     * @param property New value of property property.
     */
    public void setProperty(String property) {
        this.property = property;
    }
    
    /** Getter for property secret.
     * @return Value of property secret.
     */
    public boolean isSecret() {
        return this.secret;
    }
    
    /** Setter for property secret.
     * @param secret New value of property secret.
     */
    public void setSecret(boolean secret) {
        this.secret = secret;
    }
    
}
