/*
 * ConnectionContext.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
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
package net.sourceforge.fraglets.zeig.model;

import java.sql.SQLException;
import java.util.Properties;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.8 $
 */
public class ConnectionContext {
    private ConnectionFactory connectionFactory;
    private VersionFactory versionFactory;
    private NodeFactory nodeFactory;
    private MediaFactory mediaFactory;
    private int shares;
    
    public ConnectionContext(Properties environment) {
        open();
        connectionFactory = new ConnectionFactory(environment);
    }
    
    public ConnectionContext open() {
        shares++;
        return this;
    }
    
    public void close() throws SQLException {
        if (--shares <= 0) {
            ConnectionFactory cf = this.connectionFactory;
            this.connectionFactory = null;
            if (cf != null) {
                cf.close();
            }
        }
    }
    
    /**
     * @return
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
    
    /**
     * @return
     */
    public NodeFactory getNodeFactory() {
        if (nodeFactory == null) {
            synchronized(this) {
                if (nodeFactory == null) {
                    nodeFactory = new NodeFactory(getConnectionFactory());
                }
            }
        }
        return nodeFactory;
    }

    /**
     * @return
     */
    public VersionFactory getVersionFactory() {
        if (versionFactory == null) {
            synchronized(this) {
                if (versionFactory == null) {
                    versionFactory = new VersionFactory(getConnectionFactory());
                }
            }
        }
        return versionFactory;
    }

    /**
     * @return
     */
    public MediaFactory getMediaFactory() {
        if (mediaFactory == null) {
            synchronized(this) {
                if (mediaFactory == null) {
                    mediaFactory = new MediaFactory(getConnectionFactory());
                }
            }
        }
        return mediaFactory;
    }

    public NameFactory getNameFactory() {
        return getNodeFactory().getNameFactory();
    }
    
    public NamespaceFactory getNamespaceFactory() {
        return getNodeFactory().getNamespaceFactory();
    }
    
    public PlainTextFactory getPlainTextFactory() {
        return getNodeFactory().getPlainTextFactory();
    }
    
    public XMLTextFactory getXMLTextFactory() {
        return getNodeFactory().getXMLTextFactory();
    }
}