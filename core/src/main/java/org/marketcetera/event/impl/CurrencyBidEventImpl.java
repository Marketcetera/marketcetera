package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CurrencyEvent;
import org.marketcetera.event.beans.CurrencyBean;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a Currency implementation of {@link BidEvent}.
 *
 * @version $Id: CurrencyBidEventImpl.java
 */
@ThreadSafe
@ClassVersion("$Id: CurrencyBidEventImpl.java")
public class CurrencyBidEventImpl
        extends AbstractQuoteEventImpl
        implements BidEvent, CurrencyEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.CurrencyEvent#getDeliveryType()
     */
    @Override
    public DeliveryType getDeliveryType()
    {
        return currency.getDeliveryType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public Currency getInstrument()
    {
        return (Currency)super.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.CurrencyEvent#getContractSize()
     */
    @Override
    public int getContractSize()
    {
        return currency.getContractSize();
    }
    /**
     * Create a new CurrencyBidEventImpl instance.
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
    CurrencyBidEventImpl(QuoteBean inQuote,
                       CurrencyBean inCurrency)
    {
        super(inQuote);
        currency = CurrencyBean.copy(inCurrency);
        currency.validate();
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
     * Create a new CurrencyBidEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    @SuppressWarnings("unused")
    private CurrencyBidEventImpl()
    {
        currency = new CurrencyBean();
    }
    /**
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Currency Bid"; //$NON-NLS-1$
    /**
     * the currency attributes 
     */
    private final CurrencyBean currency;
    private static final long serialVersionUID = 1L;
}
