package org.marketcetera.mdclient;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataService.java 17245 2016-09-03 01:25:42Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: MarketDataService.java 17245 2016-09-03 01:25:42Z colin $")
public interface MarketDataService
{
    /**
     * Requests the given market data.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inStreamEvents a <code>boolean</code> value
     * @return a <code>long</code> value
     */
    long request(MarketDataRequest inRequest,
                 boolean inStreamEvents);
    /**
     * Gets the timestamp of the most recent update for the given request.
     *
     * @param inId a <code>long</code> value
     * @return a <code>long</code> value
     */
    long getLastUpdate(long inId);
    /**
     * Cancels the given market data request.
     *
     * @param inId a <code>long</code> value
     */
    void cancel(long inId);
    /**
     * Gets the queued events, if any, for the given request id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Deque&lt;Eventgt;</code> value
     */
    Deque<Event> getEvents(long inId);
    /**
     * Gets the queued events, if any, for each of the given request ids.
     *
     * @param inRequestIds a <code>List&lt;Long&gt;</code> value
     * @return a <code>Map&lt;Long,LinkedList&lt;Event&gt;&gt;</code> value
     */
    Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds);
    /**
     * Gets the most recent snapshot for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    Deque<Event> getSnapshot(Instrument inInstrument,
                             Content inContent,
                             String inProvider);
    /**
     * Gets the most recent snapshot page for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    Deque<Event> getSnapshotPage(Instrument inInstrument,
                                 Content inContent,
                                 String inProvider,
                                 PageRequest inPageRequest);
    /**
     * Gets the available capabilities.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    Set<Capability> getAvailableCapability();
}
