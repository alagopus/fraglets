/*
 * $Id: CSSFile.java,v 1.1 2004-03-21 21:52:26 marion Exp $
 * Copyright (C) 2004 Klaus Rennecke, all rights reserved.
 * Algorithm from CRM114 Copyright 2001-2004  William S. Yerazunis.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.1
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.fraglets.crm114j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @version $Id: CSSFile.java,v 1.1 2004-03-21 21:52:26 marion Exp $
 */
public class CSSFile {
    public static final int BYTES_PER_BUCKET = 12;

    public static final long DEFAULT_BUCKET_COUNT = 1048577;

    public static final int INITIAL = 0xDEADBEEF;

    public static final int HASH_INDEX = 0;
    public static final int KEY_INDEX = 1;
    public static final int VALUE_INDEX = 2;

    public static final int VALUE_MAX = 32767;

    private FileChannel fc;
    private FileLock fl;

    private IntBuffer ib;
    private MappedByteBuffer mb;

    public CSSFile(FileChannel fc) {
        this.fc = fc;
    }

    public CSSFile(String fileName) throws IOException {
        RandomAccessFile ras = new RandomAccessFile(fileName, "rw");
        fc = ras.getChannel();
        if (fc.size() == 0) {
            inflate(ras, DEFAULT_BUCKET_COUNT * BYTES_PER_BUCKET);
        }
    }

    public static CSSFile create(File file, long size) throws IOException {
        RandomAccessFile ras = new RandomAccessFile(file, "rw");
        inflate(ras, size);
        return new CSSFile(ras.getChannel());
    }

    protected static void inflate(RandomAccessFile ras, long size)
        throws IOException {
        byte[] blank = new byte[10000]; // intentionally not 2^x
        int len = blank.length;
        while (size > len) {
            ras.write(blank);
            size -= len;
        }
        if (size > 0) {
            ras.write(blank, 0, (int)size);
        }
    }

