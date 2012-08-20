package org.marketcetera.core.marketdata;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface FeedServices
{
    /**
     * Sets the status of the feed.
     * 
     * @param inFeedStatus a <code>FeedStatus</code> value
     */
    public void setFeedStatus(FeedStatus inFeedStatus);
    /**
     * Registers data received from the feed in association with the
     * given handle.
     * 
     * @param inHandle a <code>MarketDataHandle</code> value
     * @param inData an <code>Object</code> value containing the data
     *   to be transmitted to subscribers
     */
    public void dataReceived(String inHandle,
                             Object inData);
    
}
