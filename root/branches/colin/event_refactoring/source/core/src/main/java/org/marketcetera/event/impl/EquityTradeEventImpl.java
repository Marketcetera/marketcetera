package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.EquityEvent;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
final class EquityTradeEventImpl
        extends TradeEventImpl
        implements EquityEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getEquity()
     */
    @Override
    public Equity getEquity()
    {
        return (Equity)getInstrument();
    }
    /**
     * Create a new EquityTradeEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inEquity
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inTradeTime
     */
    EquityTradeEventImpl(long inMessageId,
                         Date inTimestamp,
                         Equity inEquity,
                         String inExchange,
                         BigDecimal inPrice,
                         BigDecimal inSize,
                         String inTradeTime)
    {
        super(inMessageId,
              inTimestamp,
              inEquity,
              inExchange,
              inPrice,
              inSize,
              inTradeTime);
    }
    private static final long serialVersionUID = 1L;
}
