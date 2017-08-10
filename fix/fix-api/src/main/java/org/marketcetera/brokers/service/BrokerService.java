package org.marketcetera.brokers.service;

import java.util.Collection;
import java.util.Date;

import org.marketcetera.brokers.Broker;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.fix.FixSession;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides access to broker services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokerService
{
    /**
     * Get the broker for the given session id.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Broker</code> value or <code>null</code>
     */
    Broker getBroker(SessionID inSessionId);
    /**
     * Get the brokers known to the system.
     *
     * @return a <code>Collection&lt;Broker&gt;</code> value
     */
    Collection<Broker> getBrokers();
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
    /**
     * Get the status for the given broker.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return a <code>BrokerStatus</code> value or <code>null</code>
     */
    BrokerStatus getBrokerStatus(BrokerID inBrokerId);
    /**
     * Get the most recent scheduled start of the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Date</code> value or <code>null</code>
     */
    Date getSessionStart(SessionID inSessionId);
    /**
     * Get the next scheduled start of the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Date</code> value or <code>null</code>
     */
    Date getNextSessionStart(SessionID inSessionId);
    /**
     * Finds the fix session with the given session id.
     *
     * @param inFixSessionListener a <code>FixSessionListener</code> value
     * @return a <code>FixSession</code> value or <code>null</code>
     */
    FixSession findFixSessionBySessionId(SessionID inSessionId);
}
