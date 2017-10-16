package org.marketcetera.marketdata;

/* $License$ */

/**
 * Provides status for a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataStatus
{
    /**
     * Get the feed status value.
     *
     * @return a <code>FeedStatus</code> value
     */
    FeedStatus getFeedStatus();
    /**
     * Get the provider value.
     *
     * @return a <code>String</code> value
     */
    String getProvider();
}
