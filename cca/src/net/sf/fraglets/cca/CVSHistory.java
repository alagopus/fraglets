/*
 * $Id: CVSHistory.java,v 1.4 2004-03-01 21:26:15 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.sf.fraglets.cca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sourceforge.cruisecontrol.CruiseControlException;
import net.sourceforge.cruisecontrol.Modification;
import net.sourceforge.cruisecontrol.SourceControl;

import org.apache.log4j.Logger;

/**
 * Perform modification check based on a plain history file.
 * 
 * @author  Klaus Rennecke
 * @version $Revision: 1.4 $
 */
public class CVSHistory implements SourceControl {
    /** The properties set for ANT. */
    private Hashtable properties = new Hashtable();

    /** The property name for the property to set on modifications. */
    private String onModifiedName;

    /** The property name for the property to set on deletions. */
    private String onDeletedName;

    /** The file name of the history file to read. */
    private String historyFileName;

    /** The URL where to fetch the history. */
    private String historyUrl;

    /** The module name to check for. */
    private List modules = new ArrayList();

    /** Logger for this class. */
    private static Logger log = Logger.getLogger(CVSHistory.class);

    /** Flag indicating that a deletion was seen. */
    private boolean deletionSeen;

    /** Minimum line length for a valid history entry. */
    private static final int MINIMUM_HISTORY_LINE = 10;

    /** Time conversion factor from un*x time to java time. */
    private static final int TIME_MULTIPLIER = 1000;

    /** End index of the timestamp. */
    private static final int TIMESTAMP_END = 9;

    /** Radix of the history timestamp. */
    private static final int TIMESTAMP_RADIX = 16;

    /**
     * Get the modifications recorded in the history file.
     * @param lastBuild the last build
     * @param now the check time
     */
    public List getModifications(Date lastBuild, Date now) {
        List result = new ArrayList();
        try {
            Reader fin;
            if (historyFileName != null) {
                fin = new FileReader(historyFileName);
            } else {
                URLConnection connection = new URL(historyUrl).openConnection();
                String encoding = connection.getContentEncoding();
                String charset;
                if (encoding == null) {
                    fin = new InputStreamReader(connection.getInputStream());
                } else {
                    fin =
                        new InputStreamReader(
                            connection.getInputStream(),
                            encoding);
                }
            }
            try {
                BufferedReader in = new BufferedReader(fin);
                String line;
                while ((line = in.readLine()) != null) {
                    parseModification(line, result, lastBuild, now);
                }

                if (onModifiedName != null && result.size() > 0) {
                    properties.put(onModifiedName, "true"); //$NON-NLS-1$
                }
                if (onDeletedName != null && deletionSeen) {
                    properties.put(onDeletedName, "true"); //$NON-NLS-1$
                }
            } finally {
                fin.close();
            }
        } catch (IOException e) {
            log.error(Messages.getString(
                "CVSHistory.Failed_to_read_history_file"), e); //$NON-NLS-1$
        }
        return result;
    }

    /**
     * @param line the line to parse
     * @param result the list where to put results
     * @param lastBuild lower bound
     * @param now upper bound
     */
    private void parseModification(
        String line,
        List result,
        Date lastBuild,
        Date now) {
        if (line.length() >= MINIMUM_HISTORY_LINE) {
            String type;
            switch (line.charAt(0)) {
                case 'A' :
                    type = "added"; //$NON-NLS-1$
                    break;
                case 'M' :
                    type = "modified"; //$NON-NLS-1$
                    break;
                case 'R' :
                    type = "deleted"; //$NON-NLS-1$
                    break;
                default :
                    // other entries ignored
                    return;
            }

            String timestamp = line.substring(1, TIMESTAMP_END);
            long time =
                Long.parseLong(timestamp, TIMESTAMP_RADIX) * TIME_MULTIPLIER;
            if (time < lastBuild.getTime()) {
                return; // too old
            } else if (time > now.getTime()) {
                return; // too young
            }

            try {
                    StringTokenizer tok = new StringTokenizer(
                        line.substring(TIMESTAMP_END + 1), "|", //$NON-NLS-1$
                        true);
                String userName = tok.nextToken();
                tok.nextToken(); // delimiter
                tok.nextToken(); // working directory
                tok.nextToken(); // delimiter
                String folderName = tok.nextToken();

                if (!isInModules(folderName)) {
                    return; // not in modules
                }

                Modification modification = new Modification();
                modification.type = type;
                modification.modifiedTime = new Date(time);
                modification.userName = userName;
                modification.folderName = folderName;
                tok.nextToken(); // delimiter
                modification.revision = tok.nextToken();
                tok.nextToken(); // delimiter
                modification.fileName = tok.nextToken();

                if (type.equals("deleted")) { //$NON-NLS-1$
                    deletionSeen = true;
                }
                result.add(modification);
            } catch (NoSuchElementException e) {
                log.warn(Messages.getString(
                    "CVSHistory.Invalid_history_format") //$NON-NLS-1$
                    + line + "\""); //$NON-NLS-1$
            }
        }
    }

