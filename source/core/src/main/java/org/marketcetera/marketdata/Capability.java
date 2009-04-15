package org.marketcetera.marketdata;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Enumeration of capabilities of {@link MarketDataFeed} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public enum Capability
{
    /**
     * the market data feed capabilities are unknown at this time
     */
    UNKNOWN,
    /**
     * the market data feed is able to return top-of-book data
     */
    TOP_OF_BOOK,
    /**
     * the market data feed is able to return NYSE OpenBook data
     */
    OPEN_BOOK,
    /**
     * the market data feed is able to return NASDAQ TotalView data
     */
    TOTAL_VIEW,
    /**
     * the market data feed is able to return NASDAQ Level II data
     */
    LEVEL_2,
    /**
     * the market data feed is able to return statistical data
     */
    MARKET_STAT,
    /**
     * the market data feed is able to return the latest trade
     */
    LATEST_TICK
}
