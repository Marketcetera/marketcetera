package org.marketcetera.event;

/* $License$ */

/**
 * Indicates that the implementer has a variety of timestamp values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasTimestamps
{
    /**
     * Gets the processed timestamp value.
     *
     * @return a <code>long</code> value
     */
    public long getProcessedTimestamp();
    /**
     * Sets the processed timestamp value.
     *
     * @param inTimestamp a <code>long</code> value
     */
    public void setProcessedTimestamp(long inTimestamp);
    /**
     * Gets the received timestamp value.
     *
     * @return a <code>long</code> value
     */
    public long getReceivedTimestamp();
    /**
     * Sets the received timestamp value.
     *
     * @param inTimestamp a <code>long</code> value
     */
    public void setReceivedTimestamp(long inTimestamp);
}
