/*
 * Main.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Mar 14, 2003 by unknown
 */
package net.sourceforge.fraglets.zieh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

/**
 * @author unknown
 */
public class Main extends Thinlet {

    public Main() {
        try {
            add(parse("main.xml"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // -- handlers

    public void exit() {
        System.exit(0);
    }

    public void about() {
        add("about.xml");
    }

    public void license(Object old) {
        remove(old);
        add("license.xml");
    }
    
    public void loadLicense(Object area) {
        InputStream inputstream = null;
        try {
            inputstream = getClass().getResourceAsStream("COPYING");
        } catch (Throwable e) {
        }
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(inputstream));
        StringBuffer buffer = new StringBuffer();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setString(area, "text", buffer.toString());
    }
    
    public void initLocal(Object tree) {
        File current = new File(".");
        String list[] = current.list();
        for (int i = 0; i < list.length; i++) {
            Object node = create("node");
            setString(node, "text", list[i]);
            add(tree, node);
        }
    }

    // -- handler utilities

    protected void add(String name) {
        try {
            super.add(parse(name));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FrameLauncher("Zieh", new Main(), 600, 400);
    }
}
