package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.Iterator;

/**
 * Instances of this class keep track of per thread metrics. Following metrics
 * are kept track of.
 * <ol>
 * <li>Current: The metrics for the current iteration of the thread.</li>
 * <li>Saved: The metrics saved from the previous iterations of the thread.</li>
 * <li>Iterations: Total number of iterations.</li>
 * </ol>
 * <p>
 * The {@link ThreadedMetric} class keeps an instance of this class for
 * every thread being instrumented.
 * <p>
 * Here is the object graph that is maintained to keep track of all
 * instrumentation metrics
 * <ul>
 *   <li>{@link ThreadedMetric} keeps track of an instance of
 *   {@link PerThreadInfo} for each thread.</li>
 *   <ul>
 *     <li>{@link PerThreadInfo} keeps track of saved and current iteration
 *      info for each thread and it's total/cumulative iteration count.</li>
 *     <ul>
 *       <li>{@link IterationInfo} keeps track of the checkpoints within
 *        an iteration. It also keeps track of cumulative iteration count until
 *        the current iteration.</li>
 *       <ul>
 *         <li>{@link CheckpointInfo} keeps track of a checkpoint.</li>
 *       </ul>
 *     </ul>
 *   </ul>
 * </ul>
 *
 * <p>
 * The contents of this class and other info classes are only updated from
 * a single thread: the thread being instrumented. The contents are however
 * eventually read by a different thread when summarizing. The data structures
 * used should guarantee that the reads from a different thread generate
 * consistent & correct results.
 *  
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
final class PerThreadInfo implements Iterable<IterationInfo> {

    /**
     * Adds the supplied checkpoint info to the current iteration info.
     *
     * @param inInfo the checkpoint info to add.
     */
    public void addCurrent(CheckpointInfo inInfo) {
        mCurrent.addCheckpoint(inInfo);
    }

    /**
     * Clears the current iteration info. Resets it for the next
     * iteration.
     */
    public void clearCurrent() {
        mCurrent.clear();
    }

    /**
     * Saves off the current iteration info for reporting.
     * Resets the current metric for the next iteration.
     */
    public void saveCurrent() {
        mCurrent.setNumIterations(getIterations());
        mSaved.add(mCurrent);
        mCurrent = new IterationInfo();
    }

    /**
     * Increments the total/cumulative number of iterations.
     */
    public void addIteration() {
        mIterations.incrementAndGet();
    }

    /**
     * Returns the total/cumulative number of iterations.
     *
     * @return the total/cumulative number of iterations.
     */
    private long getIterations() {
        return mIterations.get();
    }

    /**
     * Clears the saved iteration infos. Resets the state to what it
     * was when the instance was initialized.
     */
    public void clearSaved() {
        mSaved.clear();
        mIterations.set(0);
    }

    /**
     * Returns true if there are no saved iteration infos available.
     *
     * @return true, if there are no saved iteration infos available.
     */
    public boolean isSavedEmpty() {
        return mSaved.isEmpty();
    }

    /**
     * Returns an iterator that iterates through all the saved metrics.
     *
     * @return the iterator to iterate through saved metrics.
     */
    public Iterator<IterationInfo> iterator() {
        return mSaved.iterator();
    }

    /**
     * Returns the name of the thread for which this metric has been collected.
     *
     * @return the name of the thread.
     */
    public String getName() {
        return mName;
    }

    /**
     * Creates an empty instance.
     */
    PerThreadInfo() {
    }

    /**
     * The current iteration info. ie. the iteration that's currently in
     * progress. Need not be volatile, as it's always written to & read from
     * the same thread. 
     */

    private IterationInfo mCurrent = new IterationInfo();
    /**
     * The iteration counter.
     */
    private final AtomicLong mIterations = new AtomicLong();
    /**
     * The name of the thread being instrumented.
     */
    private final String mName = Thread.currentThread().getName();
    /**
     * The list of saved iteration infos. This list is written to from
     * the thread being instrumented and the thread invoking
     * {@link #clearSaved()}. It's read when summarizing the metrics.
     */
    private final Queue<IterationInfo> mSaved = new ConcurrentLinkedQueue<IterationInfo>();
}
