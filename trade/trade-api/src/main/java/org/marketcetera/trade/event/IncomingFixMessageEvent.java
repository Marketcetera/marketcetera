package org.marketcetera.trade.event;

import org.marketcetera.fix.HasSessionId;
import org.marketcetera.trade.HasBrokerID;

/* $License$ */

/**
 * Indicates an incoming FIX Message event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IncomingFixMessageEvent
        extends FixMessageEvent,HasSessionId,HasBrokerID
{
}
