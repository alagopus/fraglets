/*
 * $Id: ANTPublisher.java,v 1.1 2004-05-06 13:16:10 marion Exp $
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.cruisecontrol.CruiseControlException;
import net.sourceforge.cruisecontrol.Publisher;
import net.sourceforge.cruisecontrol.builders.AntBuilder;

import org.jdom.Element;

/**
 * <p>A publisher using ANT. This is a publisher that will invoke ANT
 * for publishing. All properties from the log file are propagated to
 * the ANT invocation.</p>
 * @version $Id: ANTPublisher.java,v 1.1 2004-05-06 13:16:10 marion Exp $
 */
public class ANTPublisher extends AntBuilder implements Publisher {

    /**
     * Publish for the given element from a log file. This will invoke
     * the builder with all properties from the log file.
     * @see net.sourceforge.cruisecontrol.Publisher#publish(org.jdom.Element)
     */
    public void publish(Element log) throws CruiseControlException {
        build(getBuildPropertiesMap(log));
    }

    /**
     * Get the properties map for publishing the given element.
     * @param cruisecontrolLog a log element.
     * @return the map of properties.
     * @throws CruiseControlException
     */
    protected Map getBuildPropertiesMap(Element log) throws CruiseControlException {
        Map buildProperties = new HashMap();
        Iterator propertyIterator =
            log.getChild("info").getChildren("property").iterator();
        while (propertyIterator.hasNext()) {
            Element property = (Element) propertyIterator.next();
            String name = property.getAttributeValue("name");
            String value = property.getAttributeValue("value");
            if (name != null && value != null) {
                buildProperties.put(name, value);
            }
        }
        return buildProperties;
    }
    
}
