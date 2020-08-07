package org.marketcetera.marketdata;

/* $License$ */

/**
 * Indicates the implementor has a {@link MarketDataListener} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasMarketDataListener
{
    /**
     * Get the market data listener value.
     *
     * @return a <code>MarketDataListener</code> value
     */
    MarketDataListener getMarketDataListener();
}
