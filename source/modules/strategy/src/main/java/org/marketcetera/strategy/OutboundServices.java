package org.marketcetera.strategy;

import org.marketcetera.module.DataEmitter;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Services available to strategies to emit data of various types to the strategy agent framework.
 * 
 * <p>Data transmitted via the methods in this interface will be emitted via the implementer's {@link DataEmitter}
 * implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
interface OutboundServices
{
    /**
     * Sends an order to the destination or destinations established in the strategy module.
     *
     * TODO need the FixAgnostic objects
     * 
     * @param inOrder
     */
    void sendOrder(Object inOrder);
    /**
     * Sends a trade suggestion to the destination or destinations established in the strategy module.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    void sendSuggestion(Suggestion inSuggestion);
}
