package org.marketcetera.event.impl;

import java.util.Date;

import org.marketcetera.event.Event;
import org.marketcetera.event.beans.EventBean;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
     * 
     *
     *
     * @param inSource
     */
    public final void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventImpl other = (EventImpl) obj;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        return true;
    }
    /**
     * 
     *
     *
     * @throws EventValidationException
     */
    void validate()
    {
    }
    /**
     * Create a new EventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @throws EventValidationException 
     */
    protected EventImpl(long inMessageId,
                        Date inTimestamp)
    {
        event.setMessageId(inMessageId);
        event.setTimestamp(inTimestamp);
        validate();
    }
    /**
     * 
     */
    private final EventBean event = new EventBean();
    private static final long serialVersionUID = 1L;
}
