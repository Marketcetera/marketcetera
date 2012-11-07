package org.marketcetera.marketdata.webservices.impl;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.core.Response;

import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.marketdata.events.Event;
import org.marketcetera.marketdata.manager.MarketDataManager;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestAtom;
import org.marketcetera.marketdata.webservices.MarketDataService;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * todo create a max size for the buffered events
 */
@ThreadSafe
public class MarketDataServiceImpl
        implements MarketDataService
{
    /**
     * Sets the marketDataManager value.
     *
     * @param inMarketDataManager a <code>MarketDataManager</code> value
     */
    public void setMarketDataManager(MarketDataManager inMarketDataManager)
    {
        marketDataManager = inMarketDataManager;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#request(org.marketcetera.marketdata.request.MarketDataRequest)
     */
    @Override
    public long request(MarketDataRequest inRequest)
    {
        long requestId = requestIdCounter.incrementAndGet();
        synchronized(events) {
            final BlockingDeque<Event> eventQueue = new LinkedBlockingDeque<Event>();
            events.put(requestId,
                       eventQueue);
            Subscriber subscriber = new Subscriber() {
                    @Override
                    public boolean isInteresting(Object inData)
                    {
                        return true;
                    }
                    @Override
                    public void publishTo(Object inData)
                    {
                        if(inData instanceof Event) {
                            eventQueue.add((Event)inData);
                        }
                    }
            };
            subscribers.put(requestId,
                            subscriber);
            // TODO manage exceptions (and remove the newly added objects if an error occurs)
            marketDataManager.requestMarketData(inRequest,
                                                subscriber);
        }
        return requestId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#getEvents(long)
     */
    @Override
    public Collection<Event> getEvents(long inRequestId)
    {
        synchronized(events) {
            BlockingDeque<Event> waitingEvents = events.get(inRequestId);
            if(waitingEvents != null) {
                Collection<Event> eventsToReturn = new ArrayList<Event>();
                waitingEvents.drainTo(eventsToReturn);
                return eventsToReturn;
            }
        }
        return Collections.emptyList();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#delete(long)
     */
    @Override
    public Response cancel(long inRequestId)
    {
        synchronized(events) {
            Subscriber subscriber = subscribers.remove(inRequestId);
            if(subscriber != null) {
                marketDataManager.cancelMarketDataRequest(subscriber);
            }
            BlockingDeque<Event> eventQueue = events.remove(inRequestId);
            if(eventQueue != null) {
                eventQueue.clear();
            }
        }
        return Response.ok().build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#getSnapshot(org.marketcetera.marketdata.request.MarketDataRequestAtom)
     */
    @Override
    public Event getSnapshot(MarketDataRequestAtom inRequest)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * event queues by request Id
     */
    private final Map<Long,BlockingDeque<Event>> events = new HashMap<Long,BlockingDeque<Event>>();
    /**
     * subscriber liason objects by request Id
     */
    private final Map<Long,Subscriber> subscribers = new HashMap<Long,Subscriber>();
    /**
     * manages market data requests
     */
    private MarketDataManager marketDataManager;
    /**
     * generates unique request ids
     */
    private final AtomicLong requestIdCounter = new AtomicLong(0);
}
