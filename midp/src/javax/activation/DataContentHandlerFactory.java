/*
 * DataContentHandlerFactory.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public interface DataContentHandlerFactory {
    /**
     * @param mimeType mime type
     * @return the handler for <var>mimeType</var>, or null
     */
    public DataContentHandler createDataContentHandler(String mimeType);
}
