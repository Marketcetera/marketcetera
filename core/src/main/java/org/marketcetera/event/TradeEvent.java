package org.marketcetera.event;

import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a Trade for a given security at a specific time.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
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
     * @return a <code>Date</code> value
     */
    public Date getTradeDate();
    /**
     * Gets the trade condition codes, if any.
     *
     * @return a <code>String</code> value
     */
    public String getTradeCondition();
}
