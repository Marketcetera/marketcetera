package org.marketcetera.marketdata;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;

/* $License$ */

/**
 * Provides RPC utilities for market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcUtil
{
    /**
     *
     *
     * @param inResponse
     * @return
     */
    public static Event getEvent(MarketDataRpc.EventsResponse inResponse)
    {
        throw new UnsupportedOperationException(); // TODO
    }

    /**
     *
     *
     * @param inEvent
     * @param inResponseBuilder
     */
    public static void setEvent(Event inEvent,
                                MarketDataRpc.EventsResponse.Builder inResponseBuilder)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     * Get the market data request from the given RPC request.
     *
     * @param inRequest a <code>String</code> value
     * @param inRequestId
     * @return a <code>MarketDataRequest</code> value
     */
    public static MarketDataRequest getMarketDataRequest(String inRequest,
                                                         String inRequestId)
    {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(inRequest);
        requestBuilder.append(':');
        requestBuilder.append(MarketDataRequestBuilder.REQUEST_ID_KEY).append('=').append(inRequestId);
        return MarketDataRequestBuilder.newRequestFromString(requestBuilder.toString());
    }
}
