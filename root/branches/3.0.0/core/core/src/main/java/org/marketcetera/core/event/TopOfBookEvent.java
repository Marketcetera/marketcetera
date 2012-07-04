package org.marketcetera.core.event;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Represents the Top-of-Book or Best-Bid-and-Offer for a specific instrument
 * at a specific point in time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TopOfBookEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
@ClassVersion("$Id: TopOfBookEvent.java 16063 2012-01-31 18:21:55Z colin $")
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
