package org.marketcetera.fix;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Manages FIX message listeners.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IncomingMessagePublisher
{
    /**
     * Add the given FIX message listener.
     *
     * @param inMessageListener a <code>MessageListener</code> value
     */
    void addMessageListener(FixMessageHandler inMessageListener);
    /**
     * Remove the given FIX message listener.
     *
     * @param inMessageListener a <code>MessageListener</code> value
     */
    void removeMessageListener(FixMessageHandler inMessageListener);
    /**
     * Receives a FIX message to broadcast to interested subscribers.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     */
    void reportMessage(SessionID inSessionId,
                       Message inMessage);
}