    protected final IntBuffer getBuffer() throws IOException {
        if (fl == null) {
            fl = fc.lock();
        }
        if (ib == null) {
//            fl = fc.lock();
            mb = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size());
            ib = mb.asIntBuffer();
        }
        return ib;
    }

    public final int getSize() throws IOException {
        if (mb != null) {
            return mb.capacity() / BYTES_PER_BUCKET;
        } else {
            return (int)(fc.size() / BYTES_PER_BUCKET);
        }
    }
    
    protected final void releaseBuffer() throws IOException {
        if (mb != null) {
            mb.force(); // ok so we dont really release -- :-)
        }
        if (fl != null) {
            fl.release();
            fl = null;
        }
    }
    
    public void close() throws IOException {
        FileChannel fc = this.fc;
        this.fc = null;
        releaseBuffer();
        if (fc != null) {
            fc.close();
        }
    }

    public final void fillBucket(int index, int[] bucket) throws IOException {
        IntBuffer buffer = getBuffer();
        buffer.position(index);
        buffer.get(bucket);
    }

    public final void dumpBucket(int index, int[] bucket)
        throws IOException {
        IntBuffer buffer = getBuffer();
        buffer.position(index);
        buffer.put(bucket);
    }

    public final int findBucket(Polynomial p, int[] bucket) throws IOException {
        int h1 = p.getHash();
        int h2 = p.getKey();

        if (mb == null) {
            getBuffer();
        }
        int count = getSize();
        int index = (h1 & 0x7fffffff) % count;
        if (index == 0) {
            index = 1;
        }

        int start = index;
        fillBucket(index, bucket);
        while (!(bucket[HASH_INDEX] == h1 && bucket[KEY_INDEX] == h2)) {
            if (bucket[KEY_INDEX] == 0) {
                break; // found an empty slot
            }
            index++;
            if (index >= count) {
                index = 1;
            }
            if (start == index) {
                // wrapped around
                return -1;
            } else {
                fillBucket(index, bucket);
            }
        }
        return index;
    }

    /**
     * @param tok token input
     * @param sense learning direction, 1 to learn and -1 to unlearn
     */
    public void learn(Tokenizer tok, int sense) throws IOException {
        int hfsize = getSize();
        Polynomial p = new SBPHPolynomial();

        outer: while (tok.next()) {
            p.push(StringHash.hash(tok));
            while (p.fold()) {
                int[] bucket = new int[3];
                int index = findBucket(p, bucket);
                if (index < 0) {
                    System.err.println(
                        "Your program is stuffing too many "
                            + "features into this size .css file.  "
                            + "Adding any more features is "
                            + "impossible in this file."
                            + "You are advised to build a larger "
                            + ".css file and merge your data into "
                            + "it.");
                    break outer;
                }

                bucket[HASH_INDEX] = p.getHash();
                bucket[KEY_INDEX] = p.getKey();

                if (sense > 0 && bucket[VALUE_INDEX] >= VALUE_MAX - sense) {
                    bucket[VALUE_INDEX] = VALUE_MAX;
                } else if (sense < 0 && bucket[VALUE_INDEX] <= -sense) {
                    bucket[VALUE_INDEX] = 0;
                } else {
                    bucket[VALUE_INDEX] += sense;
                }
                dumpBucket(index, bucket);
            }
        }

        releaseBuffer();
    }

    public static Statistics[] classify(CSSFile[] categories, Tokenizer tok) throws IOException {
        int classes = categories.length;
        Statistics[] result = new Statistics[classes];
        Polynomial p = new SBPHPolynomial();
        int totalFeatures = 0;
        
        for (int scan = classes; --scan >= 0;) {
            result[scan] = new Statistics(categories[scan]);
        }
        
        while (tok.next()) {
            p.push(StringHash.hash(tok));
            while (p.fold()) {
                int h1 = p.getHash();
                int h2 = p.getKey();
                totalFeatures += 1;
                int htf = 0;
                int[] bucket = new int[3];
                int weight = Statistics.SUPER_MARKOV_WEIGHTS[p.getIter()];
                for (int scan = classes; --scan >= 0;) {
                    int index = categories[scan].findBucket(p, bucket);
                    if (bucket[CSSFile.HASH_INDEX] == h1 && bucket[CSSFile.KEY_INDEX] == h2) {
                        int local = bucket[CSSFile.VALUE_INDEX] * weight;
                        htf = (int)(htf + result[scan].addHit(local));
                    }
                }
                for (int scan = classes; --scan >= 0;) {
                    result[scan].updateHitsToFeature(htf);
                }
                double renorm = 0.0;
                for (int scan = classes; --scan >= 0;) {
                    renorm += result[scan].getPtcByPltc();
                }
                for (int scan = classes; --scan >= 0;) {
                    result[scan].renormalize1(renorm);
                }
                renorm = 0.0;
                for (int scan = classes; --scan >= 0;) {
                    renorm += result[scan].getPtc();
                }
                for (int scan = classes; --scan >= 0;) {
                    result[scan].renormalize2(renorm);
                }
            }
        }
        
        return result;
    }

    public static void main(String args[]) {
        try {
            if (args.length >= 3 && "learn".equals(args[0])) {
                FileReader input = new FileReader(args[1]);
                BufferedReader br = new BufferedReader(input);
                CSSFile css = new CSSFile(args[2]);
                String line;
                while ((line = br.readLine()) != null) {
                    css.learn(new Tokenizer(line), 1);
                    System.out.println(args[2] + ": " + line);
                }
                input.close();
                css.close();
            } else if (args.length >= 2 && "classify".equals(args[0])) {
                FileReader input = new FileReader(args[1]);
                BufferedReader br = new BufferedReader(input);
                CSSFile[] classes = new CSSFile[args.length - 2];
                for (int i = 0; i < classes.length; i++) {
                    classes[i] = new CSSFile(args[i + 2]);
                }
                String line;
                while ((line = br.readLine()) != null) {
                    Statistics[] s = classify(classes, new Tokenizer(line));
                    int best = Statistics.bestOf(s);
                    System.out.println(args[best + 2] + ": " + line);
                }
                input.close();
                for (int i = 0; i < classes.length; i++) {
                    classes[i].close();
                }
            } else if (args.length >= 2 && "filter1".equals(args[0])) {
                FileReader input = new FileReader(args[1]);
                BufferedReader br = new BufferedReader(input);
                CSSFile[] classes = new CSSFile[args.length - 2];
                for (int i = 0; i < classes.length; i++) {
                    classes[i] = new CSSFile(args[i + 2]);
                }
                String line;
                while ((line = br.readLine()) != null) {
                    Statistics[] s = classify(classes, new Tokenizer(line));
                    if (Statistics.bestOf(s) == 0) {
                        System.out.println(line);
                    }
                }
                input.close();
                for (int i = 0; i < classes.length; i++) {
                    classes[i].close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
