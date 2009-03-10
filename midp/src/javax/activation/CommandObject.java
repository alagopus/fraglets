/*
 * CommandObject.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public interface CommandObject {
    /**
     * @param commandName the command to handle
     * @param handler the data handler to use, or null
     * @throws java.io.IOException propagated
     */
    public void setCommandContext(String commandName, DataHandler handler)
        throws java.io.IOException;
}
