/*
 * ConnectionContext.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 18, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.SQLException;
import java.util.Properties;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.7 $
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