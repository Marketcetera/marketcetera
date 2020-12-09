package org.marketcetera.photon.commons.events;

import com.google.common.eventbus.EventBus;

/* $License$ */

/**
 * Provides an event bus for Photon events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PhotonEventBus
{
    /**
     * Register the given object for events.
     *
     * @param inObject an <code>Object</code> value
     */
    public static void register(Object inObject)
    {
        eventBus.register(inObject);
    }
    /**
     * Unregister the given object for events.
     *
     * @param inObject an <code>Object</code> value
     */
    public static void unregister(Object inObject)
    {
        eventBus.unregister(inObject);
    }
    /**
     * Post the given event.
     *
     * @param inEvent an <code>Event</code> value
     */
    public static void post(Object inEvent)
    {
        eventBus.post(inEvent);
    }
    /**
     * Create a new PhotonEventBus instance.
     */
    private PhotonEventBus()
    {
        throw new UnsupportedOperationException();
    }
    /**
     * manages events and event subscriptions
     */
    private static final EventBus eventBus = new EventBus();
}
