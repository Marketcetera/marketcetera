package org.marketcetera.marketdata.event;

import java.util.Deque;

import org.marketcetera.event.Event;

/* $License$ */

/**
 * Indicates that some market data events have been created in response to a particular market data request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface GeneratedMarketDataEvent
        extends HasMarketDataRequestId
{
    /**
     * Get the market data event values.
     *
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    Deque<Event> getEvents();
}
