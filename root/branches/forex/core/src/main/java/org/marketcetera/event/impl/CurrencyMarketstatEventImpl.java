package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;

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
@ClassVersion("$Id: CurrencyMarketstatEventImpl.java")
class CurrencyMarketstatEventImpl
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
     * the currency attributes 
     */
    private final CurrencyBean currency;
    private static final long serialVersionUID = 1L;
}
