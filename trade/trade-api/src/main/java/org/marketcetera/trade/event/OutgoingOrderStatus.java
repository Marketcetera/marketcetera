package org.marketcetera.trade.event;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.HasMutableStatus;

/* $License$ */

/**
 * Indicates the send status of an outgoing order.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OutgoingOrderStatus
        extends HasMutableStatus, HasFIXMessage
{
}
