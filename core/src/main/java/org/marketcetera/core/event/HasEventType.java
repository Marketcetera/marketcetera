package org.marketcetera.core.event;

/* $License$ */

/**
 * Indicates that the implements has an <code>EventType</code> attribute.
 *
 * @version $Id$
 * @since 2.1.0
 */
public interface HasEventType
{
    /**
     * Gets the type of the event.
     *
     * @return an <code>EventType</code> value
     */
    public EventType getEventType();
    /**
     * Sets the type of the event.
     *
     * @param inEventType an <code>EventType</code> value
     */
    public void setEventType(EventType inEventType);
}
