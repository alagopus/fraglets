/*
 * $Id: FilesystemBrowser.java,v 1.2 2004-04-04 19:10:35 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package net.sf.fraglets.crm114j;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import thinlet.Thinlet;

/**
 * @version $Id: FilesystemBrowser.java,v 1.2 2004-04-04 19:10:35 marion Exp $
 */
public class FilesystemBrowser {
    
    private static final SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private Thread runner;
    private Thinlet thinlet;
    private Object top;
    
    public FilesystemBrowser(Thinlet thinlet) throws IOException {
        this.thinlet = thinlet;
    }
    
    public static void show(Thinlet thinlet) throws IOException {
        new FilesystemBrowser(thinlet).show();
    }
    
    public void show() throws IOException {
        thinlet.add(thinlet.parse("fsb.xml", this));
    }
    
    public void init(Object top) {
        Object tree = thinlet.find(this.top = top, "root");
        load(tree, File.listRoots());
    }
    
    public void collapse(Object node) {
        if ("root".equals(thinlet.getString(node, "name"))) {
            thinlet.setBoolean(node, "expanded", true);
            thinlet.repaint();
            return;
        }
        Thread thread = (Thread)thinlet.getProperty(node, "thread");
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        int index = getIndex(node);
        int scan = getNodeCount(node);
        Object table = thinlet.find(top, "table");
        while (--scan >= 0) {
            thinlet.remove(thinlet.getItem(table, index + 1));
        }
        thinlet.removeAll(node);
        if (isDirectory(node)) {
            thinlet.add(node, createLoader());
        }
        syncScroll();
    }
    
    private int getNodeCount(Object node) {
        if (hasLoader(node)) {
            return 0;
        }
        int count = thinlet.getCount(node);
        int result = count;
        for (int i = 0; i < count; i++) {
            Object subnode = thinlet.getItem(node, i);
            if (subnode != null && thinlet.getBoolean(subnode, "expanded")) {
                result += getNodeCount(subnode);
            }
        }
        return result;
    }

    public void expand(final Object node) {
        if (hasLoader(node)) {
            Thread thread = new Thread() {
                File[] list = null;
                boolean first = true;
                public void run() {
                    if (top != null && thinlet.getParent(top) != null) {
                        if (first) {
                            first = false;
                            try {
                                File file = (File)thinlet.getProperty(node, "file");
                                list = file.listFiles();
                                EventQueue.invokeLater(this);
                            } catch (Exception e) {
                            }
                        } else {
                            if (list != null) {
                                load(node, list);
                            } else {
                                thinlet.setBoolean(node, "expanded", false);
                                Object loader = thinlet.getItem(node, 0);
                                thinlet.remove(loader); // force update
                                thinlet.add(node, loader, 0);
                            }
                        }
                    }
                }
            };
            thinlet.putProperty(node, "thread", thread);
            thread.start();
        }
        syncScroll();
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
        if (item == null) {
            return;
        } else if (isDirectory(item)) {
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
            close();
        }
    }
    
    public void chooseCurrent() {
        choose(thinlet.getSelectedItem(thinlet.find(top, "tree")));
    }
    
    public void close() {
        if (top != null) {
            thinlet.remove(top);
            top = null;
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
            int pos = getIndex(child);
            node = thinlet.find(top, "table");
            thinlet.add(node, child = Thinlet.create("row"), pos);
            node = child;
            thinlet.add(node, child = Thinlet.create("cell"));
            long value;
            String text;
            value = getLastModified(file);
            if (value >= 0) {
                text = dateFormat.format(new Date(value));
            } else {
                text = "-";
            }
            thinlet.setString(child, "text", text);
            thinlet.add(node, child = Thinlet.create("cell"));
            value = getLength(file);
            if (value >= 0) {
                text = String.valueOf(value);
            } else {
                text = "-";
            }
            thinlet.setString(child, "text", text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean hasLoader(Object node) {
        Object first = thinlet.getItem(node, 0);
        if (first != null) {
            return thinlet.getString(first, "name") == "...";
        } else {
            return false;
        }
    }
    
    private Object createLoader() {
        Object loader = Thinlet.create("node");
        thinlet.setString(loader, "name", "...");
        thinlet.setString(loader, "text", Resources.getString("fsb.Loading"));
        return loader;
    }

    private boolean isDirectory(Object node) {
        File file;
        if (node instanceof File) {
            file = (File)node;
        } else {
            file = (File)thinlet.getProperty(node, "file");
        }
        return file != null && (file.getParentFile() == null || !file.isFile());
    }
    
    private long getLastModified(File file) {
        // avoid file system roots
        if (file.getParentFile() != null) {
            return file.lastModified();
        } else {
            return -1;
        }
    }
    
    private long getLength(File file) {
        // avoid file system roots and directories
        if (file.getParentFile() != null && file.isFile()) {
            return file.length();
        } else {
            return -1;
        }
    }
    
    private int getIndex(Object node) {
        int result = 0;
        Object root = thinlet.find(top, "root");
        while (node != null && node != root) {
            Object parent = thinlet.getParent(node);
            int end = thinlet.getCount(parent);
            for (int i = 0; i < end; i++ ) {
                if (thinlet.getItem(parent, i) == node) {
                    break;
                } else {
                    result += 1;
                }
            }
            node = parent;
            if (node != root) {
                result += 1;
            }
        }
        return result;
    }
    
    public static interface FileSelectionListener {
        public void fileSelected(File file);
    }
    
    public void syncScroll() {
        syncScroll(thinlet.find(top, "tree"), thinlet.find(top, "table"));
    }
    
    public void syncScroll(Object from, Object to) {
        Rectangle view = getView(from);
        Rectangle other = getView(to);
        other.y = view.y;
        thinlet.repaint(to);
    }
    
    private static Rectangle getView(Object c) {
        for (Object[] e = (Object[]) c; e != null;
                e = (Object[]) e[2]) {
            if (e[0] == ":view") {
                return (Rectangle)e[1];
            }
        }
        return null;
    }

}
