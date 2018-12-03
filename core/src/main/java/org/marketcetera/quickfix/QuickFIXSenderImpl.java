package org.marketcetera.quickfix;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

/* $License$ */

/**
 * Provides a {@link QuickFIXSender} implementation that sends the messages to QuickFIX.
 *
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 */
public class QuickFIXSenderImpl
        implements QuickFIXSender
{
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.QuickFIXSender#sendToTarget(quickfix.Message)
     */
    @Override
    public boolean sendToTarget(Message inMessage)
            throws SessionNotFound
    {
        return Session.sendToTarget(inMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.QuickFIXSender#sendToTarget(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public boolean sendToTarget(Message inMessage,
                                SessionID inSessionID)
            throws SessionNotFound
    {
        return Session.sendToTarget(inMessage,
                                    inSessionID);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.QuickFIXSender#sendToTarget(quickfix.Message, java.lang.String)
     */
    @Override
    public boolean sendToTarget(Message inMessage,
                                String inQualifier)
            throws SessionNotFound
    {
        return Session.sendToTarget(inMessage,
                                    inQualifier);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.QuickFIXSender#sendToTarget(quickfix.Message, java.lang.String, java.lang.String)
     */
    @Override
    public boolean sendToTarget(Message inMessage,
                                String inSenderCompID,
                                String inTargetCompID)
            throws SessionNotFound
    {
        return Session.sendToTarget(inMessage,
                                    inSenderCompID,
                                    inTargetCompID);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.QuickFIXSender#sendToTarget(quickfix.Message, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean sendToTarget(Message inMessage,
                                String inSenderCompID,
                                String inTargetCompID,
                                String inQualifier)
            throws SessionNotFound
    {
        return Session.sendToTarget(inMessage,
                                    inSenderCompID,
                                    inTargetCompID,
                                    inQualifier);
    }
}
