/*
 * DataContentHandler.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public interface DataContentHandler {
    /**
     * @since 08.02.2004
     * @author kre
     * @version $Revision: 1.1 $
     */
    public static class UnsupportedFlavorException extends Exception {
        public UnsupportedFlavorException(ActivationDataFlavor flavor) {
            super(flavor.toString());
        }
    }
    
    /**
     * @return an array of acceptable flavors, ordered by preference
     */
    public ActivationDataFlavor[] getTransferDataFlavors();
    
    /**
     * @param flavor the desired flavor
     * @param source the data to be converted
     * @return an object representing the data from <var>source</var>
     * @throws UnsupportedFlavorException if <var>flavor</var> is not supported
     * @throws IOException propagated
     */
    public Object getTransferData(ActivationDataFlavor flavor, DataSource source)
        throws UnsupportedFlavorException, IOException;
        
    /**
     * Equivalent to getTransferData(getTransferDataFlavors()[0], source).
     * @param source the data to be converted
     * @return an object representing the data from <var>source</var>
     * @throws IOException propagated
     */
    public Object getContent(DataSource source) throws IOException;
    
    /**
     * @param obj object to write
     * @param mimeType desired mime type
     * @param out stream to write to
     * @throws IOException propagated
     */
    public void writeTo(Object obj, String mimeType, OutputStream out)
        throws IOException;
}
