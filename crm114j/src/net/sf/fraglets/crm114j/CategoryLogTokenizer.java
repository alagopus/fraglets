/*
 * $Id: CategoryLogTokenizer.java,v 1.1 2004-04-04 23:37:11 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 */
package net.sf.fraglets.crm114j;

/**
 * @version $Id: CategoryLogTokenizer.java,v 1.1 2004-04-04 23:37:11 marion Exp $
 */
public class CategoryLogTokenizer extends Tokenizer {

    /**
     * @param buffer
     */
    public CategoryLogTokenizer(char[] buffer) {
        super(buffer);
    }

    /**
     * 
     */
    protected CategoryLogTokenizer() {
        super();
    }

    /**
     * @param str
     */
    public CategoryLogTokenizer(String str) {
        super(str);
    }

    /**
     * @see net.sf.fraglets.crm114j.Tokenizer#next()
     */
    public boolean next() {
        char[] b = getBuffer();
        int o = getOff() + getLen();
        int i = o, l = getMax();
        if (i < l) {
            boolean in = Character.isLetterOrDigit(b[i++]);
            while (i < l && in == Character.isLetterOrDigit(b[i])) {
                i++;
            }
            reset(b, o, i - o, l);
            return true;
        } else {
            reset(b, o, 0, l);
            return false;
        }
    }

}
