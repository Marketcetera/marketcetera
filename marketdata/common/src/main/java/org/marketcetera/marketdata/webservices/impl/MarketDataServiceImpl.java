package org.marketcetera.marketdata.webservices.impl;

import java.math.BigDecimal;
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
import org.marketcetera.core.trade.*;
import org.marketcetera.marketdata.manager.MarketDataManager;
import org.marketcetera.marketdata.webservices.MarketDataService;
import org.marketcetera.marketdata.webservices.WebServicesEvent;
import org.marketcetera.marketdata.webservices.WebServicesEventBuilder;
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#test()
     */
    @Override
    public List<Instrument> test()
    {
        List<Instrument> instruments = new ArrayList<Instrument>();
        instruments.add(new Equity("GOOG"));
        instruments.add(new Option("GOOG",
                                   "20121215",
                                   new BigDecimal("100.50"),
                                   OptionType.Put));
        instruments.add(Future.fromString("GOOG-20121231"));
        instruments.add(new ConvertibleBond("123456"));
        return instruments;
    }
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
        long requestId = requestIdCounter.incrementAndGet();
        synchronized(events) {
            final BlockingDeque<Event> eventQueue = new LinkedBlockingDeque<Event>();
            events.put(requestId,
                       eventQueue);
            Subscriber subscriber = new Subscriber() {
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
    public List<WebServicesEvent> getEvents(long inRequestId)
    {
        WebServicesEventBuilder builder = new WebServicesEventBuilder();
        List<Event> queuedEvents = new ArrayList<Event>();
        synchronized(events) {
            BlockingDeque<Event> waitingEvents = events.get(inRequestId);
            if(waitingEvents != null) {
                waitingEvents.drainTo(queuedEvents);
            }
        }
        List<WebServicesEvent> eventsToReturn = new ArrayList<WebServicesEvent>();
        for(Event event : queuedEvents) {
            WebServicesEvent eventToReturn = builder.create(event);
            eventsToReturn.add(eventToReturn);
        }
        return eventsToReturn;
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
    /**
     * determines the maximum allowed size of event queues
     */
    private int maxQueueSize = 0;
    /**
     * determines the maximum allowed interval (in seconds) at which event queues must be checked at least
     */
    private int maxQueueInterval = 0;
}
