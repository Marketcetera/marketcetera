package org.marketcetera.fix.service;

import java.util.Set;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixMessageHandler;
import org.marketcetera.fix.IncomingMessagePublisher;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides incoming message services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class IncomingMessageServiceImpl
        implements IncomingMessageService,IncomingMessagePublisher
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.IncomingMessagePublisher#addMessageListener(org.marketcetera.fix.FixMessageHandler)
     */
    @Override
    public void addMessageListener(FixMessageHandler inMessageListener)
    {
        messageListeners.add(inMessageListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.IncomingMessagePublisher#removeMessageListener(org.marketcetera.fix.FixMessageHandler)
     */
    @Override
    public void removeMessageListener(FixMessageHandler inMessageListener)
    {
        messageListeners.remove(inMessageListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.IncomingMessagePublisher#reportMessage(quickfix.SessionID, quickfix.Message)
     */
    @Override
    public void reportMessage(SessionID inSessionId,
                              Message inMessage)
    {
        for(FixMessageHandler messageListener : messageListeners) {
            try {
                messageListener.handleMessage(inSessionId,
                                              inMessage);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Error handling FIX message",
                                                 e);
            }
        }
    }
    /**
     * holds message listeners
     */
    private final Set<FixMessageHandler> messageListeners = Sets.newConcurrentHashSet();
}
