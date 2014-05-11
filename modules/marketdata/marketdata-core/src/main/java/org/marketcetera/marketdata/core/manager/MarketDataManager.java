package org.marketcetera.marketdata.core.manager;

import java.util.Set;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages market data providers and provides a central access point for market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataManager.java 16403 2012-12-14 05:04:07Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id$")
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
     * @param inSubscriber an <code>ISubscriber</code> value or <code>null</code> if no update events are required
     * @return a <code>long</code> value that identifies the request
     * @throws MarketDataRequestFailed if the request could not be executed
     * @throws MarketDataProviderNotAvailable if a specifically requested provider is not available
     * @throws NoMarketDataProvidersAvailable if no specific provider was requested and no providers are available
     * @throws MarketDataRequestTimedOut if the request could not be executed in a reasonable amount of time
     */
    public long requestMarketData(MarketDataRequest inRequest,
                                  ISubscriber inSubscriber);
    /**
     * Gets the most pertinent snapshot for the given Instrument - Content tuple.
     * 
     * <p>This method will return the most recent market data for the given criteria. This call
     * assumes that other calls will have retrieved and maintained the market data in the market data
     * cache. If no previous calls have requested the relevant market data, no market data will be
     * available. This call does not retrieve market data from the actual provider.
     * 
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value
     * @return an <code>Event</code> value or <code>null</code> if market data for the given criteria is not available
     * @throws MarketDataRequestFailed if the request could not be executed
     */
    public Event requestMarketDataSnapshot(Instrument inInstrument,
                                           Content inContent,
                                           String inProvider);
    /**
     * Cancels all market data requests for the given subscriber.
     *
     * @param inRequestId a <code>long</code> value
     */
    public void cancelMarketDataRequest(long inRequestId);
    /**
     * Gets the available capabilities of active market data providers.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    public Set<Capability> getAvailableCapability();
}
