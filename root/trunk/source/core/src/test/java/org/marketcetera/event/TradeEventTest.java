package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.trade.MSymbol;

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
     * @see org.marketcetera.event.SymbolExchangeEventTest#getObject(long, long, org.marketcetera.trade.MSymbol, java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
     */
    @Override
    protected SymbolExchangeEvent getObject(long inMessageID,
                                            long inTimestamp,
                                            MSymbol inSymbol,
                                            String inExchange,
                                            BigDecimal inPrice,
                                            BigDecimal inSize)
    {
        return new TradeEvent(inMessageID,
                              inTimestamp,
                              inSymbol,
                              inExchange,
                              inPrice,
                              inSize);
    }
}
