package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Tests {@link TradeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class TradeEventTest
    extends SymbolExchangeEventTest
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.SymbolExchangeEventTest#getObject(long, long, org.marketcetera.trade.Instrument, java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
     */
    @Override
    protected SymbolExchangeEvent getObject(long inMessageID,
                                            long inTimestamp,
                                            Instrument inInstrument,
                                            String inExchange,
                                            BigDecimal inPrice,
                                            BigDecimal inSize)
    {
        return new TradeEvent(inMessageID,
                              inTimestamp,
                              inInstrument,
                              inExchange,
                              inPrice,
                              inSize);
    }
}
