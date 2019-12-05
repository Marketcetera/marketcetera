package org.marketcetera.marketdata.event;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Indicates that the implementor has a {@link MarketDataRequest} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasMarketDataRequest
{
    /**
     * Get the market data request value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    MarketDataRequest getMarketDataRequest();
}
