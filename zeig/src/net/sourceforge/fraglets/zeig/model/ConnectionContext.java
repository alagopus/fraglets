/*
 * SharedContext.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 18, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.model;

import java.sql.SQLException;
import java.util.Properties;

import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class ConnectionContext {
    private ConnectionFactory connectionFactory;
    private VersionFactory versionFactory;
    private NodeFactory nodeFactory;
    private int shares;
    
    public ConnectionContext(Properties environment) throws NamingException {
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
                connectionFactory.close();
            }
        }
    }
    
    /**
     * @return
     */
    public ConnectionFactory getConnectionFactory() throws SQLException {
        return connectionFactory;
    }
    
    /**
     * @return
     */
    public NodeFactory getNodeFactory() throws SQLException {
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
    public VersionFactory getVersionFactory() throws SQLException {
        if (versionFactory == null) {
            synchronized(this) {
                if (versionFactory == null) {
                    versionFactory = new VersionFactory(getConnectionFactory());
                }
            }
        }
        return versionFactory;
    }

    public PlainTextFactory getPlainTextFactory() throws SQLException {
        return getNodeFactory().getPlainTextFactory();
    }
}