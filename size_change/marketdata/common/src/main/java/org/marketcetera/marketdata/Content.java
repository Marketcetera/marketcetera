package org.marketcetera.marketdata;

import java.util.EnumSet;
import java.util.Set;

/**
 * The content types for market data requests.
 * 
 * <p>In this context, <em>content</em> refers to the type of market data request.
 *
 * @version $Id: Content.java 16324 2012-10-25 19:10:42Z colin $
 * @since 1.5.0
 */
public enum Content
{
    /**
     * best-bid-and-offer only
     */
    TOP_OF_BOOK,
    /**
     * Statistics for the symbol, as available
     */
    MARKET_STAT,
    /**
     * generic depth-of-book, aggregated per price level
     */
    AGGREGATED_DEPTH,
    /**
     * generic depth-of-book, unaggregated
     */
    UNAGGREGATED_DEPTH,
    /**
     * specialized NASDAQ unaggregated depth
     */
    TOTAL_VIEW,
    /**
     * specialized NYSE unaggregated depth
     */
    OPEN_BOOK,
    /**
     * specialized NASDAQ aggregation depth
     */
    LEVEL_2,
    /**
     * latest trade
     */
    LATEST_TICK,
    /**
     * dividend data
     */
    DIVIDEND;
    /**
     * Indicates if this <code>Content</code> type represents a depth-of-book view.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isDepth()
    {
        return depth.contains(this);
    }
    /**
     * content types that represent a depth-of-book view
     */
    private static final Set<Content> depth = EnumSet.of(AGGREGATED_DEPTH,UNAGGREGATED_DEPTH,TOTAL_VIEW,OPEN_BOOK,LEVEL_2);
}