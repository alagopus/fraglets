/*
 * $Id: CVSHistory.java,v 1.2 2004-02-29 23:38:45 marion Exp $
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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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
 * @version $Revision: 1.2 $
 */
public class CVSHistory implements SourceControl
{
    /** The properties set for ANT. */
    private Hashtable properties = new Hashtable();
    
    /** The property name for the property to set on modifications. */
    private String onModifiedName;
    
    /** The property name for the property to set on deletions. */
    private String onDeletedName;
    
    /** The file name of the history file to read. */
    private String historyFileName;
    
    /** The module name to check for. */
    private String module;

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
    public List getModifications(Date lastBuild, Date now)
    {
        List result = new ArrayList();
        try
        {
            FileReader fin = new FileReader(historyFileName);
            try
            {
                BufferedReader in = new BufferedReader(fin);
                String line;
                while ((line = in.readLine()) != null)
                {
                    parseModification(line, result, lastBuild, now);
                }
                
                if (onModifiedName != null && result.size() > 0)
                {
                    properties.put(onModifiedName, "true");
                }
                if (onDeletedName != null && deletionSeen)
                {
                    properties.put(onDeletedName, "true");
                }
            }
            finally
            {
                fin.close();
            }
        }
        catch (IOException e)
        {
            log.error("Failed to read history file", e);
        }
        return result;
    }

    /**
     * @param line the line to parse
     * @param result the list where to put results
     * @param lastBuild lower bound
     * @param now upper bound
     */
    private void parseModification(String line, List result, Date lastBuild, Date now)
    {
        if (line.length() >= MINIMUM_HISTORY_LINE)
        {
            String type;
            switch(line.charAt(0))
            {
                case 'A':
                    type = "added";
                    break;
                case 'M':
                    type = "modified";
                    break;
                case 'R':
                    type = "deleted";
                    break;
                default:
                    // other entries ignored
                    return;
            }
            
            String timestamp = line.substring(1, TIMESTAMP_END);
            long time =
                Long.parseLong(timestamp, TIMESTAMP_RADIX) * TIME_MULTIPLIER;
            if (time < lastBuild.getTime())
            {
                return; // too old
            }
            else if (time > now.getTime())
            {
                return; // too young
            }
            
            try
            {
                StringTokenizer tok =
                    new StringTokenizer(line.substring(TIMESTAMP_END + 1), "|", true);
                String userName = tok.nextToken();
                tok.nextToken(); // delimiter
                tok.nextToken(); // working directory
                tok.nextToken(); // delimiter
                String folderName = tok.nextToken();
                
                if (module != null)
                {
                    if (!folderName.startsWith(module))
                    {
                        return; // does not match
                    }
                    else if (folderName.length() > module.length()
                        && folderName.charAt(module.length()) != '/')
                    {
                        return; // different folder with equal prefix
                    }
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
                
                if (type.equals("deleted"))
                {
                    deletionSeen = true;
                }
                result.add(modification);
            }
            catch (NoSuchElementException e)
            {
                log.warn("Invalid history format: \"" + line + "\"");
            }
        }
    }

    /**
     * @see net.sourceforge.cruisecontrol.SourceControl#validate()
     */
    public void validate() throws CruiseControlException
    {
        if (historyFileName == null)
        {
            throw new CruiseControlException(
                "The 'historyfilename' attribute is required on cvshistory");
        }
        else if (!new File(historyFileName).isFile()) {
            throw new CruiseControlException(
                "History file \"" + historyFileName + "\" does not exist!");
        }
    }

    /**
     * @see net.sourceforge.cruisecontrol.SourceControl#getProperties()
     */
    public Hashtable getProperties()
    {
        return properties;
    }

    /**
     * Set the property name for the property to set on modification.
     * @see net.sourceforge.cruisecontrol.SourceControl#setProperty(java.lang.String)
     */
    public void setProperty(String property)
    {
        onModifiedName = property;
    }

    /**
     * Set the property name for the property to set on deletions.
     * @see net.sourceforge.cruisecontrol.SourceControl#setPropertyOnDelete(java.lang.String)
     */
    public void setPropertyOnDelete(String property)
    {
        onDeletedName = property;
    }

    /**
     * @return the history file name
     */
    public String getHistoryFileName()
    {
        return historyFileName;
    }

    /**
     * @param string the new history file name
     */
    public void setHistoryFileName(String string)
    {
        historyFileName = string;
    }

    /**
     * @return the module name
     */
    public String getModule()
    {
        return module;
    }

    /**
     * @param string the new module name
     */
    public void setModule(String string)
    {
        module = string;
    }

}
