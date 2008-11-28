package org.marketcetera.event;

import java.util.Comparator;
import java.util.Date;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Base class for all market events.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class EventBase implements TimestampCarrier
{
    /**
     * unique identifier for this market event
     */
    private final long messageId;
    /**
     * milliseconds since EPOCH in GMT
     */
    private final long timestamp; //i18n_datetime use Date instead?
    /**
     * Create a new EventBase instance.
     *
     * @param messageId a <code>long</code> value uniquely identifying this market event
     * @param timestamp a <code>long</code> value expressing the time this event occurred in milliseconds since
     *   EPOCH in GMT
     */
    protected EventBase(long messageId, 
                        long timestamp) 
    {
        this.messageId = messageId;
        this.timestamp = timestamp;
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
        return new Date(getTimeMillis()); //non-i18n
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
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
    public final boolean equals(Object obj)
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
     * Compares two events based on their timestamps.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
   @ClassVersion("$Id$") //$NON-NLS-1$
   public final static class BookAgeComparator
       implements Comparator<EventBase>
   {
       public static final BookAgeComparator NewestToOldestComparator = new BookAgeComparator(false);
       public static final BookAgeComparator OldestToNewestComparator = new BookAgeComparator(true);
       private final boolean mIsAscending;
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
