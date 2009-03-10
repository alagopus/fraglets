/*
 * MimeTypeParseException.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class MimeTypeParseException extends Exception {

    public MimeTypeParseException() {
        super();
    }

    /**
     * @param message the exception message
     */
    public MimeTypeParseException(String message) {
        super(message);
    }

}
