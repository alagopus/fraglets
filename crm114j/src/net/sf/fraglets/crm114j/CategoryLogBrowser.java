/*
 * $Id: CategoryLogBrowser.java,v 1.1 2004-04-03 23:36:47 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package net.sf.fraglets.crm114j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

/**
 * @version $Id: CategoryLogBrowser.java,v 1.1 2004-04-03 23:36:47 marion Exp $
 */
public class CategoryLogBrowser extends Thinlet implements FilesystemBrowser.FileSelectionListener {
    
    private RandomAccessFile file;
    
    private static final String DEFAULT_CATEGORY_SUFFIX = ".crm114";
    
    private CSSFile[] categoryFile;

    public CategoryLogBrowser() throws IOException {
        setResourceBundle(CategoryLogMessages.getResourceBundle());
        add(parse("clb.xml"));
        initCategories();
    }
    
    private void initCategories() throws IOException {
        File[] list = new File(".").listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(DEFAULT_CATEGORY_SUFFIX);
            }
        });
        int length = list == null ? 0 : list.length;
        if (list != null) {
            categoryFile = new CSSFile[length];
            Object widget = find("context");
            removeAll(widget);
            for (int i = 0; i < length; i++) {
                categoryFile[i] = new CSSFile(list[i], "rw");
                String name = list[i].getName();
                name = name.substring(
                        0,
                        name.length() - DEFAULT_CATEGORY_SUFFIX.length());
                Object item = create("checkboxmenuitem");
                setString(item, "text", name);
                setBoolean(item, "selected", true);
                setMethod(item, "action", "updateDetails()", getDesktop(), this);
                add(widget, item);
            }
        }
    }
    
    public boolean isCategoryEnabled(int index) {
        return getBoolean(getItem(find("context"), index), "selected");
    }

    public void exit() {
        if (getParent() instanceof FrameLauncher) {
            System.exit(0);
        }
    }
    
    public void open() {
        try {
            FilesystemBrowser.show(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void updateHeader() {
        try {
            long pos = file.getFilePointer();
            boolean bol = pos == 0;
            byte[] buffer = new byte[8000];
            int off = 0, len = 0;
            while (!bol) {
                if (off >= len) {
                    len = file.read(buffer);
                    pos += len;
                    off = 0;
                }
                if (buffer[off++] == '\n') {
                    bol = true;
                }
            }
            file.seek(pos += off - len);
            Object item = null;
            Object header = find("header");
            Object context = find("context");
            removeAll(header);
            while (getCount(header) < 10) {
                String line = file.readLine();
                if (line == null) {
                    break;
                } else {
                    pos += line.length(); // TODO: not correct
                }
                Statistics[] s = CSSFile.classify(categoryFile, new Tokenizer(line));
                int best = Statistics.bestOf(s);
                String name = getString(getItem(context, best), "text");
                if (item == null || "start".equals(name)) {
                    item = create("item");
                    putProperty(item, "pos", new Long(pos));
                    add(header, item);
                }
                StringBuffer b = (StringBuffer)getProperty(item, name);
                if (b == null) {
                    putProperty(item, name, b = new StringBuffer());
                }
                if (b.length() > 0) {
                    b.append('\n');
                }
                b.append(line);
            }
            Object[] items = getItems(header);
            for (int i = 0; i < items.length; i++) {
                StringBuffer b = (StringBuffer)getProperty(items[i], "start");
                if (b != null) {
                    setString(items[i], "text", b.toString());
                }
            }
            updateDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void updateDetails() {
        Object details = find("details");
        Object item = getSelectedItem(find("header"));
        Object[] shown = getItems(find("context"));
        StringBuffer text = new StringBuffer();
        for (int i = 0; i < shown.length; i++) {
            if (getBoolean(shown[i], "selected")) {
                StringBuffer b = (StringBuffer)getProperty(item, getString(shown[i], "text"));
                if (b != null) {
                    if (text.length() > 0) {
                        text.append("\n");
                    }
                    text.append(b);
                }
            }
        }
        setString(details, "text", text.toString().replaceAll("[\r\t]", "_"));
    }
    
    public void seek(Object slider) {
        try {
            int min = getInteger(slider, "minimum");
            int max = getInteger(slider, "maximum");
            int val = getInteger(slider, "value");
            double portion = (double)(val - min) / (double)max;
            file.seek((long)(file.length() * portion));
            updateHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        try {
            FrameLauncher launcher = new FrameLauncher(
                CategoryLogMessages.getString("clb.title"), //$NON-NLS-1$
                new CategoryLogBrowser(), 500, 400);
//            launcher.pack();
            launcher.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fileSelected(File file) {
        try {
            if (this.file != null) {
                this.file.close();
            }
            this.file = new RandomAccessFile(file, "r");
            updateHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
