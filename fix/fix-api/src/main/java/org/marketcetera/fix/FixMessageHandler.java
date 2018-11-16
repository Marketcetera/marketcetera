package org.marketcetera.fix;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Handles incoming FIX messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixMessageHandler
{
    /**
     * Handle the given message send to the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     * @throws Exception if an error occurs 
     */
    void handleMessage(SessionID inSessionId,
                       Message inMessage)
            throws Exception;
}
