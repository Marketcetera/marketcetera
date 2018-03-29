package org.marketcetera.client;

/* $License$ */

/**
 * Publishes events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EventPublisher
{
    /**
     * Add the given event listener.
     *
     * @param inEventListener an <code>EventListener</code> value
     */
    public void addEventListener(EventListener inEventListener);
    /**
     * Remove the given event listener.
     *
     * @param inEventListener an <code>EventListener</code> vlue
     */
    public void removeEventListener(EventListener inEventListener);
}
