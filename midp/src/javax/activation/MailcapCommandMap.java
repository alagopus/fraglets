/*
 * MailcapCommandMap.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;

/**
 * This simplified implementation does not support launching of applications.
 * All mailcap entries other than the ones starting with x-java- are ignored.
 * 
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class MailcapCommandMap extends CommandMap {
    
    /** (mime type, command info) tuples, in reverse order of preference. */
    private Vector commandMap;
    
    /**
     * Create an empty mailcap command map.
     */
    public MailcapCommandMap() {
    }
    
    /**
     * @param url the location of the mailcap file
     * @throws IOException propagated
     */
    public MailcapCommandMap(String url) throws IOException {
        this(((InputConnection)Connector.open(url)).openInputStream());
    }
    
    /**
     * @param in the stream of the mailcap file
     */
    public MailcapCommandMap(InputStream in) {
        try {
            readMailcap(in);
            reverseOrder();
        } catch (IOException e) {
            // must ignore this, API requires no exceptions thrown
        }
    }
    
    /**
     * Reverse the order so that we can search from the end.
     */
    private void reverseOrder() {
        Vector m = commandMap;
        int high = m.size() - 2;
        int low = 0;
        while (high > low) {
            Object type = m.elementAt(low);
            Object info = m.elementAt(low + 1);
            m.setElementAt(m.elementAt(high), low);
            m.setElementAt(m.elementAt(high + 1), low + 1);
            m.setElementAt(type, high);
            m.setElementAt(info, high + 1);
            low += 2;
            high -= 2;
        }
    }

    /**
     * @param line
     */
    public void addMailcap(String line) {
        int semi1 = line.indexOf(';');
        int semi2 = line.indexOf(';', semi1 + 1);
        if (semi2 > semi1) {
            int mark = semi2 + 1;
            int end = line.length();
            String type = null;
            Object commands = null;
            while (mark > 0) {
                while (mark < end && line.charAt(mark) == ' ') {
                    mark += 1; // skip spaces
                }
                int start = mark;
                int qual = line.indexOf('=', start);
                mark = line.indexOf(';', start) + 1;
                if (qual > start && (qual < mark || mark == 0) &&
                    line.regionMatches(false, mark, "x-java-", 0, 7)) {
                    if (type == null) {
                        type = line.substring(0, semi1).trim();
                    }
                    String name = line.substring(mark + 7, qual).trim();
                    String clazz = line.substring(qual + 1, mark - 1).trim();
                    CommandInfo info = new CommandInfo(name, clazz);
                    if (commands instanceof Vector) {
                        ((Vector)commands).addElement(info);
                    } else if (commands != null) {
                        Vector newCommands = new Vector();
                        newCommands.addElement(commands);
                        newCommands.addElement(info);
                    } else {
                        commands = info;
                    }
                }
            }
            if (commands != null) {
                commandMap.addElement(type);
                if (commands instanceof Vector) {
                    Vector oldCommands = (Vector)commands;
                    int scan = oldCommands.size();
                    CommandInfo newCommands[] = new CommandInfo[scan];
                    while (--scan >= 0) {
                        newCommands[scan] = (CommandInfo)oldCommands.elementAt(scan);
                    }
                    commandMap.addElement(newCommands);
                } else {
                    commandMap.addElement(commands);
                }
            }
        }
    }
    
    /**
     * @param in stream to read
     */
    private void readMailcap(InputStream in) throws IOException {
        StringBuffer line = new StringBuffer();
        byte b[] = new byte[512];
        boolean nl = true;
        boolean co = false;
        boolean q = false;
        int n;
        while ((n = in.read(b)) > 0) {
            for (int i = 0; i < n; i++) {
                char c = (char)b[i];
                switch (c) {
                    case '\r':
                    case '\n':
                        if (!q) {
                            nl = true;
                            co = false;
                            if (line.length() > 0) {
                                addMailcap(line.toString());
                                line.setLength(0);
                            }
                        }
                        continue;
                    case '\\':
                        if (!q && !co) {
                            q = true;
                            nl = false;
                            continue;
                        }
                        break;
                    case '#':
                        if (nl) {
                            co = true;
                            nl = false;
                            continue;
                        }
                        break;
                }
                if (!co) {
                    line.append(c);
                    q = false;
                    nl = false;
                }
            }
        }
        // missing newline at end?
        if (line.length() > 0) {
            addMailcap(line.toString());
        }
        in.close();
    }

    /**
     * @see javax.activation.CommandMap#getPreferredCommands(java.lang.String)
     */
    public CommandInfo[] getPreferredCommands(String mimeType) {
        Vector m = commandMap;
        int scan = m.size();
        int i = 0;
        while ((scan -= 2) >= 0) {
            if (MimeType.isMimeTypeEqual(mimeType, (String)commandMap.elementAt(scan))) {
                return append(null, m.elementAt(scan));
            }
        }
        return null;
    }

    /**
     * @see javax.activation.CommandMap#getAllCommands(java.lang.String)
     */
    public CommandInfo[] getAllCommands(String mimeType) {
        Vector m = commandMap;
        int scan = m.size();
        CommandInfo result[] = null;
        int i = 0;
        while ((scan -= 2) >= 0) {
            if (MimeType.isMimeTypeEqual(mimeType, (String)commandMap.elementAt(scan))) {
                result = append(result, m.elementAt(scan));
            }
        }
        return result;
    }
    
    /**
     * @param array the array to extend, or null
     * @param more more elements to add, either CommandInfo or CommandInfo[]
     * @return a resized array with <var>more</var> added at end
     */
    private static CommandInfo[] append(CommandInfo array[], Object more) {
        int old = array == null ? 0 : array.length;
        int size = (more instanceof CommandInfo) ? 1 : ((CommandInfo[])more).length;
        CommandInfo grow[] = new CommandInfo[old + size];
        if (old > 0) {
            System.arraycopy(array, 0, grow, 0, array.length);
        }
        if (more instanceof CommandInfo) {
            grow[array.length] = (CommandInfo)more;
        } else {
            int end = grow.length;
            CommandInfo moreInfo[] = (CommandInfo[])more;
            for (int i = 0, j = moreInfo.length; j < end;) {
                grow[j++] = moreInfo[i++];
            }
        }
        return grow;
    }

    /**
     * @see javax.activation.CommandMap#getCommand(java.lang.String, java.lang.String)
     */
    public CommandInfo getCommand(String mimeType, String commandName) {
        Vector m = commandMap;
        int scan = m.size();
        while ((scan -= 2) >= 0) {
            if (MimeType.isMimeTypeEqual(mimeType, (String)m.elementAt(scan))) {
                Object info = m.elementAt(scan);
                if (info instanceof CommandInfo) {
                    CommandInfo single = (CommandInfo)info;
                    if (commandName.equals(single.getCommandName())) {
                        return single;
                    }
                } else {
                    CommandInfo infos[] = (CommandInfo[])info;
                    for (int i = 0; i < infos.length; i++) {
                        if (commandName.equals(infos[i].getCommandName())) {
                            return infos[i];
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @see javax.activation.CommandMap#createDataContentHandler(java.lang.String)
     */
    public DataContentHandler createDataContentHandler(String mimeType) {
        CommandInfo info = getCommand(mimeType, "content-handler");
        if (info == null) {
            // try fallback
            info = getCommand("application/octet-stream", "content-handler");
        }
        try {
            return info == null ? null : (DataContentHandler)info.getCommandObject(null);
        } catch (Exception e) {
            return null;
        }
    }

}
