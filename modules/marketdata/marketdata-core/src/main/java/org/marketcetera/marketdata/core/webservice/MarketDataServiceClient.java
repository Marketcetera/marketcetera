package org.marketcetera.marketdata.core.webservice;

import java.util.*;

import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides access to remote market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface MarketDataServiceClient
        extends Lifecycle
{
    /**
     * Request market data.
     * 
     * <p>Begins a market data subscription. The returned id can be
     * used to retrieve events via {@link #getEvents(ClientContext, long)}.
     * If the <code>inStreamEvents</code> value is true, events will be queued
     * for retrieval. If false, events will not be retrieved, but you can
     * determine if the market data contents have changed via {@link #getLastUpdate(ClientContext, long)}.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inStreamEvents a <code>boolean</code> value
     * @return a <code>long</code> value
     */
    long request(MarketDataRequest inRequest,
                 boolean inStreamEvents);
    /**
     * Gets the timestamp of the last update for the given request.
     *
     * @param inRequestId a <code>long</code> value
     * @return a <code>long</code> value
     */
    long getLastUpdate(long inRequestId);
    /**
     * Cancels a market data request.
     *
     * @param inRequestId a <code>long</code> value
     */
    void cancel(long inRequestId);
    /**
     * Gets the queued events generate for a market data request.
     *
     * @param inRequestId a <code>long</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    Deque<Event> getEvents(long inRequestId);
    /**
     * Gets the events from multiple market data requests at the same time.
     *
     * @param inRequestIds a <code>List&lt;Long&gt;</code> value
     * @return a <code>Map&lt;Long,LinkedList&lt;Event&gt;&gt;</code> value
     */
    Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds);
    /**
     * Gets the most recent snapshot of the given market data.
     * 
     * <p>Market data must be pre-requested via {@link #request(ClientContext, MarketDataRequest, boolean)}.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @return a <code>Deque&lt;Event&gt;</code>
     */
    Deque<Event> getSnapshot(Instrument inInstrument,
                             Content inContent,
                             String inProvider);
    /**
     * Gets a subset of the most recent snapshot available of the given market data.
     *
     * <p>Market data must be pre-requested via {@link #request(ClientContext, MarketDataRequest, boolean)}.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @param inPage a <code>PageRequest</code> value indicating what subset to return
     * @return a <code>Deque&lt;Event&gt;</code>
     */
    Deque<Event> getSnapshotPage(Instrument inInstrument,
                                 Content inContent,
                                 String inProvider,
                                 PageRequest inPage);
    /**
     * Adds a server connection status listener, which receives all server connection status changes.
     *
     * <p>If the same listener is added more than once, it will receive notifications as many times as it has been added.</p>
     *
     * <p>The listeners are notified in the reverse order of their addition.</p>
     *
     * @param inListener The listener which should be supplied the server connection status changes.
     */
    public void addServerStatusListener(ServerStatusListener inListener);
    /**
     * Removes a server connection status listener that was previously added via {@link #addServerStatusListener(ServerStatusListener)}.
     *
     * <p>If the listener was added more than once, only its most recently added instance will be removed.</p>
     *
     * @param inListener The listener which should stop receiving server connection status changes.
     */
    public void removeServerStatusListener(ServerStatusListener inListener);
    /**
     * Gets the available capabilities of active market data providers.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    public Set<Capability> getAvailableCapability();
}
