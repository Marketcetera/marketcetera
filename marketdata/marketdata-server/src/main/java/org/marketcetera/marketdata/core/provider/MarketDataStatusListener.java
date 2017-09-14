package org.marketcetera.marketdata.core.provider;

/* $License$ */

/**
 * Listens for changes in market data status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataStatusListener.java 17068 2015-12-07 17:26:31Z colin $
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
