package org.marketcetera.core.metrics;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* $License$ */
/**
 * Records information about every iteration, through the checkpoints, from
 * begin to end. Each instance, when recorded, results in a row of data in
 * the csv file.
 *
 * @version $Id: IterationInfo.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 * @see PerThreadInfo
 */
final class IterationInfo implements Iterable<CheckpointInfo> {
    /**
     * Clears the saved information. Resets the state back to as it was
     * when the instance was constructed.
     */
    public void clear() {
        mCheckpoints.clear();
        mNumIterations=0;
    }

    /**
     * Sets the total/cumulative number of iterations until this iteration.
     *
     * @param inNumIterations the total/cumulative number of iterations.
     */
    public void setNumIterations(long inNumIterations) {
        mNumIterations = inNumIterations;
    }

    /**
     * The total/cumulative number of iterations including this iteration.
     *
     * @return total/cumulative number of iteration until and including
     * this iteration.
     */
    public long getNumIterations() {
        return mNumIterations;
    }

    /**
     * Returns true if this iteration contains no checkpoints.
     *
     * @return true if this iteration contains no checkpoints.
     */
    public boolean isEmpty() {
        return mCheckpoints.isEmpty();
    }

    /**
     * Adds the supplied checkpoint info to this iteration.
     *
     * @param inInfo the supplied checkpoint info.
     */
    public void addCheckpoint(CheckpointInfo inInfo) {
        mCheckpoints.add(inInfo);
    }

    @Override
    public Iterator<CheckpointInfo> iterator() {
        return mCheckpoints.iterator();
    }

    /**
     * The list of checkpoints.
     */
    private final Queue<CheckpointInfo> mCheckpoints = new ConcurrentLinkedQueue<CheckpointInfo>();
    /**
     * The iteration number represented by this iteration info.
     */
    private volatile long mNumIterations;
}
