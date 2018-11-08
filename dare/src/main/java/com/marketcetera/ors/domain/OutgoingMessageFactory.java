package com.marketcetera.ors.domain;

import org.marketcetera.trade.BrokerID;

import quickfix.Message;
import quickfix.SessionID;

import com.marketcetera.ors.security.SimpleUser;

/* $License$ */

/**
 * Creates <code>OutgoingMessageFactory</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OutgoingMessageFactory
{
    /**
     * Creates an <code>OutgoingMessage</code> object.
     *
     * @param inMessage a <code>Message</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inActor a <code>SimpleUser</code> value
     * @return an <code>OutgoingMessage</code> value
     */
    OutgoingMessage create(Message inMessage,
                           BrokerID inBrokerId,
                           SessionID inSessionId,
                           SimpleUser inActor);
}
