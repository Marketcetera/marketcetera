package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Instances of this class keeps track of information on every checkpoint.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 * @see PerThreadInfo
 */
@ClassVersion("$Id$")
final class CheckpointInfo {
    private final String mIdentifier;
    private final long mTimestamp;
    private final Object[] mData;

    /**
     * Creates an instance.
     *
     * @param inIdentifier the checkpoint identifier.
     * @param inTimestamp the timestamp at which this checkpoint was reached,
     * in nanoseconds.
     * @param inData optional data for the checkpoint.
     */
    public CheckpointInfo(String inIdentifier, long inTimestamp, Object[] inData) {
        mIdentifier = inIdentifier;
        mTimestamp = inTimestamp;
        mData = inData;
    }

    /**
     * The checkpoint identifier.
     *
     * @return the checkpoint identifier.
     */
    public String getIdentifier() {
        return mIdentifier;
    }

    /**
     * The timestamp at which the checkpoint was reached in nanoseconds.
     *
     * @return the timestamp at which the checkpoint was reached.
     */
    public long getTimestamp() {
        return mTimestamp;
    }

    /**
     * Optional checkpoint data. Can be null.
     *
     * @return any optional checkpoint data.
     */
    public Object[] getData() {
        return mData;
    }
}
