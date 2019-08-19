package org.marketcetera.fix.dao;

import org.marketcetera.fix.IncomingMessage;
import org.marketcetera.fix.IncomingMessageFactory;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Creates new {@link IncomingMessage} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentIncomingMessageFactory
        implements IncomingMessageFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessageFactory#create(quickfix.SessionID, quickfix.Message)
     */
    @Override
    public PersistentIncomingMessage create(SessionID inSessionId,
                                            Message inMessage)
    {
        return new PersistentIncomingMessage(inSessionId,
                                             inMessage);
    }
}
