package org.marketcetera.brokers.service;

import java.util.Collection;

import org.marketcetera.brokers.Broker;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.BrokerID;
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
public interface BrokerService
{
    /**
     * 
     *
     *
     * @return
     */
    Collection<Broker> getBrokers();
    /**
     * 
     *
     *
     * @param inOrder
     * @return
     */
    Broker selectBroker(Order inOrder);
    /**
     * 
     *
     *
     * @param inOrder
     * @param inBroker
     * @return
     */
    Message convertOrder(Order inOrder,
                         Broker inBroker);
    /**
     * 
     *
     *
     * @param inMessage
     * @param inBroker
     * @return
     */
    TradeMessage convertResponse(Message inMessage,
                                 Broker inBroker);
    /**
     * 
     *
     *
     * @param inBrokerId
     * @return
     */
    BrokerStatus getBrokerStatus(BrokerID inBrokerId);
}
