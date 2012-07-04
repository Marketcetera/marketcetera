package org.marketcetera.core.quickfix;

import org.marketcetera.core.attributes.ClassVersion;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

/**
 * Marker class for objects that send out FIX messages
 * Essentially used for classes that need to be subclassed in unit tests
 * when we want to just capture the message instead of sending it out.
 * @author toli
 * @version $Id: FIXMessageSender.java 16063 2012-01-31 18:21:55Z colin $
 */

@ClassVersion("$Id: FIXMessageSender.java 16063 2012-01-31 18:21:55Z colin $")
public class FIXMessageSender {
    /** To be overridden by unit tests for capturing outgoing messages */
    public void sendOutgoingMessage(Message inMsg, SessionID targetID) throws SessionNotFound
    {
        Session.sendToTarget(inMsg, targetID);
    }

}
