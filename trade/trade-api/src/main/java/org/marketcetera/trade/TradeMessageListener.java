package org.marketcetera.trade;

/* $License$ */

/**
 * Listens for {@link TradeMessage} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeMessageListener
{
    /**
     * Receive a trade message.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    void receiveTradeMessage(TradeMessage inTradeMessage);
}
