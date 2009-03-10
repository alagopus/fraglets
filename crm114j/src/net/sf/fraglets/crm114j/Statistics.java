/*
 * $Id: Statistics.java,v 1.1 2004-03-21 21:52:26 marion Exp $
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


/**
 * @version $Id: Statistics.java,v 1.1 2004-03-21 21:52:26 marion Exp $
 */
public class Statistics {

    public static final int ENTROPIC_WEIGHTS[] = {
        1, // 0
        2, // 1
        2, // 2
        3, // 3
        2, // 4
        3, // 5
        3, // 6
        4, // 7
        2, // 8
        3, // 9
        3, // 10 
        4, // 11
        3, // 12
        4, // 13
        4, // 14
        5, // 15
    };

    public static final int[] MARKOV_WEIGHTS = {
        1, // 0
        2, // 1
        2, // 2
        4, // 3
        2, // 4
        4, // 5
        4, // 6
        8, // 7
        2, // 8
        4, // 9
        4, // 10 
        8, // 11
        4, // 12
        8, // 13
        8, // 14
        16, // 15
    };

    public static final int[] SUPER_MARKOV_WEIGHTS = {
        1, // 0
        4, // 1
        4, // 2
        16, // 3
        4, // 4
        16, // 5
        16, // 6
        64, // 7
        4, // 8
        16, // 9
        16, // 10 
        64, // 11
        16, // 12
        64, // 13
        64, // 14
        256 // 15
    };
    
    public static final double LOCAL_PROB_DENOM = 16.0;
    
    /** total counts for feature normalize */
    int fcounts = 0;

    //    /** corpus correction factors */
    //    double cpcorr = 0; DISABLED

    /** absolute hit counts */
    double hits = 0;

    /** absolute hit counts */
    int totalhits = 0;

    /** priori probability */
    double ptc = 0.5;

    /** current local probability of this class */
    double pltc = 0.5;
    
    CSSFile css;
    
    public Statistics(CSSFile css) {
        this.css = css;
    }
    
    public double addHit(int local) {
        // TODO: check: this was totalhits + l, in contrast to totalhits + 1
        // This might be a typing error unseen because of unluky identifier naming
        totalhits = totalhits + local;
        return hits = local;
    }
    
    public void updateHitsToFeature(int htf) {
        pltc = 0.5 +
          (( hits - (htf - hits)) 
             / (LOCAL_PROB_DENOM * (htf + 1.0)));
    }
    
    public double getPtcByPltc() {
        return ptc * pltc;
    }
    
    public void renormalize1(double factor) {
        ptc = (ptc * pltc) / factor;
        // prevent underflow
        if (ptc < 10 * Double.MIN_VALUE) {
            ptc = 10 * Double.MIN_VALUE;
        } 
    }

    public void renormalize2(double factor) {
        ptc = ptc / factor;
        // prevent underflow
        if (ptc < 10 * Double.MIN_VALUE) {
            ptc = 10 * Double.MIN_VALUE;
        } 
    }
    
    /**
     * @return
     */
    public double getPtc() {
        return ptc;
    }

    public static int bestOf(Statistics[] s) {
        try {
            int scan = s.length;
            double bestValue = s[--scan].getPtc();
            int bestIndex = scan;
            while (--scan >= 0) {
                double nextValue = s[scan].getPtc();
                if (nextValue > bestValue) {
                    bestValue = nextValue;
                    bestIndex = scan;
                }
            }
            return bestIndex;
        } catch (Exception e) {
            return -1; // ArrayIndexOutOfBounds, NullPointer
        }
    }
}
