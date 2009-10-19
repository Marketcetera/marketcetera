package org.marketcetera.event;

import java.util.List;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DepthOfBookEvent
        extends AggregateEvent, HasInstrument
{
    /**
     * 
     *
     *
     * @return
     */
    public List<AskEvent> getAsks();
    /**
     * 
     *
     *
     * @return
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
