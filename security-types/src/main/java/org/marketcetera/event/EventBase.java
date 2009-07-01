package org.marketcetera.event;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.io.Serializable;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for all system events.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public abstract class EventBase implements TimestampCarrier, Serializable
{
    private static final long serialVersionUID = 1L;
    /**
     * unique identifier for this market event
     */
    private final long messageId;
    /**
     * milliseconds since EPOCH in GMT
     */
    private final long timestamp;
    /**
     * source of the event, may be null - note that this attribute alone is mutable
     */
    private Object source;
    /**
     * Create a new EventBase instance.
     *
     * @param messageId a <code>long</code> value uniquely identifying this market event
     * @param timestamp a <code>long</code> value expressing the time this event occurred in milliseconds since
     *   EPOCH in GMT
     * @throws IllegalArgumentException if <code>messageId</code> or <code>timestamp</code> &lt; 0
     */
    protected EventBase(long messageId, 
                        long timestamp) 
    {
        if(messageId < 0 ||
           timestamp < 0) {
            throw new IllegalArgumentException();
        }
        this.messageId = messageId;
        this.timestamp = timestamp;
    }
    /**
     * Get the source value.
     *
     * @return an <code>Object</code> value or null
     */
    public Object getSource()
    {
        return source;
    }
    /**
     * Sets the source value.
     *
     * @param an <code>Object</code> value or null
     */
    public void setSource(Object inSource)
    {
        source = inSource;
    }
    /**
     * Returns the unique message identifier.
     *
     * @return a <code>long</code> value
     */
    public long getMessageId() 
    {
        return messageId;
    }
    /**
     * Returns the time the event took place.
     *
     * @return a <code>long</code> value containing the number of milliseconds since EPOCH in GMT
     */
    public long getTimeMillis()
    {
        return timestamp;
    }
    /**
     * Returns the time the event took place expressed as a <code>Date</code>.
     *
     * @return a <code>Date</code> value
     */
    public Date getTimestampAsDate()
    {
        return new Date(getTimeMillis());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (messageId ^ (messageId >>> 32));
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
        final EventBase other = (EventBase) obj;
        if (messageId != other.messageId)
            return false;
        return true;
    }
    /**
     * Assigns an identifier guaranteed to be unique.
     *
     * @return a <code>long</code> value
     */
    static long assignCounter()
    {
        return counter.incrementAndGet();
    }
    /**
     * the counter used to guarantee that aggregate events are distinct from each-other
     */
    private static final AtomicLong counter = new AtomicLong(0);
    /**
     * Compares two events based on their timestamps.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
   @ClassVersion("$Id$")
   public final static class BookAgeComparator
       implements Comparator<EventBase>
   {
       /**
        * <code>Comparator</code> object that sorts from newest to oldest (descending)
        */
       public static final BookAgeComparator NewestToOldestComparator = new BookAgeComparator(false);
       /**
        * <code>Comparator</code> object that sorts from oldest to newest (ascending)
        */
       public static final BookAgeComparator OldestToNewestComparator = new BookAgeComparator(true);
       /**
        * indicates whether the comparator should perform an ascending sort or not
        */
       private final boolean mIsAscending;
       /**
        * Create a new BookAgeComparator instance.
        *
        * @param inIsAscending a <code>boolean</code> value indicating whether the comparator should perform
        *  an ascending sort or not
        */
       private BookAgeComparator(boolean inIsAscending)
       {
           mIsAscending = inIsAscending;
       }
       /* (non-Javadoc)
        * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
        */
       @Override
       public int compare(EventBase inO1,
                          EventBase inO2)
       {
           // invert the result to be returned if necessary to get a descending sort
           int temp = new Long(inO1.getTimeMillis()).compareTo(inO2.getTimeMillis());
           return mIsAscending ? temp : -temp;
       }
   }
}
