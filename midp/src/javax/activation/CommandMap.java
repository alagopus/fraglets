/*
 * CommandMap.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public abstract class CommandMap {
    /** The default command map. */
    private static CommandMap defaultCommandMap;
    
    /**
     * @return the default command map
     */
    public static CommandMap getDefaultCommandMap() {
        if (defaultCommandMap == null) {
            return defaultCommandMap = new MailcapCommandMap();
        } else {
            return defaultCommandMap;
        }
    }
    
    /**
     * @param commandMap the new default command map, or null to reset
     */
    public static void setDefaultCommandMap(CommandMap commandMap) {
        defaultCommandMap = commandMap;
    }
    
    /**
     * @param mimeType mime type
     * @return preferred commands for <var>mimeType</var>
     */
    public abstract CommandInfo[] getPreferredCommands(String mimeType);
    
    /**
     * @param mimeType mime type
     * @return all commands for <var>mimeType</var>
     */
    public abstract CommandInfo[] getAllCommands(String mimeType);
    
    /**
     * @param mimeType the mime type
     * @param commandName the desired command
     * @return info for the command
     */
    public abstract CommandInfo getCommand(String mimeType, String commandName);
    
    /**
     * @param mimeType the mime type
     * @return the content handler for <var>mimeType</var>
     */
    public abstract DataContentHandler createDataContentHandler(String mimeType);
}
