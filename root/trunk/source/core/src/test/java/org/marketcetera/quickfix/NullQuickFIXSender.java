package org.marketcetera.quickfix;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.Side;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Dummy implementation of a QuickfixSender that captures all the messages
 * but doesn't actually send them anywhere.
 * If there's a semaphore, signals on it that the element is available.
 * @author toli
* @version $Id$
*/
public class NullQuickFIXSender implements IQuickFIXSender {

    LinkedList<Message> capturedMessages = new LinkedList<Message>();
    private Semaphore sema;

    public boolean sendToTarget(Message message) throws SessionNotFound {
        return sendHelper(message);
    }

    public boolean sendToTarget(Message message, SessionID sessionID)
            throws SessionNotFound {
        return sendHelper(message);
    }

    public boolean sendToTarget(Message message, String qualifier)
            throws SessionNotFound {
        return sendHelper(message);
    }

    public boolean sendToTarget(Message message, String senderCompID,
            String targetCompID) throws SessionNotFound {
        return sendHelper(message);
    }

    public boolean sendToTarget(Message message, String senderCompID,
            String targetCompID, String qualifier) throws SessionNotFound {
        return sendHelper(message);
    }

    public LinkedList<Message> getCapturedMessages()
    {
        return capturedMessages;
    }

    public void setSemaphore(Semaphore inSema)
    {
        sema = inSema;
    }

    private boolean sendHelper(Message message)
    {
        capturedMessages.add(message);
        if(sema != null) {
            sema.release();
            if (SLF4JLoggerProxy.isDebugEnabled(this)) {
                String humanSide = null;
                try {
                    humanSide = CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldValue(Side.FIELD,
                                                                                                          ""+message.getChar(Side.FIELD)); //$NON-NLS-1$
                } catch (FieldNotFound fieldNotFound) {
                    //ignore
                }
                SLF4JLoggerProxy.debug(this, "qfSender released sema {} for side {}", sema.getQueueLength(), humanSide); //$NON-NLS-1$
            }
        }
        return false;
    }
}
