/*
 * GPL.java
 * Copyright (C) 2002 Klaus Rennecke.
 * Created on December 3, 2002, 6:02 AM
 */

package net.sourceforge.fraglets.yaelp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

/**
 * Utility class to read gpl.txt.
 * @author  marion@users.sourceforge.net
 */
public class GPL {
    /** The cached fragment. */
    private static String gplFragment;
    
    /** The cached version. */
    private static String version;
    
    /** Read gpl.txt and return the fragment used for about dialogs.
     * @return the GPL fragment
     */    
    public static synchronized String getFragment() {
        if (gplFragment != null) {
            return gplFragment;
        } else try {
            StringBuffer buffer = new StringBuffer();
            InputStream stream = GPL.class
                .getResource("gpl.txt").openStream();
            stream.skip(15816); // HACK
            BufferedReader reader = new BufferedReader
                (new InputStreamReader(stream, "US-ASCII"));
            String line;
            int count = 13;
            while (--count >= 0 && (line = reader.readLine()) != null) {
                buffer.append(line.trim()).append('\n');
            }
            return gplFragment = buffer.toString();
        } catch (Exception ex) {
            return gplFragment = "<ERROR: gpl.txt missing: "+ex+">";
        }
    }
    
    /** Read version.properties and return value of the version property.
     * @return version string
     */    
    public static synchronized String getVersion() {
        if (version != null) {
            return version;
        } else try {
            return version = ResourceBundle
                .getBundle(GPL.class.getPackage().getName()
                .replace('.', '/')+"/version")
                .getString("version");
        } catch (Exception ex) {
            return gplFragment = "<ERROR: version.properties missing: "+ex+">";
        }
    }
    
    /** Format an about text, inserting the given application name and
     * copyright claim.
     * @param application the application name
     * @param claim the copyright claim
     * @return formatted about text including the GPL fragment.
     */    
    public static String format(String application, String claim) {
        return application + ", version " + getVersion() + '\n'
            + claim + '\n' + '\n' + getFragment();
    }
    
    public static void main(String args[]) {
        System.out.print(format("GPL utility", "Copyright (C) 2002 Klaus Rennecke."));
        System.out.flush();
    }
}
