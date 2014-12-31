package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.CurrencyEvent;
import org.marketcetera.event.beans.CurrencyBean;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a Currency implementation of {@link AskEvent}.
 *
 */
@ThreadSafe
@XmlRootElement(name="currencyAsk")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class CurrencyAskEventImpl
        extends AbstractQuoteEventImpl
        implements AskEvent, CurrencyEvent
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
     * Create a new CurrencyAskEventImpl instance.
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
    CurrencyAskEventImpl(QuoteBean inQuote,
                       CurrencyBean inCurrency)
    {
        super(inQuote);
        currency = CurrencyBean.copy(inCurrency);
        currency.validate();
    }
    /**
     * Create a new CurrencyAskEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    @SuppressWarnings("unused")
    private CurrencyAskEventImpl()
    {
        currency = new CurrencyBean();
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
    private static final String description = "Currency Ask"; //$NON-NLS-1$
    /**
     * the currency attributes 
     */
    @XmlElement
    private final CurrencyBean currency;
    private static final long serialVersionUID = 1L;
}
