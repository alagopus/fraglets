/*
 * EventEncoder.java
 * Copyright (C) 2002, Klaus Rennecke.
 * Created on 10. Juli 2002, 10:39
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

import java.util.Arrays;

/**
 * An event encoder is able to encode strings and integer literals into
 * a compact UTF-8 stream.
 * @author  marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class EventEncoder extends EventCodec {
    private final double MIN_LOAD = 0.4;
    private final double MAX_LOAD = 0.6;
    
    /** Reverse mapping. */
    private int reverse[];
    
    /** Stepping prime. */
    private int stepPrime;
    
    /** The UTF8 encoder. */
    private UTF8Encoder encoder;
    
    /** Creates a new instance of EventEncoder */
    public EventEncoder() {
        encoder = new UTF8Encoder();
    }
    
    public void clear() {
        encoder.setSize(0);
    }
    
    public byte[] getUTF8() {
        return encoder.toByteArray();
    }
    
    public int declareWord(int ucs4[]) {
        int now = nextLiteral;
        Word word = insert(ucs4);
        if (now < nextLiteral) {
            // new word
            encoder.encodeUCS4(STRING_WORD);
            encoder.encodeUCS4(ucs4.length);
            encoder.encodeUCS4(ucs4, 0, ucs4.length);
        }
        return word.getCode();
    }
    
    public int declareWord(String ucs2) {
        int now = nextLiteral;
        Word word = insert(ucs2);
        if (now < nextLiteral) {
            // new word
            int ucs4[] = word.getUCS4();
            encoder.encodeUCS4(STRING_WORD);
            encoder.encodeUCS4(ucs4.length);
            encoder.encodeUCS4(ucs4, 0, ucs4.length);
        }
        return word.getCode();
    }
    
    public void encodeWord(int ucs4[]) {
        encodeWord(declareWord(ucs4));
    }
    
    public void encodeWord(String ucs2) {
        encodeWord(declareWord(ucs2));
    }
    
    public void encodeWord(int code) {
        encoder.encodeUCS4(code);
    }
    
    protected synchronized void ensureCapacity(int code) {
        super.ensureCapacity(code);
        if (getLoad() > MAX_LOAD) {
            rehash();
        }
    }
    
    protected double getLoad() {
        if (alphabet == null) {
            return 0.0;
        } else if (reverse == null) {
            return Double.MAX_VALUE;
        } else {
            return (double)alphabet.length / (double)reverse.length;
        }
    }
    
    protected void rehash() {
        int scan = alphabet.length;
        int p = nextPrime((int)(scan / MIN_LOAD));
        int b = nextPrime(p);
        Arrays.fill(reverse = new int[b], 0);
        stepPrime = p;
        while (--scan >= 0) {
            Word word = alphabet[scan];
            if (word != null) {
                insert(word);
            }
        }
    }
    
    protected Word insert(Word word) {
        int ucs4[] = word.getUCS4();
        return lookup(ucs4, 0, ucs4.length, word, 0, null);
    }
    
    protected Word insert(int ucs4[]) {
        Word result = lookup(ucs4, 0, ucs4.length, null, nextLiteral, null);
        if (result.getCode() == nextLiteral) {
            nextLiteral += 1;
        }
        return result;
    }
    
    protected Word insert(String ucs2) {
        int ucs4[] = toUCS4(ucs2);
        Word result = lookup(ucs4, 0, ucs4.length, null, nextLiteral, ucs2);
        if (result.getCode() == nextLiteral) {
            nextLiteral += 1;
        }
        return result;
    }
    
    /** Hash lookup using double hashing open addressing. */
    protected Word lookup(int ucs4[], int off, int len, Word word, int code, String ucs2) {
        // code defaults to word.code
        if (word != null) {
            code = word.getCode();
        }
        
        // make sure we have not to resize later
        if (alphabet == null) {
            if (code > 0) {
                ensureCapacity(code);
            } else {
                return null;
            }
        } else if (code >= alphabet.length) {
            ensureCapacity(code);
        }
        
        // start hash lookup
        int b = reverse.length;
        int k = Math.abs(hashCode(ucs4, off, len));
        int s = stepPrime - (k % stepPrime);
        int f = k % b;
        for(int i = 1; i < b; i++) {
            Word probe = alphabet[reverse[f]];
            if (probe == null) {
                if (word != null) {
                    return alphabet[reverse[f] = word.getCode()] = word;
                } else if (code > 0) {
                    return alphabet[reverse[f] = code] =
                        new Word(code, ucs4, off, len, ucs2);
                } else {
                    return null;
                }
            } else if (probe.equals(ucs4, off, len)) {
                return probe;
            } else {
                f = (f + s) % b;
            }
        }
        throw new RuntimeException("internal error - hash table full");
    }
    
    /** Simplistic prime search. */
    public static int nextPrime(int n) {
        if (n <= 2) {
            return 2;
        }
        n |= 1; // ensure it is odd
        int probe = 3;
        while (probe * probe <= n) {
            if (n % probe == 0) {
                n += 2;
                probe = 3;
            } else {
                probe = nextPrime(probe + 2);
            }
        }
        return n;
    }
}
