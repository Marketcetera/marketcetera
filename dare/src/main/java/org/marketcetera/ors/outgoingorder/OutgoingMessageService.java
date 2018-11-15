package org.marketcetera.ors.outgoingorder;

import org.marketcetera.ors.domain.OutgoingMessage;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.UserID;

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
     * Cache the owner of the outgoing message.
     *
     * @param inOutgoingMessage a <code>Message</code> value
     * @param inActor a <code>UserID</code> value
     */
    void cacheMessageOwner(Message inOutgoingMessage,
                           UserID inActor);
    /**
     * Get the owner of the given message received from the given session or broker.
     *
     * @param inIncomingMessage a <code>Message</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @return a <code>UserID</code> value
     */
    UserID getMessageOwner(Message inIncomingMessage,
                           SessionID inSessionId,
                           BrokerID inBrokerId);
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
                                          SimpleUser inActor);
    /**
     * Save the given outgoing message.
     *
     * @param inOutgoingMessage an <code>OutgoingMessage</code> value
     * @return an <code>OutgoingMessage</code> value
     */
    OutgoingMessage save(OutgoingMessage inOutgoingMessage);
}
