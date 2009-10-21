package org.marketcetera.event.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.Event;
import org.marketcetera.event.Messages;
import org.marketcetera.event.util.EventValidationServices;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link Event}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public class EventBean
        implements Serializable, Messages
{
    /**
     * Get the messageId value.
     *
     * @return a <code>long</code> value
     */
    public final long getMessageId()
    {
        return messageId;
    }
    /**
     * Sets the messageId value.
     *
     * @param a <code>long</code> value
     */
    public final void setMessageId(long inMessageId)
    {
        messageId = inMessageId;
    }
    /**
     * Get the timestamp value.
     *
     * @return a <code>Date</code> value
     */
    public final Date getTimestamp()
    {
        return timestamp;
    }
    /**
     * Get the timestamp value as millis.
     *
     * @return a <code>long</code> value
     * @throws NullPointerException if the timestamp value has not been set with
     *  {@link #setTimestamp(Date)}
     */
    public final long getTimeMillis()
    {
        return getTimestamp().getTime();
    }
    /**
     * Sets the timestamp value.
     *
     * @param a <code>Date</code> value
     */
    public final void setTimestamp(Date inTimestamp)
    {
        timestamp = inTimestamp;
    }
    /**
     * Get the source value.
     *
     * @return a <code>Object</code> value
     */
    public final Object getSource()
    {
        return source;
    }
    /**
     * Sets the source value.
     *
     * @param a <code>Object</code> value
     */
    public final void setSource(Object inSource)
    {
        source = inSource;
    }
    /**
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     *  their attributes as necessary and invoke the parent method.
     * @throws IllegalArgumentException if {@link #timestamp} is <code>null</code>
     * @throws IllegalArgumentException if {@link #messageId} &lt; 0
     */
    public void validate()
    {
        if(timestamp == null) {
            EventValidationServices.error(VALIDATION_NULL_TIMESTAMP);
        }
        if(messageId < 0) {
            EventValidationServices.error(new I18NBoundMessage1P(VALIDATION_INVALID_MESSAGEID,
                                                                 messageId));
        }
    }
    /**
     * Sets the attributes to their appropriate default values, if any.
     *
     * <p>Subclasses should override this method to set default values to
     *  their attributes as necessary and invoke the parent method.
     */
    public void setDefaults()
    {
        if(messageId == Long.MIN_VALUE) {
            messageId = counter.incrementAndGet();
        }
        if(timestamp == null) {
            timestamp = new Date();
        }
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
        EventBean other = (EventBean) obj;
        if (messageId != other.messageId)
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("EventBean [messageId=").append(messageId).append(", source=").append(source) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", timestamp=").append(timestamp).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
    /**
     * the event messageId
     */
    private long messageId = Long.MIN_VALUE;
    /**
     * the event timestamp
     */
    private Date timestamp = null;
    /**
     * the event source
     */
    private Object source;
    /**
     * counter used to assign default values
     */
    private static final AtomicLong counter = new AtomicLong(0);
    private static final long serialVersionUID = 1L;
}
