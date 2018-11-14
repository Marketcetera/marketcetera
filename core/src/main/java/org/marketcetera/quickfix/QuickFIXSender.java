package org.marketcetera.quickfix;

import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;

public interface QuickFIXSender 
{
    /**
     * Send a message to the session specified in the message's target identifiers.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     * @throws SessionNotFound if the session for the message cannot be found
     */
    boolean sendToTarget(Message inMessage)
            throws SessionNotFound;
    /**
     * Send a message to the session specified by the provided session ID.
     *
     * @param inMessage a <code>Message</code> value
     * @param inSessionID a <code>SessionID</code> value
     * @return a <code>boolean</code> value
     * @throws SessionNotFound if the session for the message cannot be found
     */
    boolean sendToTarget(Message inMessage,
                         SessionID inSessionID)
            throws SessionNotFound;
    /**
     * Send a message to the session specified in the message's target identifiers.
     *
     * @param inMessage a <code>Message</code> value
     * @param inQualifier a <code>String</code> value
     * @return a <code>boolean</code> value
     * @throws SessionNotFound if the session for the message cannot be found
     */
    boolean sendToTarget(Message inMessage,
                         String inQualifier)
            throws SessionNotFound;
    /**
     * Send a message to the session specified by the provided target company ID.
     *
     * @param inMessage a <code>Message</code> value
     * @param inSenderCompID a <code>String</code> value
     * @param inTargetCompID a <code>String</code> value
     * @return a <code>boolean</code> value
     * @throws SessionNotFound if the session for the message cannot be found
     */
    boolean sendToTarget(Message inMessage,
                         String inSenderCompID,
                         String inTargetCompID)
        throws SessionNotFound;
    /**
     * Send a message to the session specified by the provided target company ID.
     *
     * @param inMessage a <code>Message</code> value
     * @param inSenderCompID a <code>String</code> value
     * @param inTargetCompID a <code>String</code> value
     * @param inQualifier a <code>String</code> value
     * @return a <code>boolean</code> value
     * @throws SessionNotFound if the session for the message cannot be found
     */
    boolean sendToTarget(Message inMessage,
                         String inSenderCompID,
                         String inTargetCompID,
                         String inQualifier)
            throws SessionNotFound;
}
