package org.marketcetera.event;

import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;


/* $License$ */

/**
 * Represents the set of available statistics of a specific {@link Option}.
 * 
 * <p>The data contained in this event represent the best-effort result of a request
 * to retrieve available statistics for a specific symbol at a specific time.  Some
 * or all of the attributes may be null if the data was not available.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketstatEvent.java 10808 2009-10-12 21:33:18Z anshul $
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OptionMarketstatEvent
        extends MarketstatEvent, HasOption, OptionEvent
{
}