    /**
     * Check of the given folder name is in the set of modules configured.
     * @param folderName the folder name to check.
     * @return true iff the folder is in at least one of the modules.
     */
    public boolean isInModules(String folderName) {
        int scan = modules.size();
        if (scan == 0) {
            return true; // no modules set
        }

        while (--scan >= 0) {
            String moduleName = ((Module)modules.get(scan)).getName();
            if (!folderName.startsWith(moduleName)) {
                continue; // does not match
            } else if (
                folderName.length() > moduleName.length()
                    && folderName.charAt(moduleName.length()) != '/') {
                continue; // different folder with equal prefix
            } else {
                return true;
            }
        }

        return false; // not found
    }

    /**
     * @see net.sourceforge.cruisecontrol.SourceControl#validate()
     */
    public void validate() throws CruiseControlException {
        if (historyFileName == null) {
            if (historyUrl == null) {
                throw new CruiseControlException(Messages.getString(
                    "CVSHistory.history_required")); //$NON-NLS-1$
            } else {
                try {
                    new URL(historyUrl).toExternalForm();
                } catch (MalformedURLException e) {
                    throw new CruiseControlException(Messages.getString(
                        "CVSHistory.Malformed_historyurl"), e); //$NON-NLS-1$
                }
            }
        } else if (historyUrl != null) {
            throw new CruiseControlException(Messages.getString(
                "CVSHistory.Either_historyfilename_or_historyurl")); //$NON-NLS-1$
        } else if (!new File(historyFileName).isFile()) {
            // changed to warning to allow bootstrapping the history
            log.warn(Messages.getString("CVSHistory.History_file") //$NON-NLS-1$
                + historyFileName
                + Messages.getString("CVSHistory.does_not_exist")); //$NON-NLS-1$
        }
    }

    /**
     * @see net.sourceforge.cruisecontrol.SourceControl#getProperties()
     */
    public Hashtable getProperties() {
        return properties;
    }

    /**
     * Set the property name for the property to set on modification.
     * @see net.sourceforge.cruisecontrol.SourceControl#setProperty(java.lang.String)
     */
    public void setProperty(String property) {
        onModifiedName = property;
    }

    /**
     * Set the property name for the property to set on deletions.
     * @see net.sourceforge.cruisecontrol.SourceControl#setPropertyOnDelete(java.lang.String)
     */
    public void setPropertyOnDelete(String property) {
        onDeletedName = property;
    }

    /**
     * @return the history file name
     */
    public String getHistoryFileName() {
        return historyFileName;
    }

    /**
     * @param string the new history file name
     */
    public void setHistoryFileName(String string) {
        historyFileName = string;
    }

    /**
     * @return the history url
     */
    public String getHistoryUrl() {
        return historyUrl;
    }

    /**
     * @param string the new history url
     */
    public void setHistoryUrl(String string) {
        historyUrl = string;
    }

    /**
     * @param string the new module name
     */
    public void setModule(String string) {
        createModule().setName(string);
    }

    /**
     * Create a new sub-element for a module.
     * @return the new module sub-element.
     */
    public Module createModule() {
        Module result = new Module();
        modules.add(result);
        return result;
    }

    /**
     * Bean implementation for the module sub-element.
     * @since 01.03.2004
     * @author Klaus Rennecke
     * @version $Revision: 1.4 $
     */
    public static class Module {

        /** The module name. */
        private String name;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param string the new name
         */
        public void setName(String string) {
            name = string;
        }

    }
}
