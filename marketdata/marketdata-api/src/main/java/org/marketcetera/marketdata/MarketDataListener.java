package org.marketcetera.marketdata;

import org.marketcetera.event.Event;

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
    void receiveMarketData(Event inEvent);
}
