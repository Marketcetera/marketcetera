package org.marketcetera.core.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import org.marketcetera.core.event.BidEvent;
import org.marketcetera.core.event.EquityEvent;
import org.marketcetera.core.event.beans.QuoteBean;
import org.marketcetera.core.trade.Equity;

/* $License$ */

/**
 * Provides an Equity implementation of {@link org.marketcetera.core.event.BidEvent}.
 *
 * @version $Id: EquityBidEventImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ThreadSafe
class EquityBidEventImpl
        extends AbstractQuoteEventImpl
        implements BidEvent, EquityEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public Equity getInstrument()
    {
        return (Equity)super.getInstrument();
    }
    /**
     * Create a new EquityBidEventImpl instance.
     *
     * @param inQuote a <code>QuoteBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Action</code> is <code>null</code>
     */
    EquityBidEventImpl(QuoteBean inQuote)
    {
        super(inQuote);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractQuoteEventImpl#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return description;
    }
    /**
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Equity Bid"; //$NON-NLS-1$
    private static final long serialVersionUID = 1L;
}
