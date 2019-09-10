package org.marketcetera.trade.event;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.HasBrokerID;

/* $License$ */

/**
 * Indicates a FIX Message event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixMessageEvent
        extends HasFIXMessage,HasBrokerID
{
}
