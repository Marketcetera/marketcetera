package org.marketcetera.marketdata.core.cache;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Caches market data and makes it available to queries.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface MarketDataCache
{
    /**
     * Gets a snapshot of the given content for the given instrument, if available.
     * 
     * <p>This method returns the most recently collected market data for the given request attributes.
     * The data returned must never be stale, though it may be arbitrarily old as long as no new
     * data is not represented in the returned value. For example, the most recent trade for a given
     * instrument may have occurred some time ago (arbitrarily old) but this method must never return
     * a trade that is not the most recent. Implementers guarantee to return nothing (<code>null</code>)
     * rather than return data that is not certain to be the most recent available.
     * 
     * <p>There is no guarantee that the implementer will actively fetch new market data if it is not
     * already available. The implemented <em>may</em> do this, or it may passively reflect the most
     * recent data collected through other means.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @return an <code>Event</code> value or <code>null</code>
     */
    public Event getSnapshot(Instrument inInstrument,
                             Content inContent);
}
