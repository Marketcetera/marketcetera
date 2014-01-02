package org.marketcetera.event;

import org.marketcetera.trade.DeliveryType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class represents an currency event.
 *
 */
@ClassVersion("$Id: CurrencyEvent.java")
public interface CurrencyEvent
        extends HasCurrency, Event
{
    /**
     * Gets the delivery type.
     *
     * @return a <code>DeliveryType</code> value
     */
    public DeliveryType getDeliveryType();
    /**
     * Returns the contract size.
     *
     * @return an <code>int</code> value
     */
    public int getContractSize();
}
