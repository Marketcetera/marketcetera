package org.marketcetera.core.metrics;

/**
 * Instances of this class keeps track of information on every checkpoint.
 *
 * @version $Id: CheckpointInfo.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 * @see PerThreadInfo
 */
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
