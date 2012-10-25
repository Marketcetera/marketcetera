package org.marketcetera.marketdata;

import java.util.Collection;

import org.marketcetera.marketdata.events.Event;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataManager
{
    public Collection<Event> requestMarketData(MarketDataRequest inRequest);
}
