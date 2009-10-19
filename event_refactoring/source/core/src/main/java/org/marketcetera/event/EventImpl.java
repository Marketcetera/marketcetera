package org.marketcetera.event;

import java.util.Date;

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
        return messageId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return timestamp.getTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return source;
    }
    /**
     * 
     *
     *
     * @param inSource
     */
    public void setSource(Object inSource)
    {
        source = inSource;
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
        messageId = inMessageId;
        timestamp = inTimestamp;
        validate();
    }
    private final long messageId;
    private final Date timestamp;
    private volatile Object source;
    private static final long serialVersionUID = 1L;
}
