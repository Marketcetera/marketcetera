package org.marketcetera.eventbus.event;

/* $License$ */

/**
 * Receives events from the server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EventbusEventListener
{
    /**
     * Receive the given event.
     *
     * @param inEvent an <code>Event</code> value
     */
    default void receiveEventbusEvent(Event inEvent) {}
}
