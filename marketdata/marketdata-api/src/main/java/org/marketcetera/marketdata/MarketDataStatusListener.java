package org.marketcetera.marketdata;

/* $License$ */

/**
 *
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
     * @param inMarketDataStatus
     */
    void receiveMarketDataStatus(MarketDataStatus inMarketDataStatus);
}
