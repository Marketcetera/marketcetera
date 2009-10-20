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
    public long getMessageId()
    {
        return event.getMessageId();
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
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return event.getTimestamp().getTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return event.getSource();
    }
    /**
     * 
     *
     *
     * @param inSource
     */
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
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
