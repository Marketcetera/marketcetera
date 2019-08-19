package org.marketcetera.marketdata;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataStatusPublisher
{
    /**
     * Add the given market data status listener.
     *
     * @param inMarketDataStatusListener a <code>MarketDataStatusListener</code> value
     */
    void addMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener);
    /**
     * Remove the given market data status listener.
     *
     * @param inMarketDataStatusListener a <code>MarketDataStatusListener</code> value
     */
    void removeMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener);
}
