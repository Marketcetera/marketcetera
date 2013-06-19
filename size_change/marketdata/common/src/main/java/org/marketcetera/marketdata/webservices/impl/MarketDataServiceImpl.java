package org.marketcetera.marketdata.webservices.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.core.Response;

import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.manager.MarketDataManager;
import org.marketcetera.marketdata.webservices.MarketDataService;
import org.marketcetera.marketdata.webservices.WebServicesEvent;
import org.marketcetera.marketdata.webservices.WebServicesEventFactory;
import org.marketcetera.marketdata.webservices.WebServicesMarketDataRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * TODO create a max size for the buffered events
 */
@ThreadSafe
public class MarketDataServiceImpl
        implements MarketDataService
{
    /**
     * Sets the maxQueueSize value.
     *
     * @param inMaxQueueSize an <code>int</code> value
     */
    public void setMaxQueueSize(int inMaxQueueSize)
    {
        maxQueueSize = inMaxQueueSize;
    }
    /**
     * Sets the maxQueueInterval value.
     *
     * @param inMaxQueueInterval an <code>int</code> value
     */
    public void setMaxQueueInterval(int inMaxQueueInterval)
    {
        maxQueueInterval = inMaxQueueInterval;
    }
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
    public long request(WebServicesMarketDataRequest inRequest)
    {
        long requestId = -1;
        synchronized(events) {
            final BlockingDeque<Event> eventQueue = new LinkedBlockingDeque<Event>();
            Subscriber subscriber = new Subscriber() {
                @Override
                public void publishTo(Object inData)
                {
                    if(inData instanceof Event) {
                        eventQueue.add((Event)inData);
                    }
                }
            };
            // TODO manage exceptions (and remove the newly added objects if an error occurs)
            requestId = marketDataManager.requestMarketData(inRequest,
                                                            subscriber);
            if(requestId != -1) {
                events.put(requestId,
                           eventQueue);
            }
        }
        return requestId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#createRequest(java.lang.String, java.lang.String)
     */
    @Override
    public long createRequest(String inSymbol,
                              String inContent)
    {
        WebServicesMarketDataRequest request = new WebServicesMarketDataRequest();
        request.getSymbols().add(inSymbol);
        request.getContent().add(Content.valueOf(inContent));
        return request(request);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#getEvents(long)
     */
    @Override
    public List<WebServicesEvent> getEvents(long inRequestId)
    {
        WebServicesEventFactory factory = new WebServicesEventFactory();
        List<Event> queuedEvents = new ArrayList<Event>();
        synchronized(events) {
            BlockingDeque<Event> waitingEvents = events.get(inRequestId);
            if(waitingEvents != null) {
                waitingEvents.drainTo(queuedEvents);
            }
        }
        List<WebServicesEvent> eventsToReturn = new ArrayList<WebServicesEvent>();
        for(Event event : queuedEvents) {
            WebServicesEvent eventToReturn = factory.create(event);
            eventsToReturn.add(eventToReturn);
        }
        return eventsToReturn.isEmpty() ? null : eventsToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#delete(long)
     */
    @Override
    public Response cancel(long inRequestId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Canceling market data request {}",
                               inRequestId);
        synchronized(events) {
            marketDataManager.cancelMarketDataRequest(inRequestId);
            BlockingDeque<Event> eventQueue = events.remove(inRequestId);
            if(eventQueue != null) {
                eventQueue.clear();
            }
        }
        return Response.ok().build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#cancelRequest(long)
     */
    @Override
    public Response cancelRequest(long inRequestId)
    {
        return cancel(inRequestId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#getSnapshot(java.lang.String, java.lang.String)
     */
    @Override
    public Event getSnapshot(String inSymbol,
                             String inContent)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * event queues by request Id
     */
    private final Map<Long,BlockingDeque<Event>> events = new HashMap<Long,BlockingDeque<Event>>();
    /**
     * manages market data requests
     */
    private MarketDataManager marketDataManager;
    /**
     * generates unique request ids
     */
    private final AtomicLong requestIdCounter = new AtomicLong(0);
    /**
     * determines the maximum allowed size of event queues
     */
    private int maxQueueSize = 0;
    /**
     * determines the maximum allowed interval (in seconds) at which event queues must be checked at least
     */
    private int maxQueueInterval = 0;
}
