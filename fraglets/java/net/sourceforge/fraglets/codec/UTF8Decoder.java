/*
 * UTF8Decoder.java
 *
 * Created on 10. Juli 2002, 04:06
 */

package net.sourceforge.fraglets.codec;

/**
 *
 * @author  marion@users.sourceforge.net
 */
public class UTF8Decoder {
    private byte buffer[];
    
    /** Offset into buffer. */
    private int off;
    
    /** End of buffer. */
    private int end;
    
    /** Byte value table. */
    private static int value[];
    
    /** Code length table. */
    private static int length[];
    
    /** Creates a new instance of UTF8Decoder */
    public UTF8Decoder() {
        initTables();
    }

    public void setBuffer(byte buffer[]) {
        setBuffer(buffer, 0, buffer.length);
    }
    
    public void setBuffer(byte buffer[], int off, int len) {
        this.buffer = buffer;
        this.off = off;
        this.end = off + len;
    }
    
    public int[] decodeUCS4(int buffer[], int off, int len) {
        if (buffer == null) {
            buffer = new int[len];
            off = 0; // ignored
        }
        while (--len >= 0) {
            buffer[off++] = decodeUCS4();
        }
        return buffer;
    }
    
    public char[] decodeUCS2(char buffer[], int off, int len) {
        if (buffer == null) {
            buffer = new char[len];
            off = 0; // ignored
        }
        while (--len >= 0) {
            buffer[off++] = (char)decodeUCS4();
        }
        return buffer;
    }
    
    public String decodeString(int len) {
        return new String(decodeUCS2(null, 0, len));
    }
    
    public final int decodeUCS4() {
        int datum = buffer[off++] & 0xff;
        int result = value[datum];
        switch (length[datum]) {
            case 6:
                result = result << 6 | (buffer[off++] & 0x3f); 
            case 5:
                result = result << 6 | (buffer[off++] & 0x3f); 
            case 4:
                result = result << 6 | (buffer[off++] & 0x3f); 
            case 3:
                result = result << 6 | (buffer[off++] & 0x3f); 
            case 2:
                result = result << 6 | (buffer[off++] & 0x3f); 
            case 1:
                return result;
            default:
                throw new IllegalArgumentException
                    ("format error: illegal byte: "+datum);
        }
    }
    
    public static void main(String[] args) {
        int count = 1000;
        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }
        char sample[] = new char[count];
        char check[] = new char[count];
        byte test[] = null;
        String sampleCheck = null;
        try {
            long local = 0;
            long system = 0;
            java.util.Random random = new java.util.Random(count);
            UTF8Encoder encoder = new UTF8Encoder();
            UTF8Decoder decoder = new UTF8Decoder();
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < count; j++) {
                    char value = random.nextInt(100) <= 2 ?
                    (char)random.nextInt(0x10000) :
                        (char)random.nextInt(0x100);
                        if (value >= 0xd800 && value <= 0xdfff) {
                            value &= 0xefff; // don't trust UTF-16 system decoding
                            //                        if (j + 1 >= count) {
                            //                            value &= 0xefff;
                            //                        } else {
                            //                            if (value > 0xdbff) {
                            //                                value &= 0xfbff;
                            //                            }
                            //                            sample[j++] = value;
                            //                            value = (char)(random.nextInt(0x400) + 0xdc00);
                            //                        }
                        }
                        sample[j] = value;
                }
                encoder.setSize(0);
                long t0 = System.currentTimeMillis();
                encoder.encodeUCS2(sample, 0, sample.length);
                long t1 = System.currentTimeMillis();
                test = encoder.toByteArray();
                decoder.setBuffer(test);
                long t2 = System.currentTimeMillis();
                check = decoder.decodeUCS2(check, 0, check.length);
                long t3 = System.currentTimeMillis();
                local += t1 - t0;
                system += t3 - t2;
                if (sample.length != check.length) {
                    throw new RuntimeException
                    ("length differs: "+test.length+" != "+check.length);
                } else {
                    int scan = sample.length;
                    while (--scan >= 0) {
                        if (sample[scan] != check[scan]) {
                            throw new RuntimeException
                            ("result differs at ["+scan+"]: "
                            + (int)sample[scan] + " != "
                            + (int)check[scan]);
                        }
                    }
                }
            }
            System.out.println("encode: "+((double)local / (double)count)+"ms");
            System.out.println("decode: "+((double)system / (double)count)+"ms");
        } catch (Exception ex) {
            ex.printStackTrace();
            UTF8Encoder.print("sample", sample, System.err);
            UTF8Encoder.print("test  ", test, System.err);
            UTF8Encoder.print("check ", check, System.err);
        }
    }
    
    private static void initTables() {
        if (length != null) {
            return;
        }
        synchronized (UTF8Decoder.class) {
            if (length != null) {
                return;
            }
            length = new int[256];
            value = new int[256];
            for (int i = 0; i <= 0x7f; i++) {
                length[i] = 1;
                value[i] = i;
            }
            for (int i = 0x80; i < 0xc0; i++) {
                length[i] = 0; // invalid
                value[i] = 0;
            }
            for (int i = 0xc0; i <= 0xdf; i++) {
                length[i] = 2;
                value[i] = i & 0x1f;
            }
            for (int i = 0xe0; i <= 0xef; i++) {
                length[i] = 3;
                value[i] = i & 0xf;
            }
            for (int i = 0xf0; i <= 0xf7; i++) {
                length[i] = 4;
                value[i] = i & 0x7;
            }
            for (int i = 0xf8; i <= 0xfb; i++) {
                length[i] = 5;
                value[i] = i & 0x3;
            }
            for (int i = 0xfc; i <= 0xfd; i++) {
                length[i] = 6;
                value[i] = i & 0x1;
            }
            for (int i = 0xfe; i <= 0xff; i++) {
                length[i] = 0; // invalid
                value[i] = 0;
            }
        }
    }
}
