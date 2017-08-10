package org.marketcetera.trade;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.UserID;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides a method to determine the owner of a received message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IdentifyOwnerStrategy
{
    /**
     * Get the owning user of the given message received from the given session and broker.
     *
     * @param inMessage a <code>Message</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @return a <code>UserID</code> value
     */
    UserID getOwnerOf(Message inMessage,
                      SessionID inSessionId,
                      BrokerID inBrokerId);
}
