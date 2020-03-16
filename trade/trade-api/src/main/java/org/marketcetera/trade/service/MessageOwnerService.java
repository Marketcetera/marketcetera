package org.marketcetera.trade.service;

import java.util.List;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.IdentifyOwnerStrategy;
import org.marketcetera.trade.UserID;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Identifies the owner of a message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MessageOwnerService
{
    /**
     * Cache the owner of the outgoing message.
     *
     * @param inOutgoingMessage a <code>Message</code> value
     * @param inUser a <code>UserID</code> value
     */
    void cacheMessageOwner(Message inOutgoingMessage,
                           UserID inUser);
    /**
     * Get the owner of the given message received from the given session or broker.
     *
     * @param inIncomingMessage a <code>HasFIXMessage</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @return a <code>UserID</code> value
     */
    UserID getMessageOwner(HasFIXMessage inIncomingMessage,
                           SessionID inSessionId,
                           BrokerID inBrokerId);
    /**
     * Get the identifyOwnerStrategies value.
     *
     * @return a <code>List&lt;IdentifyOwnerStrategy&gt;</code> value
     */
    List<IdentifyOwnerStrategy> getIdentifyOwnerStrategies();
}
