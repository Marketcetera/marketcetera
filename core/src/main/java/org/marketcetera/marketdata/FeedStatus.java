package org.marketcetera.marketdata;

/* $License$ */

/**
 * Indicates market data feed status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum FeedStatus
{
    /**
     * feed is off-line
     */
    OFFLINE,
    /**
     * an error has occurred that has caused the feed to be unavailable
     */
    ERROR,
    /**
     * the feed is available
     */
    AVAILABLE,
    /**
     * the status of the feed is unknown
     */
    UNKNOWN;
    /**
     * Indicate if the feed is running based on its status.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRunning()
    {
        return equals(AVAILABLE);
    }
}
