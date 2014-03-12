package org.marketcetera.marketdata.core.webservice;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
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
     * 
     *
     *
     * @param inRequest
     * @param inStreamEvents
     * @return
     */
    long request(MarketDataRequest inRequest,
                 boolean inStreamEvents);
    /**
     * 
     *
     *
     * @param inRequestId
     * @return
     */
    long getLastUpdate(long inRequestId);
    /**
     * 
     *
     *
     * @param inRequestId
     */
    void cancel(long inRequestId);
    /**
     * 
     *
     *
     * @param inRequestId
     * @return
     */
    Deque<Event> getEvents(long inRequestId);
    /**
     * 
     *
     *
     * @param inRequestIds
     * @return
     */
    Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds);
    /**
     * 
     *
     *
     * @param inInstrument
     * @param inContent
     * @param inProvider
     * @return
     */
    Deque<Event> getSnapshot(Instrument inInstrument,
                             Content inContent,
                             String inProvider);
    /**
     * 
     *
     *
     * @param inInstrument
     * @param inContent
     * @param inProvider
     * @param inPage
     * @return
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
}
