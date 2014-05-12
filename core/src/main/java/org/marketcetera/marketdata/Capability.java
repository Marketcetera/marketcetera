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
     * the market data feed is able to return top 10 aggregated quotes
     */
    BBO10,
    /**
     * the market data feed is able to return statistical data
     */
    MARKET_STAT,
    /**
     * the market data feed is able to return the latest trade
     */
    LATEST_TICK,
    /**
     * the market data feed is able to return dividend information
     */
    DIVIDEND,
    /**
     * the market data feed is able to return national best bid and offer
     */
    NBBO,
    /**
     * the market data feed is able to identify event boundaries
     */
    EVENT_BOUNDARY,
    /**
     * the market data feed is able to return price level depth of book of an unspecified type
     */
    AGGREGATED_DEPTH,
    /**
     * the market data feed is able to return order level depth of book of an unspecified type
     */
    UNAGGREGATED_DEPTH,
    /**
     * the market data feed is able to return imbalance events
     */
    IMBALANCE
}
