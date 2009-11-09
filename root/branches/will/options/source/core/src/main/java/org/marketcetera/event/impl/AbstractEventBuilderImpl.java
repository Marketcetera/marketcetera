package org.marketcetera.event.impl;

import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

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
@NotThreadSafe
@ClassVersion("$Id$")
abstract class AbstractEventBuilderImpl<E extends Event>
        implements EventBuilder<E>
{
    /**
     * Sets the message id to use with the new event. 
     *
     * @param inMessageId a <code>long</code> value
     * @return an <code>AbstractEventBuilderImpl</code> value
     */
    public AbstractEventBuilderImpl<E> withMessageId(long inMessageId)
    {
        event.setMessageId(inMessageId);
        return this;
    }
    /**
     * Sets the timestamp value to use with the new event.
     *
     * @param inTimestamp a <code>Date</code> value or <code>null</code>
     * @return an <code>AbstractEventBuilderImpl</code> value
     */
    public AbstractEventBuilderImpl<E> withTimestamp(Date inTimestamp)
    {
        event.setTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the source value to use with the new event.
     *
     * @param inSource an <code>Object</code> value or <code>null</code>
     * @return an <code>AbstractEventBuilderImpl</code> value
     */
    public AbstractEventBuilderImpl<E> withSource(Object inSource)
    {
        event.setSource(inSource);
        return this;
    }
    /**
     * Get the event value.
     *
     * @return an <code>EventBean</code> value
     */
    protected final EventBean getEvent()
    {
        return event;
    }
    /**
     * the event attributes
     */
    private final EventBean event = new EventBean();
}
