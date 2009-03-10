/*
 * ProgressListener.java
 *
 * Created on 8. Juni 2002, 19:41
 */

package net.sourceforge.fraglets.mtgo.trader;

/**
 *
 * @author  marion@users.sourceforge.net
 */
public interface ProgressListener {
    public void setObjective(String objective);
    public void setPercent(int percent);
}
