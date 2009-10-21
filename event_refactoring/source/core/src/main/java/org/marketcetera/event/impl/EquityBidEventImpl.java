package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link BidEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
class EquityBidEventImpl
        extends AbstractQuoteEventImpl
        implements BidEvent, EquityEvent
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
        builder.append("EquityBidEventImpl [getAction()=").append(getAction()).append(", getExchange()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getExchange()).append(", getExchangeTimestamp()=").append(getExchangeTimestamp()) //$NON-NLS-1$
                .append(", getInstrument()=").append(getInstrument()).append(", getMessageId()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getMessageId()).append(", getPrice()=").append(getPrice()).append(", getSize()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getSize()).append(", getSource()=").append(getSource()).append(", getTimestamp()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getTimestamp()).append("]"); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * Create a new EquityBidEventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inQuoteTime a <code>String</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>inPrice</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inSize</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inQuoteTime</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>QuoteAction</code> is <code>null</code>
     */
    EquityBidEventImpl(long inMessageId,
                       Date inTimestamp,
                       Equity inInstrument,
                       String inExchange,
                       BigDecimal inPrice,
                       BigDecimal inSize,
                       String inQuoteTime,
                       QuoteAction inAction)
    {
        super(inMessageId,
              inTimestamp,
              inInstrument,
              inExchange,
              inPrice,
              inSize,
              inQuoteTime,
              inAction);
    }
    private static final long serialVersionUID = 1L;
}
