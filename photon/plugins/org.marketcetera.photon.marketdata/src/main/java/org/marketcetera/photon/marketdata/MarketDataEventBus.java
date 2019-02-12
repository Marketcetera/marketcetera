package org.marketcetera.photon.marketdata;

import com.google.common.eventbus.EventBus;

/* $License$ */

/**
 * Provides event bus services for market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class MarketDataEventBus
{
    /**
     * Register the given object to receive events.
     *
     * @param inSubscriber an <code>Object</code> value
     */
    public static void register(Object inSubscriber)
    {
        marketDataEventBus.register(inSubscriber);
    }
    /**
     * Unregister the given object from receiving events.
     *
     * @param inSubscriber an <code>Object</code> value
     */
    public static void unregister(Object inSubscriber)
    {
        marketDataEventBus.unregister(inSubscriber);
    }
    /**
     * Post the given event to the event bus.
     *
     * @param inEvent an <code>Object</code> value
     */
    public static void post(Object inEvent)
    {
        marketDataEventBus.post(inEvent);
    }
    /**
     * underlying event bus
     */
    private static final EventBus marketDataEventBus = new EventBus();
}
