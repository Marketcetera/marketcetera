package org.marketcetera.trade.service;

import org.marketcetera.brokers.Broker;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;

import quickfix.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeService
{
    /**
     * Select a broker for the given order.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>Broker</code> value
     */
    Broker selectBroker(Order inOrder);
    /**
     * Convert the given order into a FIX message targeted to the given broker.
     *
     * @param inOrder an <code>Order</code> value
     * @param inBroker a <code>Broker</code> value
     * @return a <code>Message</code> value
     */
    Message convertOrder(Order inOrder,
                         Broker inBroker);
    /**
     * Convert the given message from the given broker to a <code>TradeMessage</code>.
     *
     * @param inMessage a <code>Message</code> value
     * @param inBroker a <code>Broker</code> value
     * @return a <code>TradeMessage</code> value
     */
    TradeMessage convertResponse(Message inMessage,
                                 Broker inBroker);
}
