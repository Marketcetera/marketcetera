package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the Top-of-Book or Best-Bid-and-Offer for a specific instrument
 * at a specific point in time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TopOfBook.java 10808 2009-10-12 21:33:18Z anshul $
 * @since 1.5.0
 */
@ClassVersion("$Id: TopOfBook.java 10808 2009-10-12 21:33:18Z anshul $")
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
