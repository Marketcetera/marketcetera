package org.marketcetera.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;
import quickfix.UnsupportedMessageType;

/* $License$ */

/**
 * Sends messages and listens for responses.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class Sender
        implements Application
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(sessionSettings);
        Validate.notNull(messageFactory);
        applicationMessages.clear();
        initiators.clear();
        try {
            FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
            ThreadedSocketInitiator initiator = new ThreadedSocketInitiator(this,
                                                                            fixSettingsProvider.getMessageStoreFactory(sessionSettings),
                                                                            sessionSettings,
                                                                            fixSettingsProvider.getLogFactory(sessionSettings),
                                                                            messageFactory);
            initiator.start();
            initiators.add(initiator);
        } catch (ConfigError e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException(e);
        }
        SLF4JLoggerProxy.info(this,
                              "Message receiver started");
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        try {
            for(ThreadedSocketInitiator initiator : initiators) {
                initiator.stop(true);
            }
        } catch (Exception ignored) {
        }
        initiators.clear();
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} created",
                              inSessionId);
        synchronized(applicationMessages) {
            applicationMessages.put(inSessionId,
                                    new LinkedBlockingDeque<Message>());
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} logged on",
                              inSessionId);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} logged out",
                              inSessionId);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(Message inMessage,
                        SessionID inSessionId)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} sending admin message {}",
                               inSessionId,
                               inMessage);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromAdmin(Message inMessage,
                          SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received admin message {}",
                               inSessionId,
                               inMessage);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toApp(Message inMessage,
                      SessionID inSessionId)
            throws DoNotSend
    {
        SLF4JLoggerProxy.trace(this,
                               "{} sending app message {}",
                               inSessionId,
                               inMessage);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromApp(Message inMessage,
                        SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received app message {}",
                               inSessionId,
                               inMessage);
        synchronized(applicationMessages) {
            BlockingDeque<Message> messages = applicationMessages.get(inSessionId);
            messages.add(inMessage);
        }
    }
    /**
     * Wait for and return the next application message.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws InterruptedException if the call is interrupted while waiting for the next message
     */
    public Message getNextApplicationMessage(SessionID inSessionId)
            throws Exception
    {
        SessionID reversedSessionId = new SessionID(inSessionId.getBeginString(),
                                                    inSessionId.getTargetCompID(),
                                                    inSessionId.getSenderCompID());
        BlockingDeque<Message> messages;
        synchronized(applicationMessages) {
            messages = applicationMessages.get(reversedSessionId);
        }
        if(messages == null) {
            return null;
        }
        Message message = messages.poll(10000,
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
        synchronized(applicationMessages) {
            for(BlockingDeque<Message> messages : applicationMessages.values()) {
                messages.clear();
            }
        }
    }
    /**
     * Get the messageFactory value.
     *
     * @return a <code>MessageFactory</code> value
     */
    public MessageFactory getMessageFactory()
    {
        return messageFactory;
    }
    /**
     * Sets the messageFactory value.
     *
     * @param inMessageFactory a <code>MessageFactory</code> value
     */
    public void setMessageFactory(MessageFactory inMessageFactory)
    {
        messageFactory = inMessageFactory;
    }
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>SessionSettings</code> value
     */
    public SessionSettings getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     */
    public void setSessionSettings(SessionSettings inSessionSettings)
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
     * provides FIX provider settings
     */
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * holds application messages received by session id
     */
    private Map<SessionID,BlockingDeque<Message>> applicationMessages = new HashMap<>();
    /**
     * creates FIX messages from incoming streams
     */
    private MessageFactory messageFactory;
    /**
     * active initiators
     */
    private final List<ThreadedSocketInitiator> initiators = new ArrayList<>();
    /**
     * session setting information
     */
    private SessionSettings sessionSettings;
}
