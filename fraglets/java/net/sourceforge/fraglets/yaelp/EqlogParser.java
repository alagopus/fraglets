/*
 * EqlogParser.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 */

package net.sourceforge.fraglets.yaelp;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

/** This class implements a simple scanner for log files.
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
 * @version $Revision: 1.10 $
 */
public class EqlogParser {
    public static final String PREFIX = "[Sun Apr 01 16:38:57 2001] ";
    
    public static final int TIMESTAMP_START = 1;
    public static final int TIMESTAMP_END = PREFIX.length()-2;
    public static final int LINE_START = PREFIX.length();
    
    protected Reader in;
    
    protected char buffer[];
    
    protected int off;
    
    protected int len;
    
    protected int count;
    
    /** Creates new Parser */
    public EqlogParser(Reader in)
    {
        this.in = in;
        this.buffer = new char[8192];
    }
    
    /** Fill the buffer with more characters. Grow buffer as needed.
     * @return true iff more characters were read.
     * @throws IOException propagated from Reader.read
     */
    protected boolean fill()
        throws IOException
    {
        if (off + len >= buffer.length) {
            if (off > 0) {
                System.arraycopy(buffer, off, buffer, 0, len);
                off = 0;
            } else {
                char grow[] = new char[buffer.length + buffer.length / 2];
                System.arraycopy(buffer, off, grow, 0, len);
                buffer = grow;
                off = 0;
            }
        }
        
        int fill = off + len;
        int n = in.read(buffer, fill, buffer.length - fill);
        if (n > 0) {
            len += n;
            return true;
        } else {
            return false;
        }
    }
    
    public synchronized boolean readLine(Line line)
        throws IOException
    {
        char c;
        int scan = off;
        int last = scan;
        int end = off + len;
        boolean newline = false;
        char buffer[] = this.buffer;
        search: for(;;) {
            if (scan >= end) {
                scan = scan - off;
                if (!fill()) {
                    if (scan == 0) {
                        return false; // end of file
                    }
                    break search;
                }
                scan = off + scan;
                end = off + len;
            }
            switch (buffer[scan++]) {
                case '\n':
                    count += 1;
                    // fall through
                case '\r':
                    newline = true;
                    continue;
                case '[':
                    if (newline) {
                        scan = scan - off - 1;
                        break search;
                    }
                    // fall through
                default:
                    newline = false;
                    last = scan;
                    continue;
            }
        }
        
        int start = off;
        
        len -= scan;
        off += scan;
        scan = last - start;
        
        if (last - start >= LINE_START) {
            String timestamp = new String(buffer, start + TIMESTAMP_START, TIMESTAMP_END - TIMESTAMP_START);
            char rest[] = new char[scan - LINE_START];
            System.arraycopy(buffer, start + LINE_START, rest, 0, scan - LINE_START);
            line.recycle(timestamp, rest);
            return true;
        } else {
            throw new IOException("invalid log file format :'"+new String(buffer, start, scan)+"'");
        }
    }
    
    public static int parseFile(String name, Recognizer recognizer)
        throws IOException
    {
        return parseFile(new File(name), recognizer);
    }
    
    public static int parseFile(File file, Recognizer recognizer)
        throws IOException
    {
        Reader in;
        if (file.getName().endsWith(".gz")) {
            System.out.println("reading compressed "+file+"...");
            in = new InputStreamReader(new GZIPInputStream
                (new FileInputStream(file)));
        } else {
            System.out.println("reading text "+file+"...");
            in = new FileReader(file);
        }
        EqlogParser parser = new EqlogParser(in);
        Line line = new Line(0L, (char[])null);
        int n = 0;
        try {
            for (;;) {
                if (parser.readLine(line)) {
                    n++;
                    recognizer.parse(line);
                } else {
                    break;
                }
            }
            recognizer.setChanged(n > 0 || recognizer.isChanged());
        } catch (IOException ex) {
            throw new IOException(file + ":" + parser.getLine() + ": " + ex.getMessage());
        }
        return parser.getLine();
    }
    
    public static void main(String args[])
    {
        try {
            int opt = 0;
            
            // Parse all log files
            Recognizer recognizer = new Recognizer();
            while (opt < args.length) {
                parseFile(args[opt++], recognizer);
            }
            
            // Print avatars in name order
            Avatar avatars[] = recognizer.getAvatars();
            Comparator nameOrder = new Comparator() {
                public boolean equals(Object other) {
                    return other != null &&
                    other.getClass() == getClass();
                }
                public int compare(Object a, Object b) {
                    return ((Avatar)a).getName().toString()
                    .compareTo(((Avatar)b).getName().toString());
                }
            };
            Arrays.sort(avatars, nameOrder);
            for (int i = 0; i < avatars.length; i++) {
                System.out.println(avatars[i]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** Getter for property line.
     * @return Value of property line.
     */
    public int getLine() {
        return this.count;
    }
    
}
