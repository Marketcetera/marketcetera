package org.marketcetera.eventbus;

/* $License$ */

/**
 * Provides a test event implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventBusTestEvent
{
    /**
     * Mark the test event as received.
     *
     * @return a <code>long</code> value
     */
    public long receive()
    {
        receive = System.nanoTime();
        return receive-post;
    }
    /**
     * Post this event.
     *
     * @return an <code>EventBusTestEvent</code> value
     */
    public EventBusTestEvent post()
    {
        post = System.nanoTime();
        return this;
    }
    /**
     * time when event was posted
     */
    private long post = System.nanoTime();
    /**
     * time when event was received
     */
    private long receive;
}
