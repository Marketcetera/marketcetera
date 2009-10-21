package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.HasEquity;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
class EquityMarketstatEventImpl
        extends MarketstatEventImpl
        implements HasEquity
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
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inOpenPrice a <code>BigDecimal</code> value
     * @param inHighPrice a <code>BigDecimal</code> value
     * @param inLowPrice a <code>BigDecimal</code> value
     * @param inClosePrice a <code>BigDecimal</code> value
     * @param inPreviousClosePrice a <code>BigDecimal</code> value
     * @param inCloseDate a <code>String</code> value
     * @param inPreviousCloseDate a <code>String</code> value
     * @param inTradeHighTime a <code>String</code> value
     * @param inTradeLowTime a <code>String</code> value
     * @param inOpenExchange a <code>String</code> value
     * @param inHighExchange a <code>String</code> value
     * @param inLowExchange a <code>String</code> value
     * @param inCloseExchange a <code>String</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inInstrument</code> is <code>null</code>
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
