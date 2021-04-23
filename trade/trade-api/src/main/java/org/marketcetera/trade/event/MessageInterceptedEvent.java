package org.marketcetera.trade.event;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.HasSessionId;

/* $License$ */

/**
 * Indicates that an order has been intercepted.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MessageInterceptedEvent
        extends HasFIXMessage,HasSessionId
{
}
