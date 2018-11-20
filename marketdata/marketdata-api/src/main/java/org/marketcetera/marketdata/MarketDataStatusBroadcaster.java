package org.marketcetera.marketdata;

/* $License$ */

/**
 * Broadcasts market data status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataStatusBroadcaster
{
    /**
     * Report the status of a provider.
     *
     * @param inMarketDataStatus a <code>MarketDataStatus</code> value
     */
    void reportMarketDataStatus(MarketDataStatus inMarketDataStatus);
}
