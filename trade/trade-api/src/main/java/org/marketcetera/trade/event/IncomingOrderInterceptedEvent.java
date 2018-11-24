package org.marketcetera.trade.event;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.HasSessionId;

/* $License$ */

/**
 * Indicates that an incoming order has been intercepted.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IncomingOrderInterceptedEvent
        extends HasFIXMessage,HasSessionId
{
}
