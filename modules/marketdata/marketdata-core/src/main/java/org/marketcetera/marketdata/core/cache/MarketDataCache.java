package org.marketcetera.marketdata.core.cache;

import java.util.List;

import org.marketcetera.core.Cacheable;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Optional;

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
        extends Cacheable
{
    /**
     * Gets a snapshot of the given content for the given instrument/exchange, if available.
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
     * @param inExchange a <code>String</code> value or <code>null</code>
     * @return an <code>Optional&lt;Event&gt;</code> value
     */
    Optional<Event> getSnapshot(Instrument inInstrument,
                                Content inContent,
                                String inExchange);
    /**
     * Update the cache with the given events for the given content.
     *
     * @param inContent a <code>Content</code> value
     * @param inEvents an <code>Event...</code> value
     * @return a <code>List&lt;Event&gt;</code> value containing the net change represented by the given update
     */
    List<Event> update(Content inContent,
                       Event...inEvents);
    /**
     * Invalidates the cached data for the given instrument and exchange.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     */
    void invalidate(Instrument inInstrument,
                    String inExchange);
}
