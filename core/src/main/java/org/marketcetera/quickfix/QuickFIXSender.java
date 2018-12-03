package org.marketcetera.quickfix;

import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;

/* $License$ */

/**
 * Provides a method to send QuickFIX messages appropriate for the implementation.
 *
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 */
public interface QuickFIXSender
{
    /**
     * Send a message to the session specified in the message's target
     * identifiers.
     */
    boolean sendToTarget(Message message)
            throws SessionNotFound;
    /** Send a message to the session specified by the provided session ID. */
    boolean sendToTarget(Message message,
                         SessionID sessionID)
            throws SessionNotFound;
    /**
     * Send a message to the session specified in the message's target
     * identifiers.
     */
    boolean sendToTarget(Message message,
                         String qualifier)
            throws SessionNotFound;
    /**
     * Send a message to the session specified by the provided target company
     * ID.
     */
    boolean sendToTarget(Message message,
                         String senderCompID,
                         String targetCompID)
            throws SessionNotFound;
    /**
     * Send a message to the session specified by the provided target company
     * ID.
     */
    boolean sendToTarget(Message message,
                         String senderCompID,
                         String targetCompID,
                         String qualifier)
            throws SessionNotFound;
}
