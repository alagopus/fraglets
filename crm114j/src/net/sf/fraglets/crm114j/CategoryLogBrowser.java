/*
 * $Id: CategoryLogBrowser.java,v 1.2 2004-04-04 19:10:35 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package net.sf.fraglets.crm114j;

import java.awt.Cursor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

/**
 * @version $Id: CategoryLogBrowser.java,v 1.2 2004-04-04 19:10:35 marion Exp $
 */
public class CategoryLogBrowser extends Thinlet implements FilesystemBrowser.FileSelectionListener {
    
    private RandomAccessFile file;
    
    private static final char TAB_SUBSTITUTE = '\u21e5';
    
    private static final String DEFAULT_CATEGORY_SUFFIX = ".crm114";
    
    private CSSFile[] categoryFile;
    
    private long lastPos;

    public CategoryLogBrowser() throws IOException {
        setResourceBundle(Resources.getResourceBundle());
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
        if (length <= 0) {
            // no categories -- create default category
            list = new File[] {
                new File("start"+DEFAULT_CATEGORY_SUFFIX)
            };
            length = list.length;
        }
        if (length > 0) {
            categoryFile = new CSSFile[length];
            clearCategories();
            for (int i = 0; i < length; i++) {
                categoryFile[i] = new CSSFile(list[i], "rw");
                String name = list[i].getName();
                name = name.substring(
                        0,
                        name.length() - DEFAULT_CATEGORY_SUFFIX.length());
                addCategory(name);
            }
        }
    }
    
    private void clearCategories() {
        Object widget;
        
        widget = find("context");
        if (widget != null) {
            removeAll(widget);
        }
        widget = find("learn");
        if (widget != null) {
            removeAll(widget);
        }
        widget = find("forget");
        if (widget != null) {
            removeAll(widget);
        }
    }
    
    private void addCategory(String name) {
        Object widget, item;
        
        widget = find("context");
        if (widget != null) {
            item = create("checkboxmenuitem");
            setString(item, "text", name);
            setBoolean(item, "selected", !name.equals("start"));
            setMethod(
                item,
                "action",
                "updateDetails()",
                getDesktop(),
                this);
            add(widget, item);
        }
        widget = find("learn");
        if (widget != null) {
            item = create("menuitem");
            setString(item, "text", name);
            setMethod(
                item,
                "action",
                "learn(this)",
                getDesktop(),
                this);
            add(widget, item);
        }
        widget = find("forget");
        if (widget != null) {
            item = create("menuitem");
            setString(item, "text", name);
            setMethod(
                item,
                "action",
                "forget(this)",
                getDesktop(),
                this);
            add(widget, item);
        }
    }
    
