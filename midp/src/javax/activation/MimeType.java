/*
 * MimeType.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

/**
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class MimeType {
    private String mimeType;
    
    public MimeType() {
        this("application/octet-stream");
    }
    
    /**
     * @param raw the raw mime type
     */
    public MimeType(String raw) {
        this.mimeType = raw;
    }
    
    /**
     * @param primaryType the primary type
     * @param subType the sub type
     */
    public MimeType(String primaryType, String subType) {
        this(primaryType + '/' + (subType == null ? "*" : subType));
    }
    
    /**
     * @return base type without parameters
     */
    public String getBaseType() {
        int mark = mimeType.indexOf(';', mimeType.indexOf('/') + 1);
        return (mark < 0) ? mimeType : mimeType.substring(0, mark);
    }
    
    /**
     * @param name parameter name
     * @return the value of parameter <var>name</var>, or null
     */
    public String getParameter(String name) {
        return MimeTypeParameterList.getParameter(name, mimeType);
    }
    
    /**
     * @return the parameter list of this mime type
     */
    public MimeTypeParameterList getParameters() {
        return new MimeTypeParameterList(this);
    }
    
    /**
     * @return the primary type
     */
    public String getPrimaryType() {
        return getPrimaryType(mimeType);
    }
    
    /**
     * @return the sub type
     */
    public String getSubType() {
        return getSubType(mimeType);
    }
    
    /**
     * @param other another mime type
     * @return true iff the mime types are equivalent
     */
    public boolean match(MimeType other) {
        return isMimeTypeEqual(mimeType, other.mimeType);
    }
    
    /**
     * @param mimeType another mime type representation
     * @return true iff this mime type is equivalent to <var>mimeType</var>
     * @throws MimeTypeParseException never
     */
    public boolean match(String mimeType) throws MimeTypeParseException {
        return isMimeTypeEqual(mimeType, mimeType);
    }
    
    /**
     * @param name parameter name
     */
    public void removeParameter(String name) {
        mimeType = MimeTypeParameterList.removeParameter(name, mimeType);
    }
    
    /**
     * @param name parameter name
     * @param value parameter value
     */
    public void setParameter(String name, String value) {
        mimeType = MimeTypeParameterList.setParameter(name, value, mimeType);
    }
    
    /**
     * @param primaryType the primary type
     * @throws MimeTypeParseException for invalid primary types
     */
    public void setPrimaryType(String primaryType) throws MimeTypeParseException {
        if (primaryType.indexOf('/') >= 0 || primaryType.indexOf(';') >= 0) {
            throw new MimeTypeParseException(primaryType);
        }
        int slash = mimeType.indexOf('/');
        if (slash >= 0) {
            mimeType = primaryType + mimeType.substring(slash);
        } else {
            mimeType = primaryType;
        }
    }
    
    /**
     * @param subType the sub type
     * @throws MimeTypeParseException for invalid sub types
     */
    public void setSubType(String subType) throws MimeTypeParseException {
        if (subType.indexOf('/') >= 0 || subType.indexOf(';') >= 0) {
            throw new MimeTypeParseException(subType);
        }
        int semi = mimeType.indexOf(';');
        if (semi >= 0) {
            mimeType = getPrimaryType() + '/' + subType + mimeType.substring(semi);
        } else {
            mimeType = getPrimaryType() + '/' + subType;
        }
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return mimeType;
    }

    /**
     * @param mimeType mime type
     * @return the content type
     */
    public static String getHumanPresentableNameFor(String mimeType) {
        int semi = mimeType.indexOf(';');
        return semi >= 0 ? mimeType.substring(0, semi) : mimeType;
    }

    /**
     * @param representationClass
     * @return
     */
    public static String getMimeTypeFor(Class representationClass) {
        return "application/x-java-serialized-object; class=" + representationClass.getName();
    }

    /**
     * @param mimeType a mime type
     * @return the primary type
     */
    public static String getPrimaryType(String mimeType) {
        int mark = mimeType.indexOf('/');
        if (mark < 0) {
            mark = mimeType.indexOf(';');
        }
        return (mark < 0) ? mimeType : mimeType.substring(0, mark);
    }

    /**
     * @param mimeType a mime type
     * @return the sub type
     */
    public static String getSubType(String mimeType) {
        int slash = mimeType.indexOf('/') + 1;
        if (slash == 0) {
            return "*";
        } else {
            int semi = mimeType.indexOf(';', slash);
            if (semi < 0) {
                return mimeType.substring(slash);
            } else {
                return mimeType.substring(slash, semi);
            }
        }
    }

    /**
     * @param a first mime type
     * @param b second mime type
     * @return true iff <var>a</var> and <var>b</var> are equivalent
     */
    public static boolean isMimeTypeEqual(String a, String b) {
        if (!getPrimaryType(a).equals(getPrimaryType(b))) {
            return false;
        }
        a = getSubType(a);
        b = getSubType(b);
        return a.equals(b) || a.equals("*") || b.equals("*");
    }
}
