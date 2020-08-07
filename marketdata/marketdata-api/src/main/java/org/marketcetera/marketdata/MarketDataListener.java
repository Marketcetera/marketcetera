package org.marketcetera.marketdata;

import org.marketcetera.event.Event;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Receives market data events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataListener
{
    /**
     * Receive the given market data event.
     *
     * @param inEvent an <code>Event</code> value
     */
    default void receiveMarketData(Event inEvent) {}
    /**
     * Receive the given error that occurred.
     *
     * @param inThrowable a <code>Throwable</code>
     */
    default void onError(Throwable inThrowable) {}
    /**
     * Receive the given error message that occurred.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    default void onError(I18NBoundMessage inMessage) {}
}
