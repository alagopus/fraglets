/*
 * DataSource.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public interface DataSource {
    /**
     * @return a new input stream representing the data
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException;
    
    /**
     * @return a new output stream to write the representation to
     * @throws IOException
     */
    public OutputStream getOutputStream() throws IOException;
    
    /**
     * @return the mime type
     */
    public String getContentType();
    
    /**
     * @return the name, domain dependent
     */
    public String getName();
}
