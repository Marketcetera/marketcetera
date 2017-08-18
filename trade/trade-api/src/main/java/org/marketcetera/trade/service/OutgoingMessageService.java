package org.marketcetera.trade.service;

import org.marketcetera.admin.User;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.OutgoingMessage;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides services for outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OutgoingMessageService
{
    /**
     * Record the outgoing message.
     *
     * @param inOutgoingMessage a <code>Message</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inActor a <code>SimpleUser</code> value
     * @return an <code>OutgoingMessage</code> value
     */
    OutgoingMessage recordOutgoingMessage(Message inOutgoingMessage,
                                          SessionID inSessionId,
                                          BrokerID inBrokerId,
                                          User inActor);
    /**
     * Save the given outgoing message.
     *
     * @param inOutgoingMessage an <code>OutgoingMessage</code> value
     * @return an <code>OutgoingMessage</code> value
     */
    OutgoingMessage save(OutgoingMessage inOutgoingMessage);
}
