package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.EquityMarketstatEvent;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class EquityMarketstatEventImpl
        extends MarketstatEventImpl
        implements EquityMarketstatEvent
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
     * Create a new EquityMarketstatEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inOpenPrice
     * @param inHighPrice
     * @param inLowPrice
     * @param inClosePrice
     * @param inPreviousClosePrice
     * @param inCloseDate
     * @param inPreviousCloseDate
     * @param inTradeHighTime
     * @param inTradeLowTime
     * @param inOpenExchange
     * @param inHighExchange
     * @param inLowExchange
     * @param inCloseExchange
     */
    EquityMarketstatEventImpl(long inMessageId,
                              Date inTimestamp,
                              Equity inInstrument,
                              BigDecimal inOpenPrice,
                              BigDecimal inHighPrice,
                              BigDecimal inLowPrice,
                              BigDecimal inClosePrice,
                              BigDecimal inPreviousClosePrice,
                              String inCloseDate,
                              String inPreviousCloseDate,
                              String inTradeHighTime,
                              String inTradeLowTime,
                              String inOpenExchange,
                              String inHighExchange,
                              String inLowExchange,
                              String inCloseExchange)
    {
        super(inMessageId,
              inTimestamp,
              inInstrument,
              inOpenPrice,
              inHighPrice,
              inLowPrice,
              inClosePrice,
              inPreviousClosePrice,
              inCloseDate,
              inPreviousCloseDate,
              inTradeHighTime,
              inTradeLowTime,
              inOpenExchange,
              inHighExchange,
              inLowExchange,
              inCloseExchange);
    }
    private static final long serialVersionUID = 1L;
}
