/*
 * zeigURLConnection.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 29, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author unknown
 */
public class zeigURLConnection extends URLConnection {
    private InputStream stream;
    /**
     * @param u
     * @param stream
     */
    public zeigURLConnection(URL u, InputStream stream) {
        super(u);
        this.stream = stream;
    }

    /**
     * @see java.net.URLConnection#connect()
     */
    public void connect() throws IOException {
        connected = true;
    }

    /**
     * @see java.net.URLConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        if (!connected) connect();
        return stream;
    }

}
