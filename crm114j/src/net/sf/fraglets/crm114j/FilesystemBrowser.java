/*
 * $Id: FilesystemBrowser.java,v 1.1 2004-04-03 23:36:47 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package net.sf.fraglets.crm114j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import thinlet.Thinlet;

/**
 * @version $Id: FilesystemBrowser.java,v 1.1 2004-04-03 23:36:47 marion Exp $
 */
public class FilesystemBrowser {
    
    private Thinlet thinlet;
    
    public FilesystemBrowser(Thinlet thinlet) throws IOException {
        this.thinlet = thinlet;
    }
    
    public static void show(Thinlet thinlet) throws IOException {
        new FilesystemBrowser(thinlet).show();
    }
    
    public void show() throws IOException {
        Object dialog = Thinlet.create("dialog");
        thinlet.setInteger(dialog, "width", 300);
        thinlet.setInteger(dialog, "height", 200);
        thinlet.add(dialog, thinlet.parse("fsb.xml", this));
        thinlet.add(dialog);
    }
    
    public void init(Object tree) {
        load(tree, File.listRoots());
    }
    
    public void collapse(Object node) {
        thinlet.removeAll(node);
        if (isDirectory(node)) {
            thinlet.add(node, createLoader());
        }
    }
    
    public void expand(Object node) {
        if (hasLoader(node)) {
            load(node, ((File)thinlet.getProperty(node, "file")).listFiles());
        }
    }
    
    private void load(Object node, File[] list) {
        Arrays.sort(list);
        Object last = null;
        thinlet.removeAll(node);
        for (int i = 0; i < list.length; i++) {
            addFile(node, list[i]);
        }
    }
    
    public void choose(Object item) {
        if (isDirectory(item)) {
            if (thinlet.getBoolean(item, "expanded")) {
                thinlet.setBoolean(item, "expanded", false);
                collapse(item);
            } else {
                thinlet.setBoolean(item, "expanded", true);
                expand(item);
            }
        } else {
            if (thinlet instanceof FileSelectionListener) {
                File file = (File)thinlet.getProperty(item, "file");
                ((FileSelectionListener)thinlet).fileSelected(file);
            }
            while (item != null && Thinlet.getClass(item) != "tree") {
                item = thinlet.getParent(item);
            }
            if (item != null) {
                item = thinlet.getParent(item);
            }
            if (item != null) {
                thinlet.remove(item);
            }
        }
    }
    
    private void addFile(Object node, File file) {
        try {
            Object child = Thinlet.create("node");
            thinlet.putProperty(child, "file", file);
            String name = file.getName();
            if (name == null || name.length() == 0) {
                name = file.getAbsolutePath();
            }
            thinlet.setString(child, "text", name);
            if (isDirectory(file)) {
                thinlet.setBoolean(child, "expanded", false);
                thinlet.add(child, createLoader());
            }
            thinlet.add(node, child);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean hasLoader(Object node) {
        Object first = thinlet.getItem(node, 0);
        if (first != null) {
            return thinlet.getString(first, "text") == "...";
        } else {
            return false;
        }
    }
    
    private Object createLoader() {
        Object loader = Thinlet.create("node");
        thinlet.setString(loader, "text", "...");
        return loader;
    }

    private boolean isDirectory(Object node) {
        File file;
        if (node instanceof File) {
            file = (File)node;
        } else {
            file = (File)thinlet.getProperty(node, "file");
        }
        return file != null && !file.isFile();
    }
    
    public static interface FileSelectionListener {
        public void fileSelected(File file);
    }
}
