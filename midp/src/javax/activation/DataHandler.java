/*
 * DataHandler.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataContentHandler.UnsupportedFlavorException;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class DataHandler {
    private DataSource dataSource;
    private CommandMap commandMap;
    private Object dataObject;
    private String mimeType;
    
    private static DataContentHandlerFactory factory;
    
    /**
     * @param dataSource the data source to handle
     */
    public DataHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * @param obj the data object to handle
     * @param mimeType the mime type
     */
    public DataHandler(Object obj, String mimeType) {
        this.dataObject = obj;
        this.mimeType = mimeType;
    }
    
    /**
     * @param url the url where to fetch the data
     */
    public DataHandler(String url) {
        this(new URLDataSource(url));
    }
    
    /**
     * @return the data source
     */
    public DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * @return name of the data source, or null
     */
    public String getName() {
        return dataSource == null ? null : dataSource.getName();
    }
    
    /**
     * @return mime type of the data source
     */
    public String getContentType() {
        return dataSource == null ? mimeType : dataSource.getContentType();
    }
    
    /**
     * Returns an input stream to read from the converted data, either
     * from the data source, or from a buffer created by writing the
     * object using an appropriate data content handler.
     * 
     * @return an input stream reading from the data conversion
     * @throws IOException propagated
     * @throws UnsupportedDataTypeException when no data content handler can be found
     */
    public InputStream getInputStream() throws IOException {
        if (dataSource != null) {
            return dataSource.getInputStream();
        } else {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            try {
                writeTo(b);
                return new ByteArrayInputStream(b.toByteArray());
            } finally {
                b.close();
            }
        }
    }
    
    /**
     * @param out the stream to write to
     * @throws IOException propagated
     */
    public void writeTo(OutputStream out) throws IOException {
        if (dataSource != null) {
            InputStream in = dataSource.getInputStream();
            byte b[] = new byte[512];
            int n;
            while ((n = in.read(b)) > 0) {
                out.write(b, 0, n);
            }
        } else {
            createDataContentHandler().writeTo(dataObject, mimeType, out);
        }
    }
    
    /**
     * @return the output stream from the data source, or null
     * @throws IOException propagated
     */
    public OutputStream getOutputStream() throws IOException {
        return dataSource == null ? null : dataSource.getOutputStream();
    }
    
    /**
     * @return the flavors available
     */
    public ActivationDataFlavor[] getTransferDataFlavors() {
        try {
            return createDataContentHandler().getTransferDataFlavors();
        } catch (UnsupportedDataTypeException e) {
                return new ActivationDataFlavor[] {
                    new ActivationDataFlavor(dataSource == null
                        ? dataObject.getClass()
                        : InputStream.class,
                        getContentType(), null)
                };
        }
    }
    
    /**
     * @param flavor the requested flavor
     * @return true iff <var>flavor</var> is supported
     */
    public boolean isDataFlavorSupported(ActivationDataFlavor flavor) {
        ActivationDataFlavor list[] = getTransferDataFlavors();
        int scan = list.length;
        while (--scan >= 0) {
            if (flavor.match(list[scan])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param flavor the desired flavor
     * @return the transfer data
     * @throws UnsupportedFlavorException of the flavor is not supported
     * @throws IOException propagated
     */
    public Object getTransferData(ActivationDataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
        if (dataSource != null) {
            return createDataContentHandler().getTransferData(
                flavor,
                dataSource);
        } else {
            if (!flavor
                .getRepresentationClass()
                .isAssignableFrom(dataObject.getClass())) {
                throw new UnsupportedFlavorException(flavor);
            }
            if (!flavor.isMimeTypeEqual(getContentType())) {
                throw new UnsupportedFlavorException(flavor);
            }
            return dataObject;
        }
    }
    
    /**
     * @param commandMap the new command map to use
     */
    public void setCommandMap(CommandMap commandMap) {
        this.commandMap = commandMap;
    }
    
    /**
     * @return the preferred commands for this content type
     */
    public CommandInfo[] getPreferredCommands() {
        return getCommandMap().getPreferredCommands(getContentType());
    }
    
    /**
     * @return all commands for this content type
     */
    public CommandInfo[] getAllCommands() {
        return getCommandMap().getAllCommands(getContentType());
    }
    
    /**
     * @param commandName the name of the desired command
     * @return the info for the command for this content type
     */
    public CommandInfo getCommand(String commandName) {
        return getCommandMap().getCommand(getContentType(), commandName);
    }
    
    /**
     * @return the content in its preferred form
     * @throws IOException propagated
     */
    public Object getContent() throws IOException {
        if (dataSource != null) {
            try {
                return createDataContentHandler().getContent(dataSource);
            } catch (UnsupportedDataTypeException e) {
                return dataSource.getInputStream();
            }
        } else {
            return dataObject;
        }
    }
    
    /**
     * @param commandInfo the command info to instantiate
     * @return the instantiated bean
     */
    public Object getBean(CommandInfo commandInfo) {
        try {
            return commandInfo.getCommandObject(this);
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    /**
     * @param newFactory the new factory
     */
    public static void setDataContentHandlerFactory(DataContentHandlerFactory newFactory) {
        factory = newFactory;
    }
    
    private CommandMap getCommandMap() {
        return commandMap == null ? CommandMap.getDefaultCommandMap() : commandMap;
    }
    
    private DataContentHandler createDataContentHandler() throws UnsupportedDataTypeException {
        DataContentHandler dch = getFactory().createDataContentHandler(getContentType());
        if (dch == null) {
            throw new UnsupportedDataTypeException(getContentType());
        } else {
            return dch;
        }
    }

    private static DataContentHandlerFactory getFactory() {
        return factory;
    }
}
