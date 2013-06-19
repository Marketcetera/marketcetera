package org.marketcetera.core.event;

import java.util.List;

/* $License$ */

/**
 * An aggregation of {@link Event} objects that represents a market data provider data output. 
 *
 * @version $Id$
 * @since 1.5.0
 */
public interface AggregateEvent
        extends Event, HasInstrument
{
    /**
     * Produces a list of <code>QuoteEvent</code> objects that describe
     * this <code>AggregateEvent</code>.
     * 
     * @return a <code>List&lt;QuoteEvent&gt;</code> value
     */
    public List<QuoteEvent> decompose();
}
