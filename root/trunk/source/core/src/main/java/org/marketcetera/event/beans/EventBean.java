package org.marketcetera.event.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.Event;
import org.marketcetera.event.Messages;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link Event}.
 *
 * <p>This class is not thread-safe because {@link #validate()} and
 * {@link #setDefaults()} need a consistent state which is not guaranteed
 * by design.  In  order to make the state consistent, all methods would
 * need to be made synchronized, or all attributes volatile and <code>validate</code>
 * and <code>setDefaults</code> synchronized.  Therefore, the easiest way
 * to achieve thread-safety is to externally synchronize instances of this
 * object.  There is another path to thread-safety that is more complicated
 * to arrange and does not provide a cast-iron guarantee from the point-of-view
 * of this class.  This class may be used in a thread-safe manner by invoking
 * <code>validate</code> and <code>setDefaults</code> <em>only</em> during instantiation of
 * an owning object and forbidding mutation of any attribute except via {@link #setSource(Object)}.
 * This is the intended use of this object. 
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
     * Creates a shallow copy of the given <code>EventBean</code>.
     *
     * @param inBean an <code>EventBean</code> value
     * @return an <code>EventBean</code> value
     */
    public static EventBean copy(EventBean inBean)
    {
        EventBean newBean = new EventBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
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
     * <p>If the given <code>inMessageId</code> is equal to
     * {@link Long#MIN_VALUE}, the object <code>MessageId</code>
     * will be assigned a unique value if {@link #setDefaults()}
     * is invoked.
     *
     * @param inMessageId a <code>long</code> value
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
     * @param inTimestamp a <code>Date</code> value
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
     * @param inSource a <code>Object</code> value
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
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     */
    public void validate()
    {
        if(messageId < 0) {
            EventServices.error(new I18NBoundMessage1P(VALIDATION_INVALID_MESSAGEID,
                                                                 messageId));
        }
        if(timestamp == null) {
            EventServices.error(VALIDATION_NULL_TIMESTAMP);
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
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EventBean)) {
            return false;
        }
        EventBean other = (EventBean) obj;
        if (messageId != other.messageId) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Event: [%s with source %s at %s]", //$NON-NLS-1$
                             messageId,
                             source,
                             timestamp);
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor an <code>EventBean</code> value
     * @param inRecipient an <code>EventBean</code> value
     */
    protected static void copyAttributes(EventBean inDonor,
                                         EventBean inRecipient)
    {
        inRecipient.setMessageId(inDonor.getMessageId());
        inRecipient.setSource(inDonor.getSource());
        inRecipient.setTimestamp(inDonor.getTimestamp());
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
    private transient volatile Object source;
    /**
     * counter used to assign default values
     */
    private static final AtomicLong counter = new AtomicLong(0);
    private static final long serialVersionUID = 1L;
}
