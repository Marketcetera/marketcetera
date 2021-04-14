package org.marketcetera.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Application;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractMockFixApplication
        implements Application
{
    /**
     * Validates and starts the object.
     */
    public void start()
    {
        Validate.notNull(sessionSettings);
        Validate.notNull(messageFactory);
        reset();
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(quickfix.SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} created",
                              inSessionId);
        synchronized(fromAppMessages) {
            fromAppMessages.put(inSessionId,
                                new LinkedBlockingDeque<quickfix.Message>());
        }
        synchronized(fromAdminMessages) {
            fromAdminMessages.put(inSessionId,
                                  new LinkedBlockingDeque<quickfix.Message>());
        }
        synchronized(toAppMessages) {
            toAppMessages.put(inSessionId,
                              new LinkedBlockingDeque<quickfix.Message>());
        }
        synchronized(toAdminMessages) {
            toAdminMessages.put(inSessionId,
                                new LinkedBlockingDeque<quickfix.Message>());
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(quickfix.SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} logged on",
                              inSessionId);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(quickfix.SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} logged out",
                              inSessionId);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(quickfix.Message inMessage,
                        quickfix.SessionID inSessionId)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} sending admin message {}",
                               inSessionId,
                               inMessage);
        addMessageToContainer(inSessionId,
                              inMessage,
                              toAdminMessages);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromAdmin(quickfix.Message inMessage,
                          quickfix.SessionID inSessionId)
            throws quickfix.FieldNotFound, quickfix.IncorrectDataFormat, quickfix.IncorrectTagValue, quickfix.RejectLogon
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received admin message {}",
                               inSessionId,
                               inMessage);
        addMessageToContainer(inSessionId,
                              inMessage,
                              fromAdminMessages);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toApp(quickfix.Message inMessage,
                      quickfix.SessionID inSessionId)
            throws quickfix.DoNotSend
    {
        SLF4JLoggerProxy.trace(this,
                               "{} sending app message {}",
                               inSessionId,
                               inMessage);
        addMessageToContainer(inSessionId,
                              inMessage,
                              toAppMessages);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromApp(quickfix.Message inMessage,
                        quickfix.SessionID inSessionId)
            throws quickfix.FieldNotFound, quickfix.IncorrectDataFormat, quickfix.IncorrectTagValue, quickfix.UnsupportedMessageType
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received app message {}",
                               inSessionId,
                               inMessage);
        addMessageToContainer(inSessionId,
                              inMessage,
                              fromAppMessages);
    }
    /**
     * Wait for and return the next from application message.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>quickfix.Message</code> value
     * @throws InterruptedException if the call is interrupted while waiting for the next message
     */
    public quickfix.Message getNextFromApplicationMessage(quickfix.SessionID inSessionId)
            throws Exception
    {
        quickfix.SessionID reversedSessionId = new quickfix.SessionID(inSessionId.getBeginString(),
                                                                      inSessionId.getTargetCompID(),
                                                                      inSessionId.getSenderCompID());
        BlockingDeque<quickfix.Message> messages;
        synchronized(fromAppMessages) {
            messages = fromAppMessages.get(reversedSessionId);
        }
        if(messages == null) {
            return null;
        }
        quickfix.Message message = messages.poll(10000,
                                                 TimeUnit.MILLISECONDS);
        Validate.notNull(message,
                         "Message not received in 10s");
        return message;
    }
    /**
     * Wait for and return the next from admin message.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>quickfix.Message</code> value
     * @throws InterruptedException if the call is interrupted while waiting for the next message
     */
    public quickfix.Message getNextFromAdminMessage(quickfix.SessionID inSessionId)
            throws Exception
    {
        quickfix.SessionID reversedSessionId = new quickfix.SessionID(inSessionId.getBeginString(),
                                                                      inSessionId.getTargetCompID(),
                                                                      inSessionId.getSenderCompID());
        BlockingDeque<quickfix.Message> messages;
        synchronized(fromAdminMessages) {
            messages = fromAdminMessages.get(reversedSessionId);
        }
        if(messages == null) {
            return null;
        }
        quickfix.Message message = messages.poll(10000,
                                                 TimeUnit.MILLISECONDS);
        Validate.notNull(message,
                         "Message not received in 10s");
        return message;
    }
    /**
     * Wait for and return the next to application message.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>quickfix.Message</code> value
     * @throws InterruptedException if the call is interrupted while waiting for the next message
     */
    public quickfix.Message getNextToApplicationMessage(quickfix.SessionID inSessionId)
            throws Exception
    {
        BlockingDeque<quickfix.Message> messages;
        synchronized(toAppMessages) {
            messages = toAppMessages.get(inSessionId);
        }
        if(messages == null) {
            return null;
        }
        quickfix.Message message = messages.poll(10000,
                                                 TimeUnit.MILLISECONDS);
        Validate.notNull(message,
                         "Message not received in 10s");
        return message;
    }
    /**
     * Wait for and return the next to admin message.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>quickfix.Message</code> value
     * @throws InterruptedException if the call is interrupted while waiting for the next message
     */
    public quickfix.Message getNextToAdminMessage(quickfix.SessionID inSessionId)
            throws Exception
    {
        BlockingDeque<quickfix.Message> messages;
        synchronized(toAdminMessages) {
            messages = toAdminMessages.get(inSessionId);
        }
        if(messages == null) {
            return null;
        }
        quickfix.Message message = messages.poll(10000,
                                                 TimeUnit.MILLISECONDS);
        Validate.notNull(message,
                         "Message not received in 10s");
        return message;
    }
    /**
     * Reset the object.
     */
    public void reset()
    {
        reset(fromAppMessages);
        reset(fromAdminMessages);
        reset(toAppMessages);
        reset(toAdminMessages);
    }
    /**
     * Get the messageFactory value.
     *
     * @return a <code>quickfix.MessageFactory</code> value
     */
    public quickfix.MessageFactory getMessageFactory()
    {
        return messageFactory;
    }
    /**
     * Sets the messageFactory value.
     *
     * @param inMessageFactory a <code>quickfix.MessageFactory</code> value
     */
    public void setMessageFactory(quickfix.MessageFactory inMessageFactory)
    {
        messageFactory = inMessageFactory;
    }
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>quickfix.SessionSettings</code> value
     */
    public quickfix.SessionSettings getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>quickfix.SessionSettings</code> value
     */
    public void setSessionSettings(quickfix.SessionSettings inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * Get the fixSettingsProviderFactory value.
     *
     * @return a <code>FixSettingsProviderFactory</code> value
     */
    public FixSettingsProviderFactory getFixSettingsProviderFactory()
    {
        return fixSettingsProviderFactory;
    }
    /**
     * Sets the fixSettingsProviderFactory value.
     *
     * @param inFixSettingsProviderFactory a <code>FixSettingsProviderFactory</code> value
     */
    public void setFixSettingsProviderFactory(FixSettingsProviderFactory inFixSettingsProviderFactory)
    {
        fixSettingsProviderFactory = inFixSettingsProviderFactory;
    }
    /**
     * Add the given message to the message container indicated by the session id.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     * @param inMessageContainers a <code>Map&lt;quickfix.SessionID,BlockingDeque&lt;quickfix.Message&gt;&gt;
     */
    protected void addMessageToContainer(quickfix.SessionID inSessionId,
                                         quickfix.Message inMessage,
                                         final Map<quickfix.SessionID,BlockingDeque<quickfix.Message>> inMessageContainers)
    {
        synchronized(inMessageContainers) {
            BlockingDeque<quickfix.Message> messages = inMessageContainers.get(inSessionId);
            System.out.println("COCO: adding " + inMessage.toString().replaceAll('\1'+""," ") + " to " + inSessionId + " for " + getClass().getSimpleName());
            messages.add(inMessage);
            inMessageContainers.notifyAll();
        }
    }
    /**
     * Reset the given container, emptying it of messages.
     *
     * @param inMessageContainer a Map&lt;quickfix.SessionID,BlockingDeque&lt;quickfix.Message&gt;&gt; value
     */
    protected void reset(Map<quickfix.SessionID,BlockingDeque<quickfix.Message>> inMessageContainer)
    {
        synchronized(inMessageContainer) {
            for(BlockingDeque<quickfix.Message> messages : inMessageContainer.values()) {
                messages.clear();
            }
            inMessageContainer.clear();
        }
    }
    /**
     * provides FIX provider settings
     */
    protected FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * holds from app messages received by session id
     */
    protected Map<quickfix.SessionID,BlockingDeque<quickfix.Message>> fromAppMessages = new HashMap<>();
    /**
     * holds from admin messages received by session id
     */
    protected Map<quickfix.SessionID,BlockingDeque<quickfix.Message>> fromAdminMessages = new HashMap<>();
    /**
     * holds to app messages received by session id
     */
    protected Map<quickfix.SessionID,BlockingDeque<quickfix.Message>> toAppMessages = new HashMap<>();
    /**
     * holds to admin messages received by session id
     */
    protected Map<quickfix.SessionID,BlockingDeque<quickfix.Message>> toAdminMessages = new HashMap<>();
    /**
     * creates FIX messages from incoming streams
     */
    protected quickfix.MessageFactory messageFactory;
    /**
     * session setting information
     */
    protected quickfix.SessionSettings sessionSettings;
}
