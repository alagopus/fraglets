/*
 * URLDataSource.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class URLDataSource implements DataSource {
    private int mode;
    private String url;
    private Connection connection;
    
    /**
     * @param url the url to be encapsulated
     */
    public URLDataSource(String url) {
        this.url = url;
    }

    /**
     * @see javax.activation.DataSource#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        return ((InputConnection)getConnection(Connector.READ)).openInputStream();
    }

    /**
     * @see javax.activation.DataSource#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        return ((OutputConnection)getConnection(Connector.WRITE)).openOutputStream();
    }

    /**
     * @see javax.activation.DataSource#getContentType()
     */
    public String getContentType() {
        try {
            Connection c = getConnection(Connector.READ);
            if (c instanceof ContentConnection) {
                return ((ContentConnection)c).getType();
            }
        } catch (IOException e) {
        }
        return "application/octet-stream";
    }

    /**
     * @see javax.activation.DataSource#getName()
     */
    public String getName() {
        return url;
    }

    /**
     * @param mode desired mode
     * @return a connection to url according to mode
     * @throws IOException propagated
     */
    private Connection getConnection(int mode) throws IOException {
        if (connection != null) {
            if (this.mode == Connector.READ_WRITE || this.mode == mode) {
                return connection;
            }
            Connection old = connection;
            connection = null;
            old.close();
            // avoid further re-open
            mode = Connector.READ_WRITE;
        }
        this.mode = mode;
        return connection = Connector.open(url, mode);
    }
}
