package org.marketcetera.eventbus;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/* $License$ */

/**
 * Provides a test event implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventBusTestEvent
        implements Comparable<EventBusTestEvent>
{
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(EventBusTestEvent inO)
    {
        return new CompareToBuilder().append(inO.id,id).toComparison();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(id).toHashCode();
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
        EventBusTestEvent other = (EventBusTestEvent) obj;
        return new EqualsBuilder().append(other.id,id).isEquals();
    }
    /**
     * Mark the test event as received.
     *
     * @return a <code>long</code> value
     */
    public long receive()
    {
        receive = System.nanoTime();
        return receive-post;
    }
    /**
     * Post this event.
     *
     * @return an <code>EventBusTestEvent</code> value
     */
    public EventBusTestEvent post()
    {
        
        post = System.nanoTime();
        return this;
    }
    /**
     * time when event was posted
     */
    private long post = System.nanoTime();
    /**
     * time when event was received
     */
    private long receive;
    /**
     * uniquely idenfities event
     */
    private final long id = counter.incrementAndGet();
    /**
     * provides a unique identifier for each event
     */
    private static final AtomicLong counter = new AtomicLong(0);
}
