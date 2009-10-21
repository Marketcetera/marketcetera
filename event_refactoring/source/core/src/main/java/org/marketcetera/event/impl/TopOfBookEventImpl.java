package org.marketcetera.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.beans.EventBean;
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
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ask == null) ? 0 : ask.hashCode());
        result = prime * result + ((bid == null) ? 0 : bid.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopOfBookEventImpl other = (TopOfBookEventImpl) obj;
        if (ask == null) {
            if (other.ask != null)
                return false;
        } else if (!ask.equals(other.ask))
            return false;
        if (bid == null) {
            if (other.bid != null)
                return false;
        } else if (!bid.equals(other.bid))
            return false;
        return true;
    }
    /**
     * Create a new TopOfBookImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inBid a <code>BidEvent</code> value or <code>null</code>
     * @param inAsk an <code>AskEvent</code> value or <code>null</code>
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     */
    TopOfBookEventImpl(long inMessageId,
                       Date inTimestamp,
                       BidEvent inBid,
                       AskEvent inAsk)
    {
        event.setMessageId(inMessageId);
        event.setTimestamp(inTimestamp);
        event.setDefaults();
        event.validate();
        bid = inBid;
        ask = inAsk;
    }
    /**
     * the event attributes 
     */
    private final EventBean event = new EventBean();
    /**
     * the top bid or <code>null</code>
     */
    private final BidEvent bid;
    /**
     * the top ask or <code>null</code>
     */
    private final AskEvent ask;
    private static final long serialVersionUID = 1L;
}
