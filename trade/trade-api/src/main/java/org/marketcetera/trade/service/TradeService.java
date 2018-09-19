package org.marketcetera.trade.service;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.OrderIntercepted;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessagePublisher;

import quickfix.Message;

/* $License$ */

/**
 * Provides trade services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeService
        extends TradeMessagePublisher
{
    /**
     * Select a session for the given order.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>ServerFixSession</code> value
     * @throws NoBrokerSelected if a session could not be determined
     */
    ServerFixSession selectServerFixSession(Order inOrder);
    /**
     * Convert the given order into a FIX message targeted to the given session.
     *
     * @param inOrder an <code>Order</code> value
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @return a <code>Message</code> value
     * @throws BrokerUnavailable if the broker is unavailable or unknown
     * @throws OrderIntercepted if the order should not be sent on in the data flow
     */
    Message convertOrder(Order inOrder,
                         ServerFixSession inServerFixSession);
    /**
     * Convert the given message from the given broker to a <code>TradeMessage</code>.
     *
     * @param inMessage a <code>HasFIXMessage</code> value
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @return a <code>TradeMessage</code> value
     * @throws OrderIntercepted if the message should not be sent on in the data flow
     * @throws MessageCreationException if the message could not be converted
     */
    TradeMessage convertResponse(HasFIXMessage inMessage,
                                 ServerFixSession inServerFixSession);
    /**
     * Submits the given order to the standard outgoing data flow.
     *
     * @param inOrder a <code>HasOrder</code> value
     * @return an <code>Object</code> value containing the result of the submission, if available
     */
    Object submitOrderToOutgoingDataFlow(HasOrder inOrder);
}
