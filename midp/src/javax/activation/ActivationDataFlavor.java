/*
 * ActivationDataFlavor.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.io.InputStream;
import java.io.Reader;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class ActivationDataFlavor {
    
    private String humanPresentableName;
    private String mimeType;
    private Class representationClass;
    
    /**
     * Same as ActivationDataFlavor(representationClass, null, humanPresentableName)
     * @param representationClass the representation class
     * @param humanPresentableName the human presentable name
     */
    public ActivationDataFlavor(Class representationClass, String humanPresentableName) {
        this(representationClass, null, humanPresentableName);
    }
    
    /**
     * @param representationClass the representation class
     * @param mimeType the mime type
     * @param humanPresentableName the human presentable name
     */
    public ActivationDataFlavor(Class representationClass, String mimeType, String humanPresentableName) {
        this.representationClass = representationClass;
        this.mimeType = mimeType;
        this.humanPresentableName = humanPresentableName;
    }
    
    /**
     * Same as ActivationDataFlavor(null, mimeType, humanPresentableName)
     * @param mimeType the mime type
     * @param humanPresentableName the human presentable name
     */
    public ActivationDataFlavor(String mimeType, String humanPresentableName) {
        this(null, mimeType, humanPresentableName);
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        return (other instanceof ActivationDataFlavor)
            ? match((ActivationDataFlavor)other)
            : false;
    }
    
    /**
     * @return the default representation class
     */
    public Class getDefaultRepresentationClass() {
        return InputStream.class;
    }
    
    /**
     * @return the name of the default representation class
     */
    public String getDefaultRepresentationClassAsString() {
        return getDefaultRepresentationClass().getName();
    }
    
    /**
     * @return the human presentable name.
     */
    public String getHumanPresentableName() {
        return humanPresentableName != null ? humanPresentableName
            : (humanPresentableName = MimeType.getHumanPresentableNameFor(getMimeType()));
    }
    
    /**
     * @return the mime type.
     */
    public String getMimeType() {
        return mimeType != null ? mimeType
            : (mimeType = MimeType.getMimeTypeFor(representationClass));
    }
    
    /**
     * @param name name of the parameter
     * @return parameter value, or null
     */
    public String getParameter(String name) {
        return MimeTypeParameterList.getParameter(name, getMimeType());
    }
    
    /**
     * @return the primary type.
     */
    public String getPrimaryType() {
        return MimeType.getPrimaryType(getMimeType());
    }
    
    /**
     * @return the representation class
     */
    public Class getRepresentationClass() {
        return representationClass != null ? representationClass
            : (representationClass = getDefaultRepresentationClass());
    }
    
    /**
     * @return the sub type.
     */
    public String getSubType() {
        return MimeType.getSubType(getMimeType());
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getRepresentationClass().hashCode() ^
            getPrimaryType().hashCode() ^
            getSubType().hashCode();
    }
    
    /**
     * @return false; MIDP does not support java.util.List
     */
    public boolean isFlavorJavaFileListType() {
        return false;
    }
    
    /**
     * @return false; MIDP does not support Remote
     */
    public boolean isFlavorRemoteObjectType() {
        return false;
    }
    
    /**
     * @return false; MIDP does not support Serializable
     */
    public boolean isFlavorSerializedObjectType() {
        return false;
    }
    
    /**
     * @return true iff the primary type is text
     */
    public boolean isFlavorTextType() {
        return getPrimaryType().equals("text");
    }
    
    /**
     * @param other the flavor to compare to
     * @return true iff the mime types are equivalent
     */
    public boolean isMimeTypeEqual(ActivationDataFlavor other)
    {
        return isMimeTypeEqual(other.getMimeType());
    }

    /**
     * @param other the mime type to compare to
     * @return true iff the mime types are equivalent
     */
    public boolean isMimeTypeEqual(String mimeType)
    {
        return MimeType.isMimeTypeEqual(getMimeType(), mimeType);
    }
    
    /**
     * @return true iff the mime type is equivalent to application/x-java-serialized-object
     */
    public boolean isMimeTypeSerializedObject() {
        return isMimeTypeEqual("application/x-java-serialized-object");
    }
    
    /**
     * @return false; MIDP does not support java.nio
     */
    public boolean isRepresentationClassByteBuffer() {
        return false;
    }
    
    /**
     * @return false; MIDP does not support java.nio
     */
    public boolean isRepresentationClassCharBuffer() {
        return false;
    }
    
    /**
     * @return true iff the representation class is a java.io.InputStream
     */
    public boolean isRepresentationClassInputStream() {
        return InputStream.class.isAssignableFrom(getRepresentationClass());
    }
    
    /**
     * @return true iff the representation class is a java.io.InputStream
     */
    public boolean isRepresentationClassReader() {
        return Reader.class.isAssignableFrom(getRepresentationClass());
    }
    
    /**
     * @return false; MIDP does not support Remote
     */
    public boolean isRepresentationClassRemote() {
        return false;
    }
    
    /**
     * @return false; MIDP does not support Serializable
     */
    public boolean isRepresentationClassSerializable() {
        return false;
    }
    
    /**
     * @param other the flavor to compare to
     * @return true iff the representation classes are equal
     *         and the mime types are equivalent.
     */
    public boolean match(ActivationDataFlavor other) {
        return getRepresentationClass().equals(other.getRepresentationClass())
            && isMimeTypeEqual(other);
    }
    
    /**
     * @param humanPresentableName a human presentable name
     */
    public void setHumanPresentableName(String humanPresentableName) {
        this.humanPresentableName = humanPresentableName;
    }
    
    /**
     * @return a string representation
     */
    public String toString() {
        return getClass().getName() + '('
            + getRepresentationClass().getName() + ','
            + getMimeType() + ','
            + getHumanPresentableName() + ')';
    }

}
