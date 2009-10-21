package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link TradeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
final class EquityTradeEventImpl
        extends AbstractTradeEventImpl
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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("EquityTradeEvent [getExchange()=").append(getExchange()) //$NON-NLS-1$
                .append(", getExchangeTimestamp()=").append(getExchangeTimestamp()).append(", getInstrument()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getInstrument()).append(", getMessageId()=").append(getMessageId()).append(", getPrice()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getPrice()).append(", getSize()=").append(getSize()).append(", getSource()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getSource()).append(", getTimestamp()=").append(getTimestamp()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
    /**
     * Create a new EquityTradeEventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inTradeTime a <code>String</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inEquity</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>inPrice</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inSize</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inTradeTime</code> is <code>null</code> or empty
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
