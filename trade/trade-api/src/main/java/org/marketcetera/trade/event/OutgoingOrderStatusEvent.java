package org.marketcetera.trade.event;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.HasMutableStatus;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.HasOrderId;

/* $License$ */

/**
 * Indicates the send status of an outgoing order.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OutgoingOrderStatusEvent
        extends HasMutableStatus,HasFIXMessage,HasOrder,HasOrderId
{
}
