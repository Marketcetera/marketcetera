package org.marketcetera.event.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
 * @since 2.0.0
 */
@NotThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class EventBean
        implements Serializable
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
     * Get the provider value.
     *
     * @return a <code>String</code> value
     */
    public String getProvider()
    {
        return provider;
    }
    /**
     * Sets the provider value.
     *
     * @param inProvider a <code>String</code> value
     */
    public void setProvider(String inProvider)
    {
        provider = inProvider;
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
            EventServices.error(new I18NBoundMessage1P(Messages.VALIDATION_INVALID_MESSAGEID,
                                                       messageId));
        }
        if(timestamp == null) {
            EventServices.error(Messages.VALIDATION_NULL_TIMESTAMP);
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
        return new HashCodeBuilder().append(messageId).append(source).append(provider).append(timestamp).toHashCode();
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
        return new EqualsBuilder().append(messageId,other.messageId).append(source,other.source).append(provider,other.provider).append(timestamp,other.timestamp).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Event: [%s with source %s from %s at %s]", //$NON-NLS-1$
                             messageId,
                             source,
                             provider,
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
        inRecipient.setProvider(inDonor.getProvider());
    }
    /**
     * the event messageId
     */
    @XmlAttribute
    private long messageId = Long.MIN_VALUE;
    /**
     * the event timestamp
     */
    @XmlAttribute
    private Date timestamp = null;
    /**
     * the event source
     */
    private transient Object source;
    /**
     * event provider value
     */
    @XmlAttribute
    private String provider;
    /**
     * counter used to assign default values
     */
    private static final AtomicLong counter = new AtomicLong(0);
    private static final long serialVersionUID = -1463953196194132003L;
}
