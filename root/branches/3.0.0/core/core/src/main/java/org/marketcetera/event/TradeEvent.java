package org.marketcetera.event;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Represents a Trade for a given security at a specific time.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TradeEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
@ClassVersion("$Id: TradeEvent.java 16063 2012-01-31 18:21:55Z colin $")
public interface TradeEvent
        extends MarketDataEvent
{
    /**
     * Gets the time the event occurred. 
     *
     * <p>The format of the returned value is dependent on the
     * originating market data provider.
     * 
     * <p>This is the same as {@link #getExchangeTimestamp()}.
     *
     * @return a <code>String</code> value
     */
    public String getTradeDate();
}
