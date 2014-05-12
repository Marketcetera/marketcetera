package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.CurrencyEvent;
import org.marketcetera.event.beans.CurrencyBean;
import org.marketcetera.event.beans.ImbalanceBean;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.DeliveryType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an <code>ImbalanceEvent</code> implementation for a <code>Currency</code> instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="currencyImbalance")
@ClassVersion("$Id$")
public class CurrencyImbalanceEvent
        extends AbstractImbalanceEvent
        implements CurrencyEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractImbalanceEvent#getInstrument()
     */
    @Override
    public Currency getInstrument()
    {
        return (Currency)super.getInstrument();
    }
    /**
     * Create a new CurrencyImbalanceEvent instance.
     *
     * @param inImbalance
     * @param inCurrencyBean
     */
    public CurrencyImbalanceEvent(ImbalanceBean inImbalance,
                                  CurrencyBean inCurrencyBean)
    {
        super(inImbalance);
        currency = inCurrencyBean;
        currency.validate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.CurrencyEvent#getDeliveryType()
     */
    @Override
    public DeliveryType getDeliveryType()
    {
        return currency.getDeliveryType();
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
     * Create a new CurrencyImbalanceEvent instance.
     */
    @SuppressWarnings("unused")
    private CurrencyImbalanceEvent()
    {
        currency = new CurrencyBean();
    }
    /**
     * the currency attributes 
     */
    @XmlElement
    private final CurrencyBean currency;
    private static final long serialVersionUID = -2972410310519537113L;
}
