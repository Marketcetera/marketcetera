package org.marketcetera.marketdata;

/* $License$ */

/**
 * Indicates the implementor has a {@link FeedStatus} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasFeedStatus
{
    /**
     * Get the feed status value.
     *
     * @return a <code>FeedStatus</code> value
     */
    FeedStatus getFeedStatus();
    /**
     * Set the feed status value.
     *
     * @param inFeedStatus a <code>FeedStatus</code> value
     */
    void setFeedStatus(FeedStatus inFeedStatus);
}
