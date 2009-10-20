package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a quote for an {@link Equity}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: QuoteEvent.java 10808 2009-10-12 21:33:18Z anshul $
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface EquityQuoteEvent
        extends QuoteEvent, HasEquity
{
}
