package org.marketcetera.ors.dao;

import org.marketcetera.ors.domain.OutgoingMessage;
import org.marketcetera.ors.domain.OutgoingMessageFactory;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.BrokerID;

import quickfix.Message;
import quickfix.SessionID;


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
