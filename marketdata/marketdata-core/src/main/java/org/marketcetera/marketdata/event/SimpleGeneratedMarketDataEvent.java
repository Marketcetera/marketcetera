package org.marketcetera.marketdata.event;

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
    public Event getEvent()
    {
        return event;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleMarketDataEvent [marketDataRequestId=").append(marketDataRequestId).append(", event=")
                .append(event).append("]");
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
     * Sets the event value.
     *
     * @param inEvent an <code>Event</code> value
     */
    public void setEvent(Event inEvent)
    {
        event = inEvent;
    }
    /**
     * Create a new SimpleGeneratedMarketDataEvent instance.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     * @param inEvent an <code>Event</code> value
     */
    public SimpleGeneratedMarketDataEvent(String inMarketDataRequestId,
                                          Event inEvent)
    {
        marketDataRequestId = inMarketDataRequestId;
        event = inEvent;
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
    private Event event;
}
