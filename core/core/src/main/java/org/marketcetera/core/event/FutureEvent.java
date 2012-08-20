package org.marketcetera.core.event;

import org.marketcetera.core.trade.DeliveryType;
import org.marketcetera.core.trade.FutureType;
import org.marketcetera.core.trade.FutureUnderlyingAssetType;
import org.marketcetera.core.trade.StandardType;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class represents a futures event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FutureEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
@ClassVersion("$Id: FutureEvent.java 16063 2012-01-31 18:21:55Z colin $")
public interface FutureEvent
        extends HasFuture, Event, HasProviderSymbol
{
    /**
     * Gets the future type.
     *
     * @return a <code>FutureType</code> value
     */
    public FutureType getType();
    /**
     * Gets the future underlying asset type.
     *
     * @return a <code>FutureUnderlyingAssertType</code> value
     */
    public FutureUnderlyingAssetType getUnderylingAssetType();
    /**
     * Gets the delivery type.
     *
     * @return a <code>DeliveryType</code> value
     */
    public DeliveryType getDeliveryType();
    /**
     * Gets the standard type.
     *
     * @return a <code>StandardType</code> value
     */
    public StandardType getStandardType();
    /**
     * Returns the original provider symbol of the future, if available. 
     *
     * @return a <code>String</code> value or <code>null</code> if the future event
     *  did not have a provider symbol
     */
    public String getProviderSymbol();
    /**
     * Returns the contract size.
     *
     * @return an <code>int</code> value
     */
    public int getContractSize();
}
