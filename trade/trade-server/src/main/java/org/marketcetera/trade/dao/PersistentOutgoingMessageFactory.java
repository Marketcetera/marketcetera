package org.marketcetera.trade.dao;

import org.marketcetera.admin.User;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.OutgoingMessage;
import org.marketcetera.trade.OutgoingMessageFactory;
import org.springframework.stereotype.Service;

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
@Service
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
                                  User inActor)
    {
        return new PersistentOutgoingMessage(inMessage,
                                             inBrokerId,
                                             inSessionId,
                                             inActor);
    }
}
