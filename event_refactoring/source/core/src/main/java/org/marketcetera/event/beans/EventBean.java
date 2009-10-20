package org.marketcetera.event.beans;

import java.util.Date;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventBean
{
    /**
     * Get the messageId value.
     *
     * @return a <code>long</code> value
     */
    public long getMessageId()
    {
        return messageId;
    }
    /**
     * Sets the messageId value.
     *
     * @param a <code>long</code> value
     */
    public void setMessageId(long inMessageId)
    {
        messageId = inMessageId;
    }
    /**
     * Get the timestamp value.
     *
     * @return a <code>Date</code> value
     */
    public Date getTimestamp()
    {
        return timestamp;
    }
    /**
     * Get the timstamp value as a long.
     *
     * @return a <code>long</code> value
     */
    public long getTimeMillis()
    {
        if(timestamp != null) {
            return timestamp.getTime();
        }
        return 0;
    }
    /**
     * Sets the timestamp value.
     *
     * @param a <code>Date</code> value
     */
    public void setTimestamp(Date inTimestamp)
    {
        timestamp = inTimestamp;
    }
    /**
     * Get the source value.
     *
     * @return a <code>Object</code> value
     */
    public Object getSource()
    {
        return source;
    }
    /**
     * Sets the source value.
     *
     * @param a <code>Object</code> value
     */
    public void setSource(Object inSource)
    {
        source = inSource;
    }
    /**
     * 
     */
    private long messageId;
    /**
     * 
     */
    private Date timestamp;
    /**
     * 
     */
    private volatile Object source;
}
