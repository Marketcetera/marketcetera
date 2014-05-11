package org.marketcetera.marketdata.core.rpc;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.marketcetera.core.Pair;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.webservice.PageRequest;
import org.marketcetera.trade.Instrument;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides an implementation of <code>MarketDataServiceAdapter</code> for testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class MockMarketDataServiceAdapter
        implements MarketDataServiceAdapter
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#request(org.marketcetera.marketdata.MarketDataRequest, boolean)
     */
    @Override
    public long request(MarketDataRequest inRequest,
                        boolean inStreamEvents)
    {
        requests.add(Pair.create(inRequest,
                                 inStreamEvents));
        return System.nanoTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getLastUpdate(long)
     */
    @Override
    public long getLastUpdate(long inId)
    {
        lastUpdateRequests.add(inId);
        return inId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#cancel(long)
     */
    @Override
    public void cancel(long inId)
    {
        canceledIds.add(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getEvents(long)
     */
    @Override
    public Deque<Event> getEvents(long inId)
    {
        eventsRequests.add(inId);
        return eventsToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getAllEvents(java.util.List)
     */
    @Override
    public Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds)
    {
        allEventsRequests.add(inRequestIds);
        return allEventsToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent,
                                    String inProvider)
    {
        snapshotRequests.add(new SnapshotRequest(inInstrument,
                                                 inContent,
                                                 inProvider));
        return snapshotEventsToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getSnapshotPage(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.marketcetera.marketdata.core.webservice.PageRequest)
     */
    @Override
    public Deque<Event> getSnapshotPage(Instrument inInstrument,
                                        Content inContent,
                                        String inProvider,
                                        PageRequest inPageRequest)
    {
        snapshotRequests.add(new SnapshotRequest(inInstrument,
                                                 inContent,
                                                 inProvider,
                                                 inPageRequest));
        return snapshotEventsToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        capabilityRequests.incrementAndGet();
        return capabilitiesToReturn;
    }
    /**
     * Get the requests value.
     *
     * @return a <code>List&lt;Pair&lt;MarketDataRequest,Boolean&gt;&gt;</code> value
     */
    public List<Pair<MarketDataRequest,Boolean>> getRequests()
    {
        return requests;
    }
    /**
     * Get the lastUpdateRequests value.
     *
     * @return a <code>List&lt;Long&gt;</code> value
     */
    public List<Long> getLastUpdateRequests()
    {
        return lastUpdateRequests;
    }
    /**
     * Get the canceledIds value.
     *
     * @return a <code>List&lt;Long&gt;</code> value
     */
    public List<Long> getCanceledIds()
    {
        return canceledIds;
    }
    /**
     * Get the getEventsRequests value.
     *
     * @return a <code>List&lt;Long&gt;</code> value
     */
    public List<Long> getEventsRequests()
    {
        return eventsRequests;
    }
    /**
     * Get the eventsToReturn value.
     *
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    public Deque<Event> getEventsToReturn()
    {
        return eventsToReturn;
    }
    /**
     * Get the allEventsRequests value.
     *
     * @return a <code>List&lt;List&lt;Long&gt;&gt;</code> value
     */
    public List<List<Long>> getAllEventsRequests()
    {
        return allEventsRequests;
    }
    /**
     * Get the allEventsToReturn value.
     *
     * @return a <code>Map&lt;Long,LinkedList&lt;Event&gt;&gt;</code> value
     */
    public Map<Long,LinkedList<Event>> getAllEventsToReturn()
    {
        return allEventsToReturn;
    }
    /**
     * Get the snapshotEventsToReturn value.
     *
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    public Deque<Event> getSnapshotEventsToReturn()
    {
        return snapshotEventsToReturn;
    }
    /**
     * Get the snapshotRequests value.
     *
     * @return a <code>List&lt;SnapshotRequest&gt;</code> value
     */
    public List<SnapshotRequest> getSnapshotRequests()
    {
        return snapshotRequests;
    }
    /**
     * Get the capabilityRequests value.
     *
     * @return a <code>AtomicInteger</code> value
     */
    public AtomicInteger getCapabilityRequests()
    {
        return capabilityRequests;
    }
    /**
     * Get the capabilitiesToReturn value.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    public Set<Capability> getCapabilitiesToReturn()
    {
        return capabilitiesToReturn;
    }
    /**
     * 
     *
     *
     */
    public void reset()
    {
        requests.clear();
        lastUpdateRequests.clear();
        canceledIds.clear();
        eventsRequests.clear();
        eventsToReturn.clear();
        allEventsRequests.clear();
        allEventsToReturn.clear();
        snapshotEventsToReturn.clear();
        snapshotRequests.clear();
        capabilitiesToReturn.clear();
        capabilityRequests.set(0);
    }
    /**
     * Records requests to {@link MockMarketDataServiceAdapter#getSnapshot(Instrument, Content, String)}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    public static class SnapshotRequest
    {
        /**
         * Create a new SnapshotRequest instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inContent a <code>Content</code> value
         * @param inProvider a <code>String</code> value
         */
        public SnapshotRequest(Instrument inInstrument,
                               Content inContent,
                               String inProvider)
        {
            this(inInstrument,
                 inContent,
                 inProvider,
                 null);
        }
        /**
         * Create a new SnapshotRequest instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inContent a <code>Content</code> value
         * @param inProvider a <code>String</code> value
         * @param inPageRequest a <code>PageRequest</code> value
         */
        public SnapshotRequest(Instrument inInstrument,
                               Content inContent,
                               String inProvider,
                               PageRequest inPageRequest)
        {
            instrument = inInstrument;
            content = inContent;
            provider = inProvider;
            pageRequest = inPageRequest;
        }
        /**
         * Get the instrument value.
         *
         * @return an <code>Instrument</code> value
         */
        public Instrument getInstrument()
        {
            return instrument;
        }
        /**
         * Get the content value.
         *
         * @return a <code>Content</code> value
         */
        public Content getContent()
        {
            return content;
        }
        /**
         * Get the provider value.
         *
         * @return a <code>String</code> value
         */
        public String getProvider()
        {
            return provider;
        }
        /**
         * Get the pageRequest value.
         *
         * @return a <code>PageRequest</code> value
         */
        public PageRequest getPageRequest()
        {
            return pageRequest;
        }
        /**
         * instrument value
         */
        private final Instrument instrument;
        /**
         * content value
         */
        private final Content content;
        /**
         * provider value
         */
        private final String provider;
        /**
         * page request value, may be <code>null</code>
         */
        private final PageRequest pageRequest;
    }
    /**
     * stores calls to {@link #getAllEvents(List)}.
     */
    private final List<List<Long>> allEventsRequests = Lists.newArrayList();
    /**
     * value to return from {@link #getAllEvents(List)}.
     */
    private final Map<Long,LinkedList<Event>> allEventsToReturn = Maps.newHashMap();
    /**
     * events to return from {@link #getEvents(long)}
     */
    private final Deque<Event> eventsToReturn = Lists.newLinkedList();
    /**
     * stores calls to {@link #getEvents(long)}
     */
    private final List<Long> eventsRequests = Lists.newArrayList();
    /**
     * stores calls to {@link #request(MarketDataRequest, boolean)}
     */
    private final List<Pair<MarketDataRequest,Boolean>> requests = Lists.newArrayList();
    /**
     * stores calls to {@link #getLastUpdate(long)}
     */
    private final List<Long> lastUpdateRequests = Lists.newArrayList();
    /**
     * stores calls to {@link #cancel(long)}
     */
    private final List<Long> canceledIds = Lists.newArrayList();
    /**
     * values to return from {@link #getSnapshot(Instrument, Content, String)}
     */
    private final Deque<Event> snapshotEventsToReturn = Lists.newLinkedList();
    /**
     * records calls to {@link #getSnapshot(Instrument, Content, String)}
     */
    private final List<SnapshotRequest> snapshotRequests = Lists.newLinkedList();
    /**
     * counts capability requests
     */
    private final AtomicInteger capabilityRequests = new AtomicInteger(0);
    /**
     * capabilities to return
     */
    private final Set<Capability> capabilitiesToReturn = Sets.newHashSet();
}
