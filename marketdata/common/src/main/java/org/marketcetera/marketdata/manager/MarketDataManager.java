package org.marketcetera.marketdata.manager;

import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.marketdata.request.MarketDataRequest;

/* $License$ */

/**
 * Manages market data providers and provides a central access point for market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataManager
{
    /**
     * Executes the given market data request and publishes the results to the given subscriber.
     * 
     * <p>Market data will be published to the given subscriber as it becomes available until
     * the request is {@link #cancelMarketDataRequest(Subscriber) canceled}.
     * 
     * TODO what is the behavior if the same request is submitted multiple times with the same subscriber?
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inSubscriber a <code>Subscriber</code>
     * @return a <code>long</code> value that identifies the request
     * @throws MarketDataRequestFailed if the request could not be executed
     * @throws MarketDataProviderNotAvailable if a specifically requested provider is not available
     * @throws NoMarketDataProvidersAvailable if no specific provider was requested and no providers are available
     * @throws MarketDataRequestTimedOut if the request could not be executed in a reasonable amount of time
     */
    public long requestMarketData(MarketDataRequest inRequest,
                                  Subscriber inSubscriber);
    /**
     * Cancels all market data requests for the given subscriber.
     *
     * @param inRequestId a <code>long</code> value
     */
    public void cancelMarketDataRequest(long inRequestId);
}
