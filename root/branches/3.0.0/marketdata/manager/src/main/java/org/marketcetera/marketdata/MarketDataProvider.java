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
public interface MarketDataProvider
{
    public Collection<Event> requestMarketData(MarketDataRequest inRequest,
                                               MarketDataToken inToken);
    public Collection<Event> requestMarketData(MarketDataRequest inRequest);
}
