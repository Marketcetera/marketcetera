package org.marketcetera.trade;

/* $License$ */

/**
 * Broadcasts {@link TradeMessage} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeMessageBroadcaster
{
    /**
     * Receives a trade message to broadcast to interested subscribers.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    void reportTradeMessage(TradeMessage inTradeMessage);
}
