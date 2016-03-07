package org.marketcetera.event.impl;

import org.marketcetera.trade.DeliveryType;

/* $License$ */

/**
 * Builds market data events that have a delivery type.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DeliveryTypeBuilder<B>
{
    /**
     * Set the delivery type value.
     *
     * @param inDeliveryType a <code>DeliveryType</code> value
     * @return a <code>B</code> value
     */
    B withDeliveryType(DeliveryType inDeliveryType);
}
