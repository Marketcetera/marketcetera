package org.marketcetera.strategyengine.client;

/* $License$ */

/**
 * Manages connection status listeners.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ConnectionStatusPublisher
{
    /**
     * Adds a connection status listener so that it can receive connection
     * status notifications.
     * <p>
     * If the same listener is added more than once, it will receive
     * notifications as many times as it's been added.
     * <p>
     * The listeners are notified in the reverse order of their addition.
     *
     * @param inListener the listener to add. Cannot be null.
     */
    void addConnectionStatusListener(ConnectionStatusListener inListener);
    /**
     * Removes a connection status listener that was added previously so that
     * it no longer receives connection status notifications.
     * <p>
     * If the listener was added more than once, only its most
     * recently added occurrence will be removed.
     *
     * @param inListener the listener to remove. Cannot be null.
     */
    void removeConnectionStatusListener(ConnectionStatusListener inListener);
}
