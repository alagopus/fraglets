/*
 * MimeTypeParameterList.java $Revision: 1.1 $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package javax.activation;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Mime type parameter list abstraction. This implementation shares all data
 * with MimeType.
 * @since 08.02.2004
 * @author kre
 * @version $Revision: 1.1 $
 */
public class MimeTypeParameterList {
    
    /**
     * Parameter name enumeration.
     * @since 08.02.2004
     * @author kre
     * @version $Revision: 1.1 $
     */
    public static class ParameterNames implements Enumeration {
        
        private String mimeType;
        private String name;
        private int pos;
        
        /**
         * @param mimeType mime type with parameters
         */
        public ParameterNames(String mimeType) {
            this.mimeType = mimeType;
            this.pos = mimeType.indexOf(';') + 1;
            advance();
        }
        
        /**
         * Advance to next parameter name.
         */
        private void advance() {
            int end = mimeType.length();
            while (pos > 0) {
                int mark = pos;
                while (mark < end && mimeType.charAt(mark) == ' ') {
                    mark += 1; // skip spaces
                }
                pos = mimeType.indexOf(';', pos) + 1;
                int qual = mimeType.indexOf('=', mark);
                if (qual > 0 && (qual < pos || pos == 0)) {
                    name = mimeType.substring(mark, qual);
                    return;
                }
            }
            name = null;
        }

        /**
         * @see java.util.Enumeration#hasMoreElements()
         */
        public boolean hasMoreElements() {
            return name != null;
        }

        /**
         * @see java.util.Enumeration#nextElement()
         */
        public Object nextElement() {
            if (name != null) {
                String result = name;
                advance();
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
    
    private String mimeType;
    
    /**
     * Create empty parameter list.
     */
    public MimeTypeParameterList() {
        mimeType = ";";
    }
    
    /**
     * @param mimeType the MimeType for which to represent the parameter list
     */
    protected MimeTypeParameterList(MimeType mimeType) {
        this.mimeType = mimeType.toString();
    }
    
    /**
     * @param parameterList string representation of parameter list
     * @throws MimeTypeParseException never
     */
    public MimeTypeParameterList(String parameterList) throws MimeTypeParseException {
        mimeType = ";" + parameterList;
    }
    
    /**
     * @param name parameter name
     * @return value of the parameter <var>name</var>, or null
     */
    public String get(String name) {
        return getParameter(name, mimeType);
    }
    
    public Enumeration getNames() {
        return new ParameterNames(mimeType);
    }
    
    /**
     * @return true iff this list is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * @param name parameter name to remove
     */
    public void remove(String name) {
        mimeType = removeParameter(name, mimeType);
    }
    
    /**
     * @param name parameter name
     * @param value parameter value
     */
    public void set(String name, String value) {
        mimeType = setParameter(name, value, mimeType);
    }
    
    /**
     * @return the number of parameters in this list
     */
    public int size() {
        return getParameterListSize(mimeType);
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        int semi = mimeType.indexOf(';') + 1;
        return (semi > 0) ? mimeType.substring(semi) : "";
    }

    /**
     * @param name name of the parameter
     * @param mimeType mime type with parameters
     * @return parameter value, or null
     */
    public static String getParameter(String name, String mimeType) {
        int semi = mimeType.indexOf(';') + 1;
        int end = mimeType.length();
        int need = name.length();
        while (semi > 0) {
            int mark = semi;
            while (mark < end && mimeType.charAt(mark) == ' ') {
                mark += 1; // skip spaces
            }
            semi = mimeType.indexOf(';', mark) + 1;
            int qual = mimeType.indexOf('=', mark);
            if ((qual - mark) == need && mimeType.regionMatches(true, mark, name, 0, qual - mark)) {
                if (semi > qual) {
                    return mimeType.substring(qual + 1, semi - 1);
                } else {
                    return mimeType.substring(qual + 1);
                }
            }
        }
        return null;
    }
    
    /**
     * @param mimeType mime type with parameters
     * @return number of parameters on <var>mimeType</var>
     */
    public static int getParameterListSize(String mimeType) {
        int semi = mimeType.indexOf(';') + 1;
        int count = 0;
        while (semi > 0) {
            int qual = mimeType.indexOf('=', semi);
            semi = mimeType.indexOf(';', semi) + 1;
            if (qual > 0 && (qual < semi || semi == 0)) {
                count += 1; // skip empty ;; sequences
            }
        }
        return count;
    }
    
    /**
     * @param name the name of the parameter to remove
     * @param mimeType the mime type
     * @return <var>mimeType</var> with the parameter <var>name</var> removed
     */
    public static String removeParameter(String name, String mimeType) {
        int semi = mimeType.indexOf(';') + 1;
        int end = mimeType.length();
        int need = name.length();
        StringBuffer b = null;
        while (semi > 0) {
            int mark = semi;
            while (mark < end && mimeType.charAt(mark) == ' ') {
                mark += 1; // skip spaces
            }
            semi = mimeType.indexOf(';', mark) + 1;
            int qual = mimeType.indexOf('=', mark);
            if ((qual - mark) == need && mimeType.regionMatches(true, mark, name, 0, qual - mark)) {
                if (b == null) {
                    int part = mimeType.lastIndexOf(';', mark);
                    b = new StringBuffer(mimeType.substring(0, part));
                }
            } else {
                if (b != null) {
                    int part = mimeType.lastIndexOf(';', mark);
                    if (semi > part) {
                        b.append(mimeType.substring(part, semi - 1));
                    } else {
                        b.append(mimeType.substring(part));
                    }
                }
            }
        }
        return b == null ? mimeType : b.toString();
    }
    
    /**
     * @param name parameter name
     * @param value parameter value
     * @param mimeType old mime type with parameter list
     * @return new mime type with parameter <var>name</var> set to <var>value</var>
     */
    public static String setParameter(String name, String value, String mimeType) {
        return removeParameter(name, mimeType) + ";" + name + "=" + value;
    }
    
}
