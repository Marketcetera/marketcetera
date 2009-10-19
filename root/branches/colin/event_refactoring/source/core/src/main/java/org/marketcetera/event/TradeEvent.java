package org.marketcetera.event;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeEvent
        extends MarketDataEvent, HasInstrument
{
    /**
     * 
     *
     *
     * @return
     */
    public String getTradeTime();
}
