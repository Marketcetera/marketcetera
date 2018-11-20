package org.marketcetera.marketdata;

import java.util.Deque;
import java.util.Set;

import org.marketcetera.core.BaseClient;
import org.marketcetera.event.Event;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;

/* $License$ */

/**
 * Provides access to remote market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MDClient.java 17245 2016-09-03 01:25:42Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: MDClient.java 17245 2016-09-03 01:25:42Z colin $")
public interface MarketDataClient
        extends BaseClient
{
    /**
     * Request market data.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inMarketDataListener a <code>MarketDataListener</code> value
     * @return a <code>String</code> value containing the request ID
     */
    String request(MarketDataRequest inRequest,
                   MarketDataListener inMarketDataListener);
    /**
     * Cancels a market data request.
     *
     * @param inRequestId a <code>String</code> value
     */
    void cancel(String inRequestId);
    /**
     * Gets the most recent snapshot of the given market data.
     * 
     * <p>Market data must be pre-requested via {@link #request(MarketDataRequest, MarketDataListener)}.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @return a <code>Deque&lt;Event&gt;</code>
     */
    Deque<Event> getSnapshot(Instrument inInstrument,
                             Content inContent);
    /**
     * Gets a subset of the most recent snapshot available of the given market data.
     *
     * <p>Market data must be pre-requested via {@link #request(ClientContext, MarketDataRequest, boolean)}.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inPage a <code>PageRequest</code> value indicating what subset to return
     * @return a <code>CollectionPageResponse&lt;Event&gt;</code>
     */
    CollectionPageResponse<Event> getSnapshot(Instrument inInstrument,
                                              Content inContent,
                                              PageRequest inPage);
    /**
     * Adds a market data connection status listener, which receives all market data connection status changes.
     *
     * <p>If the same listener is added more than once, it will receive notifications as many times as it has been added.</p>
     *
     * <p>The listeners are notified in the reverse order of their addition.</p>
     *
     * @param inListener The listener which should be supplied the server connection status changes.
     */
    void addMarketDataStatusListener(MarketDataStatusListener inListener);
    /**
     * Removes a market data connection status listener that was previously added via {@link #addMarketDataStatusListener(MarketDataStatusListener)}.
     *
     * <p>If the listener was added more than once, only its most recently added instance will be removed.</p>
     *
     * @param inListener The listener which should stop receiving server connection status changes.
     */
    void removeMarketDataStatusListener(MarketDataStatusListener inListener);
    /**
     * Gets the available capabilities of active market data providers.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    Set<Capability> getAvailableCapability();
}
