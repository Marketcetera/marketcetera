package org.marketcetera.client;

import org.marketcetera.event.Event;

/* $License$ */

/**
 * Receives incoming events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EventListener
{
    /**
     * Receive the given incoming event.
     *
     * @param inEvent an <code>Event</code> value
     */
    void receiveEvent(Event inEvent);
}
