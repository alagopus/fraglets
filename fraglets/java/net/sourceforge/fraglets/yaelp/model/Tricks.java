/*
 * Tricks.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on December 2, 2002, 1:52 PM
 */

package net.sourceforge.fraglets.yaelp.model;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import net.sourceforge.fraglets.targa.TGADecoder;

/**
 * Library of tricks.
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
 */
public class Tricks {
    
    public static void main(String args[]) {
        System.out.println("tor='"+tor(args[0])+"'");
    }
    
    /** Rotate string constant. String constants are rotated here
     * to avoid hitting in search engines. */
    public static String rot(String input) {
        StringBuffer output = new StringBuffer();
        int scan = input.length();
        while (--scan >= 0) {
            output.append((char)(input.charAt(scan) + 1));
        }
        return output.toString();
    }
    
    /** Reverse-rotate string constant. String constants are rotated
     * here to avoid hitting in search engines. */
    public static String tor(String input) {
        StringBuffer output = new StringBuffer();
        int scan = input.length();
        while (--scan >= 0) {
            output.append((char)(input.charAt(scan) - 1));
        }
        return output.toString();
    }
    
    private static File installation;
    private static boolean installationInitialized;
    
    public static synchronized File findInstallation() {
        if (installationInitialized) {
            return installation;
        }
        installationInitialized = true;
        File roots[] = File.listRoots();
        if (roots != null) for (int i = roots.length; --i >= 0;) {
            File level1[] = roots[i].listFiles(new java.io.FileFilter() {
                public boolean accept(File file) { return file.isDirectory(); }
            });
            if (level1 != null) for (int j = 0; j < level1.length; j++) {
                String marker = rot("srdtPqduD");
                File probe = new File(level1[j], marker+'/'+marker+".exe");
                if (probe.exists()) {
                    return installation = new File(level1[j], marker);
                }
            }
        }
        return null; // not found
    }
    
    private static Image tileset[];
    
    public static Image getImage(Component c, String name) {
        try {
            name = rot(name + ".skt`edc.rdkheht") + ".tga";
            FileInputStream in = new FileInputStream
                (new File(findInstallation(), name));
            try {
                return c.createImage(TGADecoder.decode(in));
            } finally {
                in.close();
            }
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static synchronized Image getTileset(Component c, int n) {
        if (tileset == null) {
            tileset = new Image[30];
        }
        if (tileset[n] == null) {
            try {
                String name = tor(String.valueOf(n < 5 ? n + 1 : n - 4))
                    + (n < 5 ? "/rkkdor" : "ldshf`qc");
                tileset[n] = getImage(c, name);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tileset[n];
    }
    
    public static Image getTile(Component c, int n) {
        Image tileset = getTileset(c, n / 36);
        Image tile = c.createImage(40, 40);
        Graphics g = tile.getGraphics();
        try {
            n = n % 36;
            g.drawImage(tileset, (n % 6) * -40, (n / 6) * -40, c);
        } finally {
            g.dispose();
        }
        return tile;
    }
}
