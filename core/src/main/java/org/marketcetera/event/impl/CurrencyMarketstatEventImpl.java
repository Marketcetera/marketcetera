package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.CurrencyEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.CurrencyBean;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.DeliveryType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a Currency implementation of {@link MarketstatEvent}.
 *
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="currencyMarketstat")
@ClassVersion("$Id$")
public class CurrencyMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements CurrencyEvent
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
     * @see org.marketcetera.event.HasCurrency#getInstrument()
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
     * Create a new CurrencyMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    CurrencyMarketstatEventImpl(MarketstatBean inMarketstat,
                                CurrencyBean inCurrency)
    {
        super(inMarketstat);
        currency = inCurrency;
        currency.validate();
    }
    /**
     * Create a new CurrencyMarketstatEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    @SuppressWarnings("unused")
    private CurrencyMarketstatEventImpl()
    {
        currency = new CurrencyBean();
    }
    /**
     * the currency attributes 
     */
    @XmlElement
    private final CurrencyBean currency;
    private static final long serialVersionUID = 1L;
}
