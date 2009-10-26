package org.marketcetera.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a {@link TopOfBookEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
final class TopOfBookEventImpl
        implements TopOfBookEvent, HasEventBean
{
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
     * @see org.marketcetera.event.beans.HasEventBean#getEventBean()
     */
    @Override
    public EventBean getEventBean()
    {
        return event;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TopOfBook#getAsk()
     */
    @Override
    public AskEvent getAsk()
    {
        return ask;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TopOfBook#getBid()
     */
    @Override
    public BidEvent getBid()
    {
        return bid;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AggregateEvent#decompose()
     */
    @Override
    public List<Event> decompose()
    {
        List<Event> output = new ArrayList<Event>();
        if(bid != null) {
            output.add(bid);
        }
        if(ask != null) {
            output.add(ask);
        }
        return Collections.unmodifiableList(output);
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        return EventServices.eventHashCode(this);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj)
    {
        return EventServices.eventEquals(this,
                                         obj);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("TopOfBook for %s at %s: %s %s-%s %sx%s %s", //$NON-NLS-1$
                             getInstrument(),
                             DateUtils.dateToString(getTimestamp()),
                             getExchange(bid),
                             getPriceAsString(bid),
                             getPriceAsString(ask),
                             getSizeAsString(bid),
                             getSizeAsString(ask),
                             getExchange(ask));
    }
   /**
     * Create a new TopOfBookImpl instance.
     *
     * @param inEvent an <code>EventBean</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inBid a <code>BidEvent</code> value or <code>null</code>
     * @param inAsk an <code>AskEvent</code> value or <code>null</code>
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if an entry in the list is for a different instrument than the one given
     */
    TopOfBookEventImpl(EventBean inEvent,
                       Instrument inInstrument,
                       BidEvent inBid,
                       AskEvent inAsk)
    {
        event = EventBean.copy(inEvent);
        event.setDefaults();
        event.validate();
        if(inInstrument == null) {
            EventServices.error(VALIDATION_NULL_INSTRUMENT);
        }
        instrument = inInstrument;
        bid = inBid;
        ask = inAsk;
        if(bid != null &&
           !bid.getInstrument().equals(instrument)) {
            EventServices.error(new I18NBoundMessage2P(VALIDATION_BID_INCORRECT_INSTRUMENT,
                                                       bid.getInstrument(),
                                                       instrument));
        }
        if(ask != null &&
           !ask.getInstrument().equals(instrument)) {
            EventServices.error(new I18NBoundMessage2P(VALIDATION_ASK_INCORRECT_INSTRUMENT,
                                                       ask.getInstrument(),
                                                       instrument));
        }
    }
    /**
     * Returns the exchange of the given event or null. 
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>String</code> value
     */
    private static String getExchange(QuoteEvent inEvent)
    {
        if(inEvent != null) {
            return inEvent.getExchange();
        }
        return null;
    }
    /**
     * Returns the price of the given event as a <code>String</code>.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>String</code> value
     */
    private static String getPriceAsString(QuoteEvent inEvent)
    {
        if(inEvent != null) {
            return inEvent.getPrice().toPlainString();
        }
        return NO_QUANTITY;
    }
    /**
     * Returns the size of the given event as a <code>String</code>.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>String</code> value
     */
    private static String getSizeAsString(QuoteEvent inEvent)
    {
        if(inEvent != null) {
            return inEvent.getSize().toPlainString();
        }
        return NO_QUANTITY;
    }
   /**
     * the event attributes 
     */
    private final EventBean event;
    /**
     * the event instrument
     */
    private final Instrument instrument;
    /**
     * the top bid or <code>null</code>
     */
    private final BidEvent bid;
    /**
     * the top ask or <code>null</code>
     */
    private final AskEvent ask;
    /**
     * value to display if the source is null
     */
    private static String NO_QUANTITY = "---"; //$NON-NLS-1$
    private static final long serialVersionUID = 1L;
}
