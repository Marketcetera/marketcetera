package org.marketcetera.event;

import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface QuoteEvent
        extends MarketDataEvent, HasInstrument
{
    public Instrument getInstrument();
    public String getExchange();
    public String getQuoteTime();
    public QuoteAction getAction();
}
