package org.marketcetera.core.fix;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides static fix session settings.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSettingsProviderImpl
        implements FixSettingsProvider
{
    /**
     * Create a new FixSettingsProviderImpl instance.
     *
     * @param inMessageStoreFactoryClass a <code>Class&lt;MessageStoreFactory&gt;</code> value
     * @param inLogFactoryClass a <code>Class&lt;LogFactory&gt;</code> value
     * @param inMessageFactory a <code>MessageFactory</code> value
     * @param inAcceptorHost a <code>String</code> value
     * @param inAcceptorProtocol a <code>String<code> value
     * @param inAcceptorPort an <code>int</code> value
     */
    public FixSettingsProviderImpl(Class<MessageStoreFactory> inMessageStoreFactoryClass,
                                   Class<LogFactory> inLogFactoryClass,
                                   MessageFactory inMessageFactory,
                                   String inAcceptorHost,
                                   String inAcceptorProtocol,
                                   int inAcceptorPort)
    {
        messageStoreFactoryClass = inMessageStoreFactoryClass;
        logFactoryClass = inLogFactoryClass;
        messageFactory = inMessageFactory;
        acceptorHost = inAcceptorHost;
        acceptorPort = inAcceptorPort;
        acceptorProtocol = inAcceptorProtocol;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSettings#getMessageStoreFactory()
     */
    @Override
    public MessageStoreFactory getMessageStoreFactory(SessionSettings inSessionSettings)
    {
        try {
            Constructor<MessageStoreFactory> messageStoreFactoryConstructor = messageStoreFactoryClass.getConstructor(SessionSettings.class);
            return messageStoreFactoryConstructor.newInstance(inSessionSettings);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSettings#getLogFactory()
     */
    @Override
    public LogFactory getLogFactory(SessionSettings inSessionSettings)
    {
        try {
            Constructor<LogFactory> logFactoryConstructor = logFactoryClass.getConstructor(SessionSettings.class);
            return logFactoryConstructor.newInstance(inSessionSettings);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSettings#getMessageFactory()
     */
    @Override
    public MessageFactory getMessageFactory()
    {
        return messageFactory;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSettingsProvider#getAcceptorPort()
     */
    @Override
    public int getAcceptorPort()
    {
        return acceptorPort;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSettingsProvider#getAcceptorHost()
     */
    @Override
    public String getAcceptorHost()
    {
        return acceptorHost;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSettingsProvider#getAcceptorProtocol()
     */
    @Override
    public String getAcceptorProtocol()
    {
        return acceptorProtocol;
    }
    /**
     * acceptor socket protocol value
     */
    private String acceptorProtocol;
    /**
     * acceptor port value
     */
    private int acceptorPort;
    /**
     * acceptor host value
     */
    private String acceptorHost;
    /**
     * message store factory class value
     */
    private Class<MessageStoreFactory> messageStoreFactoryClass;
    /**
     * log factory class value
     */
    private Class<LogFactory> logFactoryClass;
    /**
     * message factory value
     */
    private MessageFactory messageFactory;
}
