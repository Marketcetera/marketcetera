package org.marketcetera.trade;

/* $License$ */

/**
 * Manages trade message listeners.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeMessagePublisher
{
    /**
     * Add the given trade message listener.
     *
     * @param inTradeMessageListener a <code>TradeMessageListener</code> value
     */
    void addTradeMessageListener(TradeMessageListener inTradeMessageListener);
    /**
     * Remove the given trade message listener.
     *
     * @param inTradeMessageListener a <code>TradeMessageListener</code> value
     */
    void removeTradeMessageListener(TradeMessageListener inTradeMessageListener);
}
