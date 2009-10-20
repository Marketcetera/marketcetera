package org.marketcetera.event.impl;

import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.Event;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link Event}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
class EventImpl
        implements Event
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public final long getMessageId()
    {
        return event.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public final Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public final long getTimeMillis()
    {
        return event.getTimestamp().getTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public final Object getSource()
    {
        return event.getSource();
    }
    /**
     * Sets the source value.
     *
     * @param an <code>Object</code> value or null
     */
    public final void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (event.getMessageId() ^ (event.getMessageId() >>> 32));
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
        EventImpl other = (EventImpl) obj;
        if (event.getMessageId() != other.getMessageId())
            return false;
        return true;
    }
    /**
     * Create a new EventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     */
    protected EventImpl(long inMessageId,
                        Date inTimestamp)
    {
        event.setMessageId(inMessageId);
        event.setTimestamp(inTimestamp);
        event.validate();
    }
    /**
     * the event attributes 
     */
    private final EventBean event = new EventBean();
    private static final long serialVersionUID = 1L;
}
