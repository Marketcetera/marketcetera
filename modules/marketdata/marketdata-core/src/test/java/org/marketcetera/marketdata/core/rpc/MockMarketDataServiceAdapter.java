package org.marketcetera.marketdata.core.rpc;

import java.util.Deque;
import java.util.List;

import org.marketcetera.core.Pair;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.MarketDataRequest;

import com.google.common.collect.Lists;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
        getEventsRequests.add(inId);
        return eventsToReturn;
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
    public List<Long> getGetEventsRequests()
    {
        return getEventsRequests;
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
     * 
     *
     *
     */
    public void reset()
    {
        requests.clear();
        lastUpdateRequests.clear();
        canceledIds.clear();
        getEventsRequests.clear();
        eventsToReturn.clear();
    }
    /**
     * 
     */
    private final Deque<Event> eventsToReturn = Lists.newLinkedList();
    /**
     * 
     */
    private final List<Long> getEventsRequests = Lists.newArrayList();
    /**
     * 
     */
    private final List<Pair<MarketDataRequest,Boolean>> requests = Lists.newArrayList();
    /**
     * 
     */
    private final List<Long> lastUpdateRequests = Lists.newArrayList();
    /**
     * 
     */
    private final List<Long> canceledIds = Lists.newArrayList();
}
