/*
 * $Id: CVSHistoryTest.java,v 1.1 2004-02-29 23:26:54 marion Exp $
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.cruisecontrol.CruiseControlException;
import net.sourceforge.cruisecontrol.Modification;
import junit.framework.TestCase;

/**
 * Test case for CVSHistory.
 * 
 * @author  Klaus Rennecke
 * @version $Revision: 1.1 $
 */
public class CVSHistoryTest extends TestCase
{
    private CVSHistory cvsHistory;
    
    /**
     * 
     */
    public CVSHistoryTest()
    {
        super();
    }

    /**
     * @param name
     */
    public CVSHistoryTest(String name)
    {
        super(name);
    }
    
    public void testNoHistory()
    {
        cvsHistory = new CVSHistory();
        
        try
        {
            cvsHistory.validate();
            fail("failed to detect unset history file");
        }
        catch (CruiseControlException e)
        {
            assertEquals(
                "error message",
                "The 'historyfilename' attribute is required on cvshistory",
                e.getMessage());
        }
    }
    
    public void testBroadRange()
    {
        try
        {
            cvsHistory.validate();
        }
        catch (CruiseControlException e)
        {
            fail("failed to validate: " + e);
        }
        
        List list = cvsHistory.getModifications(new Date(0), new Date());
        assertTrue("list not empty", list.size() > 0);
        
        Iterator i = list.iterator();
        assertEquals("modification type 1", "modified", ((Modification)i.next()).type);
        assertEquals("modification type 2", "modified", ((Modification)i.next()).type);
        assertEquals("modification type 3", "added", ((Modification)i.next()).type);
        assertEquals("modification type 4", "deleted", ((Modification)i.next()).type);
        assertEquals("modification type 5", "modified", ((Modification)i.next()).type);
        assertEquals("modification type 6", "deleted", ((Modification)i.next()).type);
    }
    
    public void testModule() throws ParseException
    {
        cvsHistory.setModule("build/logs");
        
        try
        {
            cvsHistory.validate();
        }
        catch (CruiseControlException e)
        {
            fail("failed to validate: " + e);
        }
        
        List list = cvsHistory.getModifications(new Date(0), new Date());
        assertTrue("list size 1", list.size() == 1);
        
        Modification modification = (Modification)list.get(0);
        assertEquals("file name", "plug.txt", modification.fileName);
        assertEquals("user name", "kre", modification.userName);
        assertEquals("revision", "1.2", modification.revision);
        assertEquals("modification type", "deleted", modification.type);
        
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        Date expect = sfd.parse("2004/02/29 15:54:06 UTC");
        assertEquals("timestamp", expect, modification.modifiedTime);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        cvsHistory = new CVSHistory();
        String filename = getClass().getResource("testfile.txt").toString();
        if (filename.startsWith("file:/"))
        {
            filename = filename.substring("file:/".length());
        }
        cvsHistory.setHistoryFileName(filename);
    }

}
