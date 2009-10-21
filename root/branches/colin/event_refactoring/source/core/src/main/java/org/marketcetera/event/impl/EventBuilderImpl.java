package org.marketcetera.event.impl;

import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.Event;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides event builder utilities for subclasses of {@link Event}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
abstract class EventBuilderImpl
{
    /**
     * Sets the message id to use with the new event. 
     *
     * @param inMessageId a <code>long</code> value
     * @return an <code>EventBuilderImpl</code> value
     */
    public EventBuilderImpl withMessageId(long inMessageId)
    {
        event.setMessageId(inMessageId);
        return this;
    }
    /**
     * Sets the timestamp value to use with the new event.
     *
     * @param inTimestamp a <code>Date</code> value
     * @return an <code>EventBuilderImpl</code> value
     */
    public EventBuilderImpl withTimestamp(Date inTimestamp)
    {
        event.setTimestamp(inTimestamp);
        return this;
    }
    /**
     * Get the messageId value.
     *
     * @return a <code>long</code> value
     */
    protected final long getMessageId()
    {
        return event.getMessageId();
    }
    /**
     * Get the timestamp value.
     *
     * @return a <code>Date</code> value
     */
    protected final Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /**
     * the event attributes
     */
    private final EventBean event = new EventBean();
}
