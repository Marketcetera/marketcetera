package org.marketcetera.event;

import java.util.List;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the Depth-of-Book for a specific instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DepthOfBook.java 10808 2009-10-12 21:33:18Z anshul $
 * @since 1.5.0
 */
@ClassVersion("$Id: DepthOfBook.java 10808 2009-10-12 21:33:18Z anshul $")
public interface DepthOfBookEvent
        extends AggregateEvent, HasInstrument
{
    /**
     * Get the asks value.
     *
     * @return a <code>List&lt;AskEvent&gt;</code> value
     */
    public List<AskEvent> getAsks();
    /**
     * Get the bids value.
     *
     * @return a <code>List&lt;BidEvent&gt;</code> value
     */
    public List<BidEvent> getBids();
    /**
     * Determines if the given <code>DepthOfBookEvent</code> is equivalent
     * to this object.
     *
     * <p>Equivalent is defined as containing the same number of bids
     * and asks in the same order with each having the same price and quantity
     * for the same symbol.
     *
     * @param inOther a <code>DepthOfBookEvent</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equivalent(DepthOfBookEvent other);
}
