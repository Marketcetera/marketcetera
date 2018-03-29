package org.marketcetera.event;

import org.marketcetera.trade.DeliveryType;
import org.marketcetera.trade.FutureType;
import org.marketcetera.trade.FutureUnderlyingAssetType;
import org.marketcetera.trade.StandardType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class represents a futures event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
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
     * Returns the contract size.
     *
     * @return an <code>int</code> value
     */
    public int getContractSize();
}