    public void newCategory() {
        try {
            add(parse("ncd.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void createCategory(Object field) {
        String name = getString(field, "text");
        remove(find("categoryDialog"));
        if (name == null || name.length() == 0) {
            return;
        }
        Object context = find("context");
        int count = getCount(context);
        for (int i = 0; i < count; i++) {
            Object category = getItem(context, i);
            if (name.equalsIgnoreCase(getString(category, "text"))) {
                return; // already exists
            }
        }
        try {
            CSSFile[] grow = new CSSFile[categoryFile.length + 1];
            System.arraycopy(categoryFile, 0, grow, 0, categoryFile.length);
            grow[categoryFile.length] =
                new CSSFile(new File(name + DEFAULT_CATEGORY_SUFFIX), "rw");
            addCategory(name);
            categoryFile = grow;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close(Object dialog) {
        remove(dialog);
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
    
    public void copy() {
        String text = getSelection();
        if (text != null) {
            getToolkit().getSystemClipboard().setContents(
                new StringSelection(text),
                null);
        }
    }
    
    public void learn(Object item) {
        learn(item, 1);
    }
    
    public void forget(Object item) {
        learn(item, -1);
    }
    
    public void learn(Object item, int sense) {
        try {
            String text = getSelection();
            if (text != null) {
                Object parent = getParent(item);
                int end = getCount(parent);
                for (int i = 0; i < end; i++) {
                    if (getItem(parent, i) == item) {
                        if (i < categoryFile.length) {
                            StringTokenizer tok =
                                new StringTokenizer(
                                    text.replace(TAB_SUBSTITUTE, '\t'),
                                    "\n");
                            while (tok.hasMoreTokens()) {
                                categoryFile[i].learn(
                                    new Tokenizer(tok.nextToken()),
                                    sense);
                            }
                            file.seek(lastPos);
                            updateHeader();
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getSelection() {
        Object details = find("details");
        String text = getString(details, "text");
        int start = getInteger(details, "start");
        int end = getInteger(details, "end");
        if (end >= 0 && start > end) {
            int swap = end;
            end = start;
            start = swap;
        }
        if (start >= 0 && start < end) {
            return text.substring(start, end);
        } else {
            return null;
        }
    }
    
    public void updateHeader() {
        Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            long pos = file.getFilePointer();
            lastPos = pos;
            boolean bol = pos == 0, eol = false;
            Object item = null;
            Object header = find("header");
            Object context = find("context");
            removeAll(header);
            byte[] buffer = new byte[8000];
            Tokenizer tok = new Tokenizer(new char[buffer.length]);
            int off = 0, len = 0, max = 0;
            for(;;) {
                if (off >= len) {
                    pos += len;
                    len = file.read(buffer);
                    if (len <= 0) {
                        break;
                    } else {
                        off = 0;
                    }
                }
                char c = (char)buffer[off++];
                if (c == '\n') {
                    eol = true;
                } else if (c == '\r') {
                    bol = true;
                } else if (bol) {
                    eol = true;
                } else if (eol) {
                    bol = true;
                }
                if (eol && bol && max > 0) {
                    tok.reset(tok.getBuffer(), 0, 0, max);
                    Statistics[] s = CSSFile.classify(categoryFile, tok);
                    int best = Statistics.bestOf(s);
                    String name = getString(getItem(context, best), "text");
                    if (item == null || "start".equals(name)) {
                        if (getCount(header) >= 10) {
                            break;
                        }
                        item = create("item");
                        putProperty(item, "pos", new Long(pos + off - max));
                        add(header, item);
                    }
                    StringBuffer b = (StringBuffer)getProperty(item, name);
                    if (b == null) {
                        putProperty(item, name, b = new StringBuffer());
                    }
                    if (b.length() > 0) {
                        b.append('\n');
                    }
                    b.append(tok.getBuffer(), 0, max);
                    max = 0;
                }
                if (eol && bol) {
                    eol = bol = false;
                }
                if (c == '\n' || c == '\r') {
                    continue;
                }
                try {
                    tok.getBuffer()[max] = c;
                    max += 1;
                } catch (ArrayIndexOutOfBoundsException e) {
                    char[] small = tok.getBuffer();
                    int more = small.length * 2;
                    char[] grow = new char[more];
                    System.arraycopy(small, 0, grow, 0, small.length);
                    grow[max++] = c;
                    tok.reset(grow, 0, 0, max);
                }
            }
            Object[] items = getItems(header);
            for (int i = 0; i < items.length; i++) {
                StringBuffer b = (StringBuffer)getProperty(items[i], "start");
                if (b != null) {
                    setString(items[i], "text", b.toString());
                }
            }
            long part = pos + off - max - lastPos, all = file.length();
            setString(find("status"), "text", "pos="+lastPos+", part="+part);
            if (part > 0) {
                Object slider = find("slider");
                max = getInteger(slider, "maximum");
                setInteger(slider, "block", (int)(max * part / all));
            }
            item = find("portion");
            if (item != null) {
                max = getInteger(item, "maximum");
                setInteger(item, "value", (int)(max * (lastPos + part) / all));
            }
            updateDetails();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            setCursor(oldCursor);
        }
    }
    
    public void updateDetails() {
        Object details = find("details");
        Object item = getSelectedItem(find("header"));
        StringBuffer text = new StringBuffer();
        Object context = find("context");
        int count = getCount(context);
        for (int i = 0; i < count; i++) {
            Object category = getItem(context, i);
            if (getBoolean(category, "selected")) {
                StringBuffer b =
                    (StringBuffer)getProperty(item,
                        getString(category, "text"));
                if (b != null) {
                    if (text.length() > 0) {
                        text.append("\n");
                    }
                    text.append(b);
                }
            }
        }
        setString(
            details,
            "text",
            text.toString().replace('\t', TAB_SUBSTITUTE));
    }
    
    public void seek(Object slider) {
        try {
            int min = getInteger(slider, "minimum");
            int max = getInteger(slider, "maximum");
            int val = getInteger(slider, "value");
            double portion = (double)(val - min) / (double)max;
            long newPos = (long)(file.length() * portion);
            if (newPos != lastPos) {
                file.seek(newPos);
                updateHeader();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        try {
            FrameLauncher launcher = new FrameLauncher(
                Resources.getString("clb.title"), //$NON-NLS-1$
                new CategoryLogBrowser(), 600, 400);
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
            Object slider = find("slider");
            setInteger(slider, "value", 0);
            updateHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
