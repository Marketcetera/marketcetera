package org.marketcetera.core.event;

import java.util.List;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * An aggregation of {@link Event} objects that represents a market data provider data output. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AggregateEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
@ClassVersion("$Id: AggregateEvent.java 16063 2012-01-31 18:21:55Z colin $")
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
