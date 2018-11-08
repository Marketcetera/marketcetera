package com.marketcetera.ors.dao;

import org.marketcetera.trade.BrokerID;

import quickfix.Message;
import quickfix.SessionID;

import com.marketcetera.ors.domain.OutgoingMessage;
import com.marketcetera.ors.domain.OutgoingMessageFactory;
import com.marketcetera.ors.security.SimpleUser;

/* $License$ */

/**
 * Creates <code>PersistentOutgoingMessageFactory</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentOutgoingMessageFactory
        implements OutgoingMessageFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessageFactory#create(quickfix.Message, org.marketcetera.trade.BrokerID, quickfix.SessionID, com.marketcetera.ors.security.SimpleUser)
     */
    @Override
    public OutgoingMessage create(Message inMessage,
                                  BrokerID inBrokerId,
                                  SessionID inSessionId,
                                  SimpleUser inActor)
    {
        return new PersistentOutgoingMessage(inMessage,
                                             inBrokerId,
                                             inSessionId,
                                             inActor);
    }
}
