/*
 * Main.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Created on Mar 14, 2003 by Klaus Rennecke.
 */
package net.sourceforge.fraglets.zieh;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

/**
 * @author unknown
 */
public class Main extends Thinlet implements Comparator {
    
    private Process sftp;
    private String sftpCommand; // = "C:\\Programme\\PuTTY\\psftp.exe -v";
    private PrintStream sftpStream;
    private String sftpPrompt = "psftp> ";
    
    private String sftpCd = "Remote directory is now ";
    private String sftpLCd = "New local directory is ";
    private String sftpOpenPwd = "Remote working directory is ";
    private String sftpPwd = "Remote directory is ";
    private String sftpLPwd = "Current local directory is ";
    private String sftpLs = "Listing directory ";
    private String sftpListingPath;
    private ArrayList sftpListing;
    
    private Thread shuffler;
    private int sessionLength;
    
    private int mouseX;
    private int mouseY;
    private int lastX;
    private int lastY;
    
    public Main() {
        try {
            add(parse("main.xml"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void init () {
        ChatStream cs = new ChatStream();
        PrintStream ps = new PrintStream(cs);
//        System.setErr(ps);
        System.setOut(ps);
        
        setString(find("localPath"), "text", System.getProperty("user.dir"));
        initLocal(find("localList"));
        
        Thread finder = new Thread("Zieh backend finder") {
            public void run() {
                findBackend();
            }
        };
        finder.setDaemon(true);
        finder.start();
    }
    
    protected void findBackend() {
        File[] roots = File.listRoots();
        for (int i = 0; i < roots.length; i++) {
            try {
                // shortcut
                if (Thread.currentThread().isInterrupted()) return;
                findBackend(new File(roots[i], "Program Files\\PuTTY"));
            } catch (Exception ex) {
                // ignore
            }
            try {
                // shortcut
                if (Thread.currentThread().isInterrupted()) return;
                findBackend(new File(roots[i], "Programme\\PuTTY"));
            } catch (Exception ex) {
                // ignore
            }
            try {
                if (Thread.currentThread().isInterrupted()) return;
                findBackend(roots[i]);
            } catch (Exception ex) {
                // ignore
            }
        }
    }
    
    public static final FileFilter DIR_FILTER = new FileFilter() {
        public boolean accept(File f) { return f.isDirectory(); }
    };
    
    protected void findBackend(File root) throws IOException {
        String name = root.toString();
        if (name.toLowerCase().startsWith("a:")) {
            return;
        }
        setString(find("status"), "text", "searching: "+name);
        File probe =new File(root, "psftp.exe"); 
        if (probe.exists()) {
            setBackend(probe.getAbsolutePath());
        }
        File list[] = root.listFiles(DIR_FILTER);
        for (int i = 0; i < list.length; i++) {
            if (Thread.currentThread().isInterrupted()) return;
            findBackend(list[i]);
        }
    }
    
    protected void setBackend(String name) {
        appendChat("backend: '"+name+"'");
        sftpCommand = name;
        Thread.currentThread().interrupt();
    }

    // -- handlers

    public void actionExit() {
        actionClose();
        System.exit(0);
    }

    public void actionAbout() {
        add("about.xml");
    }
    
    public void actionClose() {
        try {
            if (sftp != null) {
                sessionCommand("bye");
                sftpStream.close();
                sftp.getOutputStream().close();
                sftp.waitFor();
                sftp = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void actionOpen() {
        try {
            sftp = Runtime.getRuntime().exec(sftpCommand);
            Thread t;
            
            t = new Thread
                (new ChatStream(sftp.getInputStream()) {
                    public void run() {
                        super.run();
                        sftpStream = null;
                    }
                },
                "Zieh input shuffler");
            t.setDaemon(true);
            t.start();
            
            t = new Thread
                (new ChatStream(sftp.getErrorStream()),
                "Zieh error shuffler");
            t.setDaemon(true);
            t.start();
            
            sftpStream = new PrintStream(sftp.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void actionLcd() {
        String value;
        Object path = find("localPath");
        int index = getInteger(path, "selected");
        if (index != -1) {
            value = (String)getProperty(getItem(path, index), "file");
        } else {
            value = getString(path, "text");
        }
        sessionCommand("lcd "+quoteSsh2(value));
    }
    
    public void actionCd() {
        String value;
        Object path = find("remotePath");
        int index = getInteger(path, "selected");
        if (index != -1) {
            value = (String)getProperty(getItem(path, index), "file");
        } else {
            value = getString(path, "text");
        }
        sessionCommand("cd "+quoteSsh2(value));
    }
    
    public void actionLocalRemove() {
        File base = new File(System.getProperty("user.dir"));
        Object items[] = getSelectedItems(find("localList"));
        for (int i = 0; i < items.length; i++) {
            Object row = items[i];
            String name = getString(getItem(row, 0), "text");
            localRemove(new File(base, name));
        }
        initLocal(find("localList"));
    }
    
    public void actionRemoteRemove() {
        Object items[] = getSelectedItems(find("remoteList"));
        for (int i = 0; i < items.length; i++) {
            Object row = items[i];
            String name = getString(getItem(row, 0), "text");
            sessionCommand("rm " + quoteSsh2(name));
        }
    }
    
    public void actionPut() {
        Object items[] = getSelectedItems(find("localList"));
        for (int i = 0; i < items.length; i++) {
            Object row = items[i];
            String name = getString(getItem(row, 0), "text");
            sessionCommand("put " + quoteSsh2(name));
        }
    }
    
    public void actionGet() {
        Object items[] = getSelectedItems(find("remoteList"));
        for (int i = 0; i < items.length; i++) {
            Object row = items[i];
            String name = getString(getItem(row, 0), "text");
            sessionCommand("get " + quoteSsh2(name));
        }
    }
    
    protected void localRemove(File file) {
        if (file.isDirectory()) {
            File list[] = file.listFiles();
            for (int i = 0; i < list.length; i++) {
                localRemove(list[i]);
            }
        }
        file.delete();
    }
    
    public void localSelect() {
        clearSelection(find("remoteList"));
        lastX = mouseX;
        lastY = mouseY;
    }

    public void remoteSelect() {
        clearSelection(find("localList"));
        lastX = mouseX;
        lastY = mouseY;
    }
    
    protected void triggerSelection() {
        {
            Object item = getSelectedItem(find("localList"));
            if (item != null) {
                triggerSelection("lcd", "put", item);
                return;
            }
        }
        {
            Object item = getSelectedItem(find("remoteList"));
            if (item != null) {
                triggerSelection("cd", "get", item);
                return;
            }
        }
    }
    
    protected void triggerSelection(String cd, String mv, Object row) {
        String name = getString(getItem(row, 0), "text");
        boolean dir = "true".equals(getProperty(row, "directory"));
        sessionCommand((dir ? cd : mv) + " " + quoteSsh2(name));
        return;
    }

    public void license(Object old) {
        remove(old);
        add("license.xml");
        loadLicense(find("licenseText"));
    }
    
    public void loadLicense(Object area) {
        InputStream inputstream = null;
        try {
            inputstream = getClass().getResourceAsStream("COPYING");
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setString(area, "text", buffer.toString());
    }
    
    public void initLocal(Object c) {
        String dir = System.getProperty("user.dir");
        setString(find("localPath"), "text", dir);
        removeAll(c);
        File current = new File(dir);
        initLocalPath(current);
        File list[] = current.listFiles();
        DateFormat df = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        if (list == null || list.length == 0) {
            list = new File[] { new File("..") };
        } else {
            Arrays.sort(list, this);
        }
        boolean empty = true;
        for (int i = 0; i < list.length; i++) {
            if (".".equals(list[i].getName())) {
                // ignore .
                continue;
            } else if (empty && !"..".equals(list[i].getName())) {
                // insert ..
                addLocalEntry(c, new File(".."), df);
            }
            addLocalEntry(c, list[i], df);
            empty = false;
        }
    }
    
    protected void addLocalEntry(Object c, File file, DateFormat df) {
        Object row = create("row");
        Object cell;
        cell = create("cell");
        setString(cell, "text", file.getName());
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", String.valueOf(file.length()));
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", df.format(new Date(file.lastModified())));
        add(row, cell);
        putProperty(row, "directory", String.valueOf(file.isDirectory()));
        add(c, row);
    }
    
    protected void initLocalPath(File dir) {
        Object path = find("localPath");
        removeAll(path);
        File roots[] = File.listRoots();
        ArrayList dirs = new ArrayList();
        File parent = dir;
        do {
            dirs.add(parent);
            parent = parent.getParentFile();
        } while (parent != null);
        
        parent = (File)dirs.get(dirs.size() - 1);
        int rootIndex = 0;
        while (rootIndex < roots.length) {
            if (parent.equals(roots[rootIndex])) {
                break; // found root
            } else {
                rootIndex += 1;
            }
        }
        
        // create indent string
        int scan = dirs.size() - 1;
        StringBuffer buffer = new StringBuffer(scan);
        while (--scan >= 0) {
            buffer.append(' ');
        }
        String indent = buffer.toString();
        
        if (rootIndex == roots.length) {
            // not found in roots, add before
            scan = dirs.size();
            while (--scan >= 0) {
                Object item = create("choice");
                File part = (File)dirs.get(scan);
                setString(item, "text",
                    indent.substring(scan) + describeShort(part));
                putProperty(item, "file", part.getAbsolutePath());
                add(path, item);
            }
        }
        for (int i = 0; i < roots.length; i++) {
            if (i == rootIndex) {
                scan = dirs.size();
                while (--scan >= 0) {
                    Object item = create("choice");
                    File part = (File)dirs.get(scan);
                    setString(item, "text",
                        indent.substring(scan) + describeShort(part));
                    putProperty(item, "file", part.getAbsolutePath());
                    add(path, item);
                }
            } else {
                Object item = create("choice");
                setString(item, "text", describeShort(roots[i]));
                putProperty(item, "file", roots[i].getAbsolutePath());
                add(path, item);
            }
        }
    }
    
    protected void initRemotePath() {
        Object path = find("remotePath");
        removeAll(path);
        String dir = getString(path, "text");
        StringTokenizer tok = new StringTokenizer(dir, "\\/", true);
        StringBuffer indent = new StringBuffer();
        StringBuffer absolute = new StringBuffer();
        if (dir.startsWith("/")) {
            Object choice = create("choice");
            setString(choice, "text", "/");
            putProperty(choice, "file", "/");
            add(path, choice);
            indent.append(' ');
            absolute.append('/');
        }
        while (tok.hasMoreTokens()) {
            dir = tok.nextToken();
            absolute.append(dir);
            if (dir.equals("/") || dir.equals("\\")) {
                continue;
            }
            Object choice = create("choice");
            setString(choice, "text", indent + dir);
            putProperty(choice, "file", absolute.toString());
            add(path, choice);
            indent.append(' ');
        }
    }
    
    public static String describeShort(File file) {
        String name = file.getName();
        if (name != null && name.length() > 0) {
            return name;
        } else {
            return file.toString();
        }
    }
    
    protected void parseSession(String line, Object source) {
        if (sftp != null) {
            if (source == sftp.getErrorStream()) {
                appendChat("ERROR: '"+line+"'");
                return;
            } else if (source == sftp.getInputStream()) {
                parseSession(line);
                return;
            }
        }
        appendChat(line);
    }
    
    protected void parseSession(String line) {
        while (line.startsWith(sftpPrompt)) {
            line = line.substring(sftpPrompt.length());
        }
//        appendChat("["+line+"]");
        if (line.startsWith(sftpOpenPwd)) {
            parseRcd(line.substring(sftpOpenPwd.length()));
        } else if (line.startsWith(sftpCd)) {
            parseRcd(line.substring(sftpCd.length()));
        } else if (line.startsWith(sftpLCd)) {
            String newCd = line.substring(sftpLCd.length());
            System.setProperty("user.dir", newCd);
            Object localList = find("localList");
            initLocal(localList);
            requestFocus(localList);
        } else if (line.startsWith(sftpPwd)) {
            parseRcd(line.substring(sftpPwd.length()));
        } else if (line.startsWith(sftpLs)) {
            sftpListingPath = line.substring(sftpLs.length());
            sftpListing = new ArrayList();
            sessionCommand("pwd", true); // hack - allow detection of list end
            if (!sftpListingPath.equals(getString(find("remotePath"), "text"))) {
                appendChat(line);
            }
        } else if (sftpListingPath != null) {
            sftpListing.add(line);
            if (!sftpListingPath.equals(getString(find("remotePath"), "text"))) {
                appendChat(line);
            }
        } else if (line.startsWith("local:") && line.indexOf("=> remote:") > 0) {
            sessionCommand("ls");
            setBoolean(find("localList"), "enabled", true);
        } else if (line.startsWith("remote:") && line.indexOf("=> local:") > 0) {
            initLocal(find("localList"));
            setBoolean(find("localList"), "enabled", true);
            setBoolean(find("remoteList"), "enabled", true);
        } else if (line.startsWith("psftp: not connected to a host") ||
            line.startsWith("local: unable to open ")) {
            setBoolean(find("localList"), "enabled", true);
            setBoolean(find("remoteList"), "enabled", true);
        } else {
            appendChat(line);
        }
    }
    
    protected void parseRcd(String dir) {
        Object rp = find("remotePath");
        String old = getString(rp, "text");
        if (!dir.equals(old)) {
            setString(rp, "text", dir);
            initRemotePath();
            sessionCommand("ls");
        } else if (sftpListingPath != null) {
            String now = sftpListingPath;
            sftpListingPath = null;
            if (now.equals(old)) {
                Object list = find("remoteList");
                removeAll(list);
//                add(list, create("header"));
                for (Iterator i = sftpListing.iterator(); i.hasNext();) {
                    String element = (String)i.next();
                    parseRemoteList(element, list);
                }
                setBoolean(find("remoteList"), "enabled", true);
            }
        }
    }
    
    protected void parseRemoteList(String line, Object list) {
        // drwxr-xr-x   19 root     root         4096 Mar 27 13:49 .
        // crw-rw-rw-    1 root     root            0 Mar 16 17:02 tty
        StringTokenizer tok = new StringTokenizer(line, " ");
        String perms = tok.nextToken(); // permissions
        String links = tok.nextToken(); // link count
        String owner = tok.nextToken(); // owner
        String group = tok.nextToken(); // group
        String size = tok.nextToken();
        String rest = tok.nextToken("");
        String date = rest.substring(0, 13);
        String name = rest.substring(14);
        
        if (name.equals(".")) {
            // ignore .
            return;
        }
        
        Object row, cell;
        row = create("row");
        cell = create("cell");
        setString(cell, "text", name);
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", size);
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", date);
        add(row, cell);
        boolean isDir = "dl".indexOf(perms.charAt(0)) >= 0;
        putProperty(row, "directory", String.valueOf(isDir));
        add(list, row);
    }
    
    public void sessionInsert() {
        Object chat = find("sessionChat");
        String text = getString(chat, "text");
        if (text.length() > sessionLength) {
            int end = getInteger(chat, "end");
            if (end >= 0 && text.charAt(end) == '\n') {
                int start = text.lastIndexOf('\n', end - 1) + 1;
                String command = text.substring(start, end);
                setString(find("status"), "text", "command: '"+command+"'");
                sessionCommand(command);
            }
        }
        sessionLength = text.length();
    }
    
    public void sessionCommand(String command) {
        sessionCommand(command, false);
    }
    
    public void sessionCommand(String command, boolean silent) {
        if (sftpStream == null) {
            actionOpen();
        }
        if (sftpStream != null) {
            if (!silent) {
                setString(find("status"), "text", "command: '"+command+"'");
            }
            
            sftpStream.println(command);
            sftpStream.flush();
            
            if (command.startsWith("put ") || command.startsWith("get ")) {
                setBoolean(find("localList"), "enabled", false);
                setBoolean(find("remoteList"), "enabled", false);
            } else if (command.equals("ls")) {
                setBoolean(find("remoteList"), "enabled", false);
            } else if (command.startsWith("rm ") || command.startsWith("del ")) {
                sessionCommand("ls");
            } else if (command.startsWith("!")) {
                try {
                    Thread.sleep(200); // TODO
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initLocal(find("localList"));
            }
        }
    }
    
    protected void appendChat(String line) {
        Object chat = find("sessionChat");
        StringBuffer buffer = new StringBuffer(getString(chat, "text"));
        buffer.append(line);
        if (buffer.charAt(buffer.length() - 1) != '\n') {
            buffer.append('\n');
        }
        if (buffer.length() > 8192) {
            int end = buffer.length();
            int cutoff = buffer.length() - 8192 + 1024;
            while (cutoff < end && buffer.charAt(cutoff) != '\n') {
                cutoff += 1;
            }
            buffer.delete(0, cutoff);
        }
        setString(chat, "text", buffer.toString());
        sessionLength = buffer.length();
        setInteger(chat, "start", sessionLength);
        setInteger(chat, "end", sessionLength);
    }

    /**
     * Destroy the application window (close clicked).
     * @see thinlet.Thinlet#destroy()
     */
    public boolean destroy() {
        actionClose();
        return super.destroy();
    }

    // -- handler utilities

    protected void add(String name) {
        try {
            super.add(parse(name));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void clearSelection(Object list) {
        Object items[] = getItems(list);
        for (int i = 0; i < items.length; i++) {
            setBoolean(items[i], "selected", false);
        }
    }
    
    public static boolean near(int a, int b) {
        int d = a - b;
        return d <= 2 && d >= -2;
    }
    
    public static String quoteSsh2(String str) {
        if (str.indexOf('"') < 0) {
            return '"'+str+'"';
        } else {
            StringTokenizer tok = new StringTokenizer(str, "\"", true);
            StringBuffer buffer = new StringBuffer(str.length() + 2 * tok.countTokens());
            buffer.append('"');
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                if (token.equals("\"")) {
                    buffer.append("\"\"");
                } else {
                    buffer.append(token);
                }
            }
            buffer.append('"');
            return buffer.toString();
        }
    }
    
    // -- launcher

    public static void main(String[] args) {
        Main main = new Main();
//        Frame.open(main, "Zieh");
        new FrameLauncher("Zieh", main, 620, 440);
        main.init();
    }
    
    // -- inner classes
    
    public class ChatStream extends OutputStream implements Runnable {
        
        protected InputStream in;
        
        protected StringBuffer buffer = new StringBuffer();
        
        public ChatStream() {
        }
        
        public ChatStream(InputStream in) {
            this.in = in;
        }
        
        /**
         * @see java.io.OutputStream#write(int)
         */
        public void write(int b) throws IOException {
            switch (b) {
                case '\n':
                    append(buffer.toString());
                    buffer.setLength(0);
                case '\r':
                    break;
                case '\t':
                    buffer.append(' ');
                    break;
                default:
                    buffer.append((char)b);
                    break;
            }
        }
        
        public void append(String s) {
            parseSession(s, in);
        }
        
        /**
         * Thread runner method.
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                int n;
                while (!Thread.interrupted() && (n = in.read()) != -1) {
                    write(n);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    
    /**
     * @see java.awt.Component#processEvent(java.awt.AWTEvent)
     */
    protected void processEvent(AWTEvent e) {
        int id = e.getID();
        if (id == MouseEvent.MOUSE_PRESSED) {
            MouseEvent me = (MouseEvent)e;
            switch (me.getClickCount()) {
                case 2:
                    if (near(lastX, me.getX()) && near(lastY, me.getY())) {
                        triggerSelection();
                        return; // consumed
                    }
                default:
                    mouseX = me.getX();
                    mouseY = me.getY();
                    break;
            }
        } else {
            mouseX = -1;
            mouseY = -1;
        }
        super.processEvent(e);
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        return compare((File)o1, (File)o2);
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(File f1, File f2) {
        return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
    }

}
