package org.marketcetera.core.fix;

import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;

/* $License$ */

/**
 * Creates {@link FixSettingsProvider} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSettingsProviderFactoryImpl
        implements FixSettingsProviderFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSettingsFactory#create()
     */
    @Override
    public FixSettingsProvider create()
    {
        return new FixSettingsProviderImpl(messageStoreFactoryClass,
                                           logFactoryClass,
                                           messageFactory,
                                           acceptorHost,
                                           acceptorProtocol,
                                           acceptorPort);
    }
    /**
     * Get the acceptorPort value.
     *
     * @return an <code>int</code> value
     */
    public int getAcceptorPort()
    {
        return acceptorPort;
    }
    /**
     * Sets the acceptorPort value.
     *
     * @param inAcceptorPort an <code>int</code> value
     */
    public void setAcceptorPort(int inAcceptorPort)
    {
        acceptorPort = inAcceptorPort;
    }
    /**
     * Get the acceptorHost value.
     *
     * @return a <code>String</code> value
     */
    public String getAcceptorHost()
    {
        return acceptorHost;
    }
    /**
     * Sets the acceptorHost value.
     *
     * @param inAcceptorHost a <code>String</code> value
     */
    public void setAcceptorHost(String inAcceptorHost)
    {
        acceptorHost = inAcceptorHost;
    }
    /**
     * Get the messageStoreFactoryClass value.
     *
     * @return a <code>Class&lt;MessageStoreFactory&gt;</code> value
     */
    public Class<MessageStoreFactory> getMessageStoreFactoryClass()
    {
        return messageStoreFactoryClass;
    }
    /**
     * Sets the messageStoreFactoryClass value.
     *
     * @param inMessageStoreFactoryClass a <code>Class&lt;MessageStoreFactory&gt;</code> value
     */
    public void setMessageStoreFactoryClass(Class<MessageStoreFactory> inMessageStoreFactoryClass)
    {
        messageStoreFactoryClass = inMessageStoreFactoryClass;
    }
    /**
     * Get the logFactoryClass value.
     *
     * @return a <code>Class&lt;LogFactory&gt;</code> value
     */
    public Class<LogFactory> getLogFactoryClass()
    {
        return logFactoryClass;
    }
    /**
     * Sets the logFactoryClass value.
     *
     * @param inLogFactoryClass a <code>Class&lt;LogFactory&gt;</code> value
     */
    public void setLogFactoryClass(Class<LogFactory> inLogFactoryClass)
    {
        logFactoryClass = inLogFactoryClass;
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
     * Get the acceptorProtocol value.
     *
     * @return a <code>String</code> value
     */
    public String getAcceptorProtocol()
    {
        return acceptorProtocol;
    }
    /**
     * Sets the acceptorProtocol value.
     *
     * @param inAcceptorProtocol a <code>String</code> value
     */
    public void setAcceptorProtocol(String inAcceptorProtocol)
    {
        acceptorProtocol = inAcceptorProtocol;
    }
    /**
     * message store factory class
     */
    private Class<MessageStoreFactory> messageStoreFactoryClass;
    /**
     * log factory class
     */
    private Class<LogFactory> logFactoryClass;
    /**
     * message factory
     */
    private MessageFactory messageFactory;
    /**
     * acceptor port value
     */
    private int acceptorPort;
    /**
     * acceptor host value
     */
    private String acceptorHost;
    /**
     * acceptor protocol value
     */
    private String acceptorProtocol = "TCP";
}
