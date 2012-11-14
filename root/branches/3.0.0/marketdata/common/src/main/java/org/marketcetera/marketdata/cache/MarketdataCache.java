package org.marketcetera.marketdata.cache;

import org.marketcetera.core.event.Event;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 * Caches market data and makes it available to queries.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketdataCache
{
    public Event getSnapshot(Instrument inInstrument,
                             Content inContent);
}
