package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a Trade for an {@link Equity} at a specific time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TradeEvent.java 10808 2009-10-12 21:33:18Z anshul $
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface EquityTradeEvent
        extends TradeEvent, HasEquity
{
}
