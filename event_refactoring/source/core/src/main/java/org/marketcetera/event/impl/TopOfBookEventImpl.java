package org.marketcetera.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.TopOfBookEvent;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class TopOfBookEventImpl
        implements TopOfBookEvent
{
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
    /**
     * Create a new TopOfBookImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inBid
     * @param inAsk
     */
    TopOfBookEventImpl(long inMessageId,
                       Date inTimestamp,
                       BidEvent inBid,
                       AskEvent inAsk)
    {
        event = new EventImpl(inMessageId,
                              inTimestamp);
        bid = inBid;
        ask = inAsk;
    }
    /**
     * 
     */
    private final EventImpl event;
    /**
     * 
     */
    private final BidEvent bid;
    /**
     * 
     */
    private final AskEvent ask;
    private static final long serialVersionUID = 1L;
}
