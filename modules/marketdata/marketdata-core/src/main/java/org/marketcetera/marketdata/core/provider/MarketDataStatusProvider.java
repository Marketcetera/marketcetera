package org.marketcetera.marketdata.core.provider;

/* $License$ */

/**
 * Provides market data status updates.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataStatusProvider
{
    /**
     * Adds the given market data status listener.
     *
     * @param inMarketDataStatusListener a <code>MarketDataStatusListener</code> value
     */
    public void addMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener);
    /**
     * Removes the given market data status listener.
     *
     * @param inMarketDataStatusListener a <code>MarketDataStatusListener</code> value
     */
    public void removeMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener);
}
