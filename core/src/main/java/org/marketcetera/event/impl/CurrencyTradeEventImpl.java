package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.CurrencyEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.CurrencyBean;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a Currency implementation of {@link TradeEvent}.
 *
 */
@ThreadSafe
@XmlRootElement(name="currencyTrade")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id: CurrencyTradeEventImpl.java")
public class CurrencyTradeEventImpl
        extends AbstractTradeEventImpl
        implements CurrencyEvent
{
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
    /* (non-Javadoc)
     * @see org.marketcetera.event.CurrencyEvent#getDeliveryType()
     */
    @Override
    public DeliveryType getDeliveryType()
    {
        return currency.getDeliveryType();
    }
    /**
     * Create a new CurrencyTradeEventImpl instance.
     *
     */
    CurrencyTradeEventImpl(MarketDataBean inMarketData,
                         CurrencyBean inCurrency)
    {
        super(inMarketData);
        currency = inCurrency;
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
     * Create a new CurrencyTradeEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    @SuppressWarnings("unused")
    private CurrencyTradeEventImpl()
    {
        currency = new CurrencyBean();
    }
    /**
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Currency Trade"; //$NON-NLS-1$
    /**
     * the currency attributes 
     */
    @XmlElement
    private final CurrencyBean currency;
    private static final long serialVersionUID = 1L;
}
