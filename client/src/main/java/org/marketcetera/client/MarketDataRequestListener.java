package org.marketcetera.client;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Receives incoming market data requests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestListener
{
    /**
     * Receive the given market data request.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     */
    void receiveMarketDataRequest(MarketDataRequest inMarketDataRequest);
}
