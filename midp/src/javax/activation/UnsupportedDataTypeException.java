/*
 * UnsupportedDataTypeException.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.IOException;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class UnsupportedDataTypeException extends IOException {

    /**
     * 
     */
    public UnsupportedDataTypeException() {
        super();
    }

    /**
     * @param message the exception message
     */
    public UnsupportedDataTypeException(String message) {
        super(message);
    }

}
