/*
 * EventDecoder.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 14:28
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
public class EventDecoder extends EventCodec {
    /** Decoder for UTF-8 stream. */
    protected UTF8Decoder decoder;
    
    /** Creates a new instance of EventDecoder */
    public EventDecoder(UTF8Decoder decoder) {
        this.decoder = decoder;
    }
    
    /** Creates a new instance of EventDecoder */
    public EventDecoder() {
        this.decoder = new UTF8Decoder();
    }
    
    public void setBuffer(byte buffer[]) {
        decoder.setBuffer(buffer);
    }
    
    public boolean hasNext() {
        return decoder.available() > 0;
    }
    
    public int next() {
        int result;
        while ((result = decoder.decodeUCS4()) == STRING_WORD) {
            continue;
        }
        return result;
    }
    
    protected Word insert(int ucs4[]) {
        int code = nextLiteral++;
        Word result = new Word(code, ucs4, 0, ucs4.length, null);
        ensureCapacity(code);
        return alphabet[code] = result;
    }
    
}
