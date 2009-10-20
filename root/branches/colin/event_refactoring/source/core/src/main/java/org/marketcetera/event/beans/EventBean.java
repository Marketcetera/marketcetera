package org.marketcetera.event.beans;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.Event;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link Event}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class EventBean
        implements Serializable
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
     * the event messageId
     */
    private volatile long messageId;
    /**
     * the event timestamp
     */
    private volatile Date timestamp;
    /**
     * the event source
     */
    private volatile Object source;
    private static final long serialVersionUID = 1L;
}
