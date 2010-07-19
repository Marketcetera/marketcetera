package org.marketcetera.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.SystemUtils;
import org.marketcetera.event.*;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implements {@link DepthOfBookEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
final class DepthOfBookEventImpl
        implements DepthOfBookEvent, HasEventBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.HasEventBean#getEventBean()
     */
    @Override
    public EventBean getEventBean()
    {
        return event;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return event.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return event.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return event.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DepthOfBookEvent#getAsks()
     */
    @Override
    public List<AskEvent> getAsks()
    {
        return Collections.unmodifiableList(asks);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DepthOfBookEvent#getBids()
     */
    @Override
    public List<BidEvent> getBids()
    {
        return Collections.unmodifiableList(bids);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AggregateEvent#decompose()
     */
    @Override
    public List<QuoteEvent> decompose()
    {
        List<QuoteEvent> events = new ArrayList<QuoteEvent>();
        events.addAll(bids);
        events.addAll(asks);
        return Collections.unmodifiableList(events);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return instrument.getSymbol();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("Depth of book for ").append(getInstrument()).append(" at ").append(DateUtils.dateToString(getTimestamp())).append(SystemUtils.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
        output.append(OrderBook.printBook(bids.iterator(),
                                          asks.iterator(),
                                          true));
        return output.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return EventServices.eventHashCode(this);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        return EventServices.eventEquals(this,
                                         obj);
    }
    /**
     * Create a new DepthOfBookEventImpl instance.
     *
     * @param inEvent an <code>EventBean</code> value
     * @param inBids a <code>List&lt;BidEvent&gt;</code> value
     * @param inAsks a <code>List&lt;AskEvent&lt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if any entry in the list is null
     * @throws IllegalArgumentException if an entry in the list is for a different instrument than the one given
     */
    DepthOfBookEventImpl(EventBean inEvent,
                         List<BidEvent> inBids,
                         List<AskEvent> inAsks,
                         Instrument inInstrument)
    {
        event = EventBean.copy(inEvent);
        event.setDefaults();
        event.validate();
        if(inInstrument == null) {
            EventServices.error(VALIDATION_NULL_INSTRUMENT);
        }
        instrument = inInstrument;
        validateList(inBids,
                     inInstrument);
        bids.addAll(inBids);
        validateList(inAsks,
                     inInstrument);
        asks.addAll(inAsks);
    }
    /**
     * Validates the contents of the given list.
     *
     * @param inQuotes a <code>List&lt;? extends QuoteEvent&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @throws IllegalArgumentException if any entry in the list is null
     * @throws IllegalArgumentException if an entry in the list is for a different instrument than the one given
     */
    private static void validateList(List<? extends QuoteEvent> inQuotes,
                                     Instrument inInstrument)
    {
        for(QuoteEvent quote : inQuotes) {
            if(quote == null) {
                EventServices.error(new I18NBoundMessage1P(VALIDATION_LIST_CONTAINS_NULL,
                                                           String.valueOf(inQuotes)));
            }
            if(!inInstrument.equals(quote.getInstrument())) {
                EventServices.error(new I18NBoundMessage2P(VALIDATION_LIST_INCORRECT_INSTRUMENT,
                                                           quote,
                                                           inInstrument));
            }
        }
    }
    /**
     * the instrument attribute 
     */
    private final Instrument instrument;
    /**
     * the event attributes
     */
    private final EventBean event;
    /**
     * the bid side of the book
     */
    private final List<BidEvent> bids = new ArrayList<BidEvent>();
    /**
     * the ask side of the book
     */
    private final List<AskEvent> asks = new ArrayList<AskEvent>();
    private static final long serialVersionUID = 1L;
}
