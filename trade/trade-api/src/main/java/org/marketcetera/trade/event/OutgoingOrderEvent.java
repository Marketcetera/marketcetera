package org.marketcetera.trade.event;

import org.marketcetera.admin.HasUser;
import org.marketcetera.trade.HasOrder;

/* $License$ */

/**
 * Indicates an outgoing order has occurred.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OutgoingOrderEvent
        extends HasOrder,HasUser
{
}
