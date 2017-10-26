package org.marketcetera.marketdata;

/* $License$ */

/**
 * Listens for market data status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataStatusListener
{
    /**
     * Receive the given market data status.
     *
     * @param inMarketDataStatus a <code>MarketDataStatus</code> value
     */
    default void receiveMarketDataStatus(MarketDataStatus inMarketDataStatus) {}
}
