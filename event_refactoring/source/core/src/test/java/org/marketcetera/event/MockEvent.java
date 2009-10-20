package org.marketcetera.event;

import java.util.Date;

import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.impl.EventValidationException;
import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockEvent
        implements Event
{
    /**
     * Create a new MockEvent instance.
     * @throws EventValidationException 
     */
    public MockEvent()
        throws EventValidationException
    {
        this(System.nanoTime(),
             System.currentTimeMillis());
    }
    /**
     * Create a new MockEvent instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @throws EventValidationException 
     */
    public MockEvent(long inMessageId,
                     long inTimestamp)
        throws EventValidationException
    {
        event.setMessageId(inMessageId);
        event.setTimestamp(new Date(inTimestamp));
    }
    /**
     * Create a new MockEvent instance.
     *
     * @param inRequest
     * @throws EventValidationException 
     */
    public MockEvent(MarketDataRequest inRequest)
        throws EventValidationException
    {
        this(System.nanoTime(),
             System.currentTimeMillis());
        setSource(inRequest);
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
     * 
     */
    private final EventBean event = new EventBean();
    private static final long serialVersionUID = 1L;
}
