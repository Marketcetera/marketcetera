package org.marketcetera.marketdata.core.provider;

/* $License$ */

/**
 * Listens for changes in market data status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataStatusListener
{
    /**
     * Receives updated market data provider status.
     *
     * @param inStatus a <code>MarketDataProviderStatus</code> value
     */
    void receiveMarketDataProviderStatus(MarketDataProviderStatus inStatus);
}
