package org.marketcetera.core.event.impl;

import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;
import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.beans.EventBean;

/* $License$ */

/**
 * Provides event builder utilities for subclasses of {@link org.marketcetera.core.event.Event}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractEventBuilderImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id: AbstractEventBuilderImpl.java 16063 2012-01-31 18:21:55Z colin $")
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
