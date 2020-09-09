package org.marketcetera.event;

import org.marketcetera.event.beans.EventBean;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.time.DateService;

/* $License$ */

/**
 * Test implementation of {@link Event}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class MockEvent
        implements Event
{
    /**
     * Create a new MockEvent instance.
     */
    public MockEvent()
    {
        this(System.nanoTime(),
             System.currentTimeMillis());
    }
    /**
     * Create a new MockEvent instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>long</code> value
     */
    public MockEvent(long inMessageId,
                     long inTimestamp)
    {
        event.setMessageId(inMessageId);
        event.setTimestamp(DateService.toLocalDateTime(inTimestamp));
    }
    /**
     * Create a new MockEvent instance.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     */
    public MockEvent(MarketDataRequest inRequest)
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
    public java.time.LocalDateTime getTimestamp()
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
     * @see org.marketcetera.event.Event#getProvider()
     */
    @Override
    public String getProvider()
    {
        return event.getProvider();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setProvider(java.lang.String)
     */
    @Override
    public void setProvider(String inProvider)
    {
        event.setProvider(inProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        java.time.LocalDateTime timestamp = event.getTimestamp();
        if(timestamp == null) {
            return -1;
        }
        return DateService.toEpochMillis(event.getTimestamp());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#getRequestId()
     */
    @Override
    public long getRequestId()
    {
        return event.getRequestId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#setRequestId(long)
     */
    @Override
    public void setRequestId(long inRequestId)
    {
        event.setRequestId(inRequestId);
    }
    /**
     * holds event attributes
     */
    private final EventBean event = new EventBean();
    private static final long serialVersionUID = 6846991271868272293L;
}
