package org.marketcetera.marketdata.event;

import java.util.Deque;

import org.marketcetera.event.Event;

/* $License$ */

/**
 * Provides a POJO {@link GeneratedMarketDataEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleGeneratedMarketDataEvent
        implements GeneratedMarketDataEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.HasMarketDataRequestId#getMarketDataRequestId()
     */
    @Override
    public String getMarketDataRequestId()
    {
        return marketDataRequestId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.MarketDataEvent#getEvents()
     */
    @Override
    public Deque<Event> getEvents()
    {
        return events;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleMarketDataEvent [marketDataRequestId=").append(marketDataRequestId).append(", events=")
                .append(events).append("]");
        return builder.toString();
    }
    /**
     * Sets the marketDataRequestId value.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     */
    public void setMarketDataRequestId(String inMarketDataRequestId)
    {
        marketDataRequestId = inMarketDataRequestId;
    }
    /**
     * Sets the events value.
     *
     * @param inEvents a <code>Deque&lt;Event&gt;</code> value
     */
    public void setEvents(Deque<Event> inEvents)
    {
        events = inEvents;
    }
    /**
     * Create a new SimpleGeneratedMarketDataEvent instance.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     * @param inEvents a <code>Deque&lt;Event&gt;</code> value
     */
    public SimpleGeneratedMarketDataEvent(String inMarketDataRequestId,
                                          Deque<Event> inEvents)
    {
        marketDataRequestId = inMarketDataRequestId;
        events = inEvents;
    }
    /**
     * Create a new SimpleGeneratedMarketDataEvent instance.
     */
    public SimpleGeneratedMarketDataEvent() {}
    /**
     * market data request id value
     */
    private String marketDataRequestId;
    /**
     * market data events value
     */
    private Deque<Event> events;
}
