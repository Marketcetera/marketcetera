package org.marketcetera.strategy;

import org.marketcetera.marketdata.DataRequest;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Services available to strategies to emit data of various types to the strategy agent framework.
 * 
 * <p>Data transmitted via the methods in this interface will be emitted via the implementer's {@link DataEmitter}
 * implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
interface OutboundServicesProvider
{
    /**
     * Sends an order to the destination or destinations established in the strategy module.
     *
     * TODO need the FixAgnostic objects
     * 
     * @param inOrder
     */
    void sendOrder(Object inOrder);
    /**
     * Sends a trade suggestion to the destination or destinations established in the strategy module.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    void sendSuggestion(Suggestion inSuggestion);
    /**
     * Creates a market data request.
     * 
     * <p>The <code>inSource</code> parameter must contain the identifier of a started market data provider
     * module.
     * 
     * @param inRequest a <code>DataRequest</code> value indicating what data to request
     * @param inSource a <code>String</code> value indicating what market data provider from which to request the data
     * @return a <code>long</code> value containing an identifier corresponding to this market data request
     */
    long requestMarketData(DataRequest inRequest,
                           String inSource);
    /**
     * Cancels a given market data request.
     * 
     * <p>If the given <code>inDataRequestID</code> identifier does not correspond to an active market data
     * request, this method does nothing.
     *
     * @param inDataRequestID a <code>long</code> value identifying the market data request to cancel
     */
    void cancelMarketDataRequest(long inDataRequestID);
    /**
     * Cancels all market data requests for this strategy.
     *
     * <p>If there are no active market data requests for this strategy, this method does nothing.
     */
    void cancelAllMarketDataRequests();
}
