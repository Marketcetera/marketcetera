package org.marketcetera.core.event;

/* $License$ */

/**
 * Represents the Top-of-Book or Best-Bid-and-Offer for a specific instrument
 * at a specific point in time.
 *
 * @version $Id$
 * @since 1.5.0
 */
public interface TopOfBookEvent
        extends AggregateEvent
{
    /**
     * Get the bid value.
     *
     * @return a <code>BidEvent</code> value
     */
    public BidEvent getBid();
    /**
     * Get the ask value.
     *
     * @return a <code>AskEvent</code> value
     */
    public AskEvent getAsk();
}
