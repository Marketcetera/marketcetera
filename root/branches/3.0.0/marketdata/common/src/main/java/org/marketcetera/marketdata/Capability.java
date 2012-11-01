package org.marketcetera.marketdata;

/* $License$ */

/**
 * Enumeration of capabilities of {@link MarketDataFeed} objects.
 *
 * @version $Id: Capability.java 16324 2012-10-25 19:10:42Z colin $
 * @since 1.5.0
 */
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
     * the market data feed is able to return depth-of-book aggregated quotes
     */
    AGGREGATED_DEPTH,
    /**
     * the market data feed is able to return depth-of-book unaggregated quotes
     */
    UNAGGREGATED_DEPTH,
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
     * the market data feed is able to identify event boundaries
     */
    EVENT_BOUNDARY
}
