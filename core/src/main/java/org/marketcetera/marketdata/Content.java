package org.marketcetera.marketdata;

import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.event.*;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The content types for market data requests.
 * 
 * <p>In this context, <em>content</em> refers to the type of market data request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public enum Content
{
    /**
     * best-bid-and-offer only
     */
    TOP_OF_BOOK,
    /**
     * NYSE OpenBook data
     */
    OPEN_BOOK,
    /**
     * Statistics for the symbol, as available
     */
    MARKET_STAT,
    /**
     * NASDAQ TotalView data
     */
    TOTAL_VIEW,
    /**
     * NASDAQ Level II data
     */
    LEVEL_2,
    /**
     * latest trade
     */
    LATEST_TICK,
    /**
     * bbo aggregated to 10 levels
     */
    BBO10,
    /**
     * national best-bid-and-offer top-of-book
     */
    NBBO,
    /**
     * dividend data
     */
    DIVIDEND,
    /**
     * unspecified price-level depth-of-book
     */
    AGGREGATED_DEPTH,
    /**
     * unspecified order-level depth-of-book
     */
    UNAGGREGATED_DEPTH,
    /**
     * imbalance events
     */
    IMBALANCE;
    /**
     * Determines if this content is relevant to the given event class.
     * 
     * <p>In this context, relevance is defined as whether an event of
     * the given class would be appropriate for this type of content.
     * For example, a <code>TradeEvent</code> would not be relevant
     * to {@link #TOP_OF_BOOK} but would be relevant to {@link #LATEST_TICK}. 
     *
     * @param inEventClass a <code>? extends EventBase</code> value
     * @return a <code>boolean</code> value
     * @throws UnsupportedOperationException if the given class is not covered by the logic in this class
     */
    public boolean isRelevantTo(Class<? extends Event> inEventClass)
    {
        switch(this) {
            case TOP_OF_BOOK :
                return QuoteEvent.class.isAssignableFrom(inEventClass);
            case OPEN_BOOK :
                return QuoteEvent.class.isAssignableFrom(inEventClass);
            case MARKET_STAT :
                return (inEventClass.equals(MarketstatEvent.class));
            case TOTAL_VIEW :
                return QuoteEvent.class.isAssignableFrom(inEventClass);
            case LEVEL_2 :
                return QuoteEvent.class.isAssignableFrom(inEventClass);
            case BBO10 :
                return QuoteEvent.class.isAssignableFrom(inEventClass);
            case LATEST_TICK :
                return TradeEvent.class.isAssignableFrom(inEventClass);
            case DIVIDEND :
                return DividendEvent.class.isAssignableFrom(inEventClass);
            case IMBALANCE :
                return ImbalanceEvent.class.isAssignableFrom(inEventClass);
            default :
                throw new UnsupportedOperationException();
        }
    }
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
     * Gets the appropriate <code>Capability</code> that maps to this <code>Content</code>. 
     *
     * @return a <code>Capability</code> value
     * @throws UnsupportedOperationException if this <code>Content</code> has no appropriate <code>Capability</code> mapping
     */
    public Capability getAsCapability()
    {
        switch(this) {
            case TOP_OF_BOOK : return Capability.TOP_OF_BOOK;
            case OPEN_BOOK : return Capability.OPEN_BOOK;
            case TOTAL_VIEW : return Capability.TOTAL_VIEW;
            case LEVEL_2 : return Capability.LEVEL_2;
            case BBO10 : return Capability.BBO10;
            case MARKET_STAT : return Capability.MARKET_STAT;
            case LATEST_TICK : return Capability.LATEST_TICK;
            case DIVIDEND : return Capability.DIVIDEND;
            case AGGREGATED_DEPTH: return Capability.AGGREGATED_DEPTH;
            case NBBO: return Capability.NBBO;
            case UNAGGREGATED_DEPTH: return Capability.UNAGGREGATED_DEPTH;
            case IMBALANCE : return Capability.IMBALANCE;
            default : throw new UnsupportedOperationException();
        }
    }
    /**
     * content types that represent a depth-of-book view
     */
    private static final Set<Content> depth = EnumSet.of(OPEN_BOOK,TOTAL_VIEW,LEVEL_2,BBO10,AGGREGATED_DEPTH,UNAGGREGATED_DEPTH);
}