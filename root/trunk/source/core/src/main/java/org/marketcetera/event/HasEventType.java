package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implements has an <code>EventType</code> attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
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
