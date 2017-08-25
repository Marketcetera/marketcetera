package org.marketcetera.fix;

import javax.annotation.PostConstruct;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
@Component
@EnableAutoConfiguration
@ConfigurationProperties(prefix="fix")
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
     * Validate and start the object
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.debug(this,
                               "FIX Settings: {} {} {} {}",
                               acceptorHost,
                               acceptorPort,
                               logFactoryClass,
                               messageStoreFactoryClass);
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
     * message factory
     */
    @Autowired
    private MessageFactory messageFactory;
    /**
     * message store factory class
     */
    @Value("${metc.fix.message.store.factory.class:org.marketcetera.fix.store.NoopStoreFactory}")
    private Class<MessageStoreFactory> messageStoreFactoryClass;
    /**
     * log factory class
     */
    @Value("${metc.fix.log.factory.class:quickfix.SLF4JLogFactory}")
    private Class<LogFactory> logFactoryClass;
    /**
     * acceptor port value
     */
    @Value("${metc.fix.acceptor.port:9800}")
    private int acceptorPort;
    /**
     * acceptor host value
     */
    @Value("${metc.fix.acceptor.host:127.0.0.1}")
    private String acceptorHost;
    /**
     * acceptor protocol value
     */
    private String acceptorProtocol = "TCP";
}
