/*
 * CommandInfo.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.IOException;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class CommandInfo {
    private String commandName;
    private String commandClass;
    
    /**
     * @param commandName the command name
     * @param className the command class
     */
    public CommandInfo(String commandName, String commandClass) {
        this.commandName = commandName;
        this.commandClass = commandClass;
    }
    
    /**
     * @return the command class
     */
    public String getCommandClass() {
        return commandClass;
    }

    /**
     * @return the command class
     */
    public String getCommandName() {
        return commandName;
    }
    
    /**
     * @param handler the data handler to use
     * @return the instantiated command object
     * @throws java.io.IOException propagated, or when instantiation fails
     * @throws java.lang.ClassNotFoundException if the command class cannot be found
     */
    public Object getCommandObject(DataHandler handler)
        throws java.io.IOException, java.lang.ClassNotFoundException {
        Class clazz = Class.forName(getCommandClass());
        try {
            Object result = clazz.newInstance();
            if (CommandObject.class.isAssignableFrom(clazz)) {
                ((CommandObject)result).setCommandContext(getCommandName(), handler);
            }
            return result;
        } catch (InstantiationException e) {
            throw new IOException(e.toString());
        } catch (IllegalAccessException e) {
            throw new IOException(e.toString());
        }
    }
}
