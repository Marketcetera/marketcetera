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
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface FutureEvent
        extends HasFuture, Event
{
    /**
     * 
     *
     *
     * @return
     */
    public FutureType getType();
    /**
     * 
     *
     *
     * @return
     */
    public FutureUnderlyingAssetType getUnderylingAssetType();
    /**
     * 
     *
     *
     * @return
     */
    public DeliveryType getDeliveryType();
    /**
     * 
     *
     *
     * @return
     */
    public StandardType getStandardType();
    /**
     * Returns the original provider symbol of the future, if available. 
     *
     * @return a <code>String</code> value or <code>null</code> if the future event
     *  did not have a provider symbol
     */
    public String getProviderSymbol();
}
