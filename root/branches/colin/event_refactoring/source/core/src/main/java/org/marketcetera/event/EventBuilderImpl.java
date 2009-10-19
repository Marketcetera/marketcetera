package org.marketcetera.event;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class EventBuilderImpl
{
    /**
     * 
     *
     *
     * @param inMessageId
     * @return
     */
    public EventBuilderImpl withMessageId(long inMessageId)
    {
        messageId = inMessageId;
        return this;
    }
    /**
     * 
     *
     *
     * @param inTimestamp
     * @return
     */
    public EventBuilderImpl withTimestamp(Date inTimestamp)
    {
        timestamp = inTimestamp;
        return this;
    }
    /**
     * Get the messageId value.
     *
     * @return a <code>long</code> value
     */
    protected final long getMessageId()
    {
        return messageId;
    }
    /**
     * Get the timestamp value.
     *
     * @return a <code>Date</code> value
     */
    protected final Date getTimestamp()
    {
        return timestamp;
    }
    /**
     * 
     *
     *
     */
    protected void setDefaults()
    {
        if(messageId == -1) {
            messageId = assignMessageId();
        }
        if(timestamp == null) {
            timestamp = new Date();
        }
    }
    /**
     * 
     *
     *
     * @return
     */
    private long assignMessageId()
    {
        return counter.incrementAndGet();
    }
    /**
     * 
     */
    private long messageId = -1;
    /**
     * 
     */
    private Date timestamp = null;
    private static final AtomicLong counter = new AtomicLong(0);
}
