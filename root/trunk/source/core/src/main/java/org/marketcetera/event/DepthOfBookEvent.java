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
}
