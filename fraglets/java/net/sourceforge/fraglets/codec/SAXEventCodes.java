/*
 * SAXEventCodes.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 15:35
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  */

package net.sourceforge.fraglets.codec;

/**
 *
 * @author  marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public interface SAXEventCodes {
    public static final int CHARACTERS_WORD = 1;
    public static final int START_DOCUMENT_WORD = 2;
    public static final int END_DOCUMENT_WORD = 3;
    public static final int START_ELEMENT_WORD = 4;
    public static final int END_ELEMENT_WORD = 5;
    public static final int PROCESSING_INSTRUCTION_WORD = 6;
    public static final int IGNORABLE_WHITESPACE_WORD = 7;
    public static final int NULL_WORD = 8;
    public static final int NEXT_WORD = 9;
}
