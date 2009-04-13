package org.marketcetera.ors.config;

import javax.jms.ConnectionFactory;
import org.marketcetera.core.IDFactory;
import org.marketcetera.ors.brokers.SpringBrokers;
import org.marketcetera.ors.brokers.SpringSelector;
import org.marketcetera.ors.filters.MessageFilter;
import org.marketcetera.ors.filters.MessageFilterNoop;
import org.marketcetera.ors.filters.OrderFilter;
import org.marketcetera.ors.filters.OrderFilterNoop;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.Node;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

/**
 * The application's Spring-based configuration. A global singleton
 * instance of this class is created by Spring during the creation of
 * the application's {@link ApplicationContext}, and it contains all
 * end-user configuration of the application.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringConfig
    implements InitializingBean
{

    // CLASS DATA.

    private static SpringConfig sSingleton;


    // INSTANCE DATA.

    private SpringBrokers mBrokers;
    private SpringSelector mSelector;
    private MessageFilter mSupportedMessages;
    private OrderFilter mAllowedOrders;
    private String mServerHost;
    private int mServerPort;
    private long mServerSessionLife;
    private ConnectionFactory mIncomingCF;
    private ConnectionFactory mOutgoingCF;
    private IDFactory mIDFactory;


    // CONSTRUCTORS.

    /**
     * Creates a new application configuration, which becomes the
     * global singleton.
     */

    public SpringConfig()
    {
        mSupportedMessages=new MessageFilterNoop();
        mAllowedOrders=new OrderFilterNoop();
        mServerHost=Node.DEFAULT_HOST;
        mServerPort=Node.DEFAULT_PORT;
        mServerSessionLife=SessionManager.INFINITE_SESSION_LIFESPAN;
        setSingleton(this);
    }

    /**
     * Creates a new application configuration with the given
     * properties. The new configuration becomes the global singleton.
     *
     * @param brokers The broker configurations.
     * @param selector The broker selector.
     * @param supportedMessages The filter of supported messages.
     * @param allowedOrders The filter of allowed orders.
     * @param serverHost The host name for web services.
     * @param serverPort The port for web services.
     * @param serverSessionLife The web services session lifetime, in
     * seconds.
     * @param incomingCF The connection factory for incoming connections.
     * @param outgoingCF The connection factory for outgoing connections.
     * @param idFactory The ID generation factory.
     */

    public SpringConfig
        (SpringBrokers brokers,
         SpringSelector selector,
         MessageFilter supportedMessages,
         OrderFilter allowedOrders,
         String serverHost,
         int serverPort,
         long serverSessionLife,
         ConnectionFactory incomingCF,
         ConnectionFactory outgoingCF,
         IDFactory idFactory)
        throws I18NException
    {
        setBrokers(brokers);
        setSelector(selector);
        setSupportedMessages(supportedMessages);
        setAllowedOrders(allowedOrders);
        setServerHost(serverHost);
        setServerPort(serverPort);
        setServerSessionLife(serverSessionLife);
        setIncomingConnectionFactory(incomingCF);
        setOutgoingConnectionFactory(outgoingCF);
        setIDFactory(idFactory);
        afterPropertiesSet();
        setSingleton(this);
    }


    // CLASS METHODS.

    /**
     * Sets the global singleton configuration to the given one.
     *
     * @param singleton The configuration. It may be null.
     */

    public static void setSingleton
        (SpringConfig singleton)
    {
        sSingleton=singleton;
    }

    /**
     * Returns the global singleton configuration.
     *
     * @return The configuration. It may be null.
     */

    public static SpringConfig getSingleton()
    {
        return sSingleton;
    }


    // INSTANCE METHODS.

    /**
     * Sets the receiver's broker configurations to the given ones. A
     * non-null value should be set during the receiver's
     * initialization.
     *
     * @param brokers The configurations.
     */

    public void setBrokers
        (SpringBrokers brokers)
    {
        mBrokers=brokers;
    }

    /**
     * Returns the receiver's broker configurations.
     *
     * @return The configurations.
     */

    public SpringBrokers getBrokers()
    {
        return mBrokers;
    }

    /**
     * Sets the receiver's broker selector to the given one. A
     * non-null value should be set during the receiver's
     * initialization.
     *
     * @param selector The selector.
     */

    public void setSelector
        (SpringSelector selector)
    {
        mSelector=selector;
    }

    /**
     * Returns the receiver's broker selector.
     *
     * @return The selector.
     */

    public SpringSelector getSelector()
    {
        return mSelector;
    }

    /**
     * Sets the receiver's filter of supported messages to the given
     * one. If this method is not called during initialization, the
     * filter defaults to a {@link MessageFilterNoop}.
     *
     * @param supportedMessages The filter.
     */

    public void setSupportedMessages
        (MessageFilter supportedMessages)
    {
        mSupportedMessages=supportedMessages;
    }

    /**
     * Returns the receiver's filter of supported messages.
     *
     * @return The filter.
     */

    public MessageFilter getSupportedMessages()
    {
        return mSupportedMessages;
    }

    /**
     * Sets the receiver's filter of allowed orders to the given one.
     * If this method is not called during initialization, the filter
     * defaults to a {@link OrderFilterNoop}.
     *
     * @param allowedOrders The filter.
     */

    public void setAllowedOrders
        (OrderFilter allowedOrders)
    {
        mAllowedOrders=allowedOrders;
    }

    /**
     * Returns the receiver's filter of allowed orders.
     *
     * @return The filter.
     */

    public OrderFilter getAllowedOrders()
    {
        return mAllowedOrders;
    }

    /**
     * Sets the receiver's host name for web services to the given
     * value. If this method is not called during initialization, the
     * host name defaults to {@link Node#DEFAULT_HOST}.
     *
     * @param serverHost The host name.
     */

    public void setServerHost
        (String serverHost)
    {
        mServerHost=serverHost;
    }

    /**
     * Returns the receiver's host name for web services.
     *
     * @return The host name.
     */

    public String getServerHost()
    {
        return mServerHost;
    }

    /**
     * Sets the receiver's port for web services to the given
     * value. If this method is not called during initialization, the
     * port defaults to {@link Node#DEFAULT_PORT}.
     *
     * @param serverPort The port.
     */

    public void setServerPort
        (int serverPort)
    {
        mServerPort=serverPort;
    }

    /**
     * Returns the receiver's port for web services.
     *
     * @return The port.
     */

    public int getServerPort()
    {
        return mServerPort;
    }

    /**
     * Sets the receiver's web services session lifetime to the given
     * value. If this method is not called during initialization, the
     * lifetime defaults to {@link
     * SessionManager#INFINITE_SESSION_LIFESPAN}.
     *
     * @param serverSessionLife The lifetime, in seconds.
     */

    public void setServerSessionLife
        (long serverSessionLife)
    {
        mServerSessionLife=serverSessionLife;
    }

    /**
     * Returns the receiver's web services session lifetime.
     *
     * @return The lifetime, in seconds.
     */

    public long getServerSessionLife()
    {
        return mServerSessionLife;
    }

    /**
     * Sets the receiver's connection factory for incoming connections
     * to the given one. A non-null value should be set during the
     * receiver's initialization.
     *
     * @param incomingCF The factory.
     */

    public void setIncomingConnectionFactory
        (ConnectionFactory incomingCF)
    {
        mIncomingCF=incomingCF;
    }

    /**
     * Returns the receiver's connection factory for incoming
     * connections.
     *
     * @return The factory.
     */

    public ConnectionFactory getIncomingConnectionFactory()
    {
        return mIncomingCF;
    }

    /**
     * Sets the receiver's connection factory for outgoing connections
     * to the given one. A non-null value should be set during the
     * receiver's initialization.
     *
     * @param outgoingCF The factory.
     */

    public void setOutgoingConnectionFactory
        (ConnectionFactory outgoingCF)
    {
        mOutgoingCF=outgoingCF;
    }

    /**
     * Returns the receiver's connection factory for outgoing
     * connections.
     *
     * @return The factory.
     */

    public ConnectionFactory getOutgoingConnectionFactory()
    {
        return mOutgoingCF;
    }

    /**
     * Sets the receiver's ID generation factory to the given one. A
     * non-null value should be set during the receiver's
     * initialization.
     *
     * @param idFactory The factory.
     */

    public void setIDFactory
        (IDFactory idFactory)
    {
        mIDFactory=idFactory;
    }

    /**
     * Returns the receiver's ID generation factory.
     *
     * @return The factory.
     */

    public IDFactory getIDFactory()
    {
        return mIDFactory;
    }


    // InitializingBean.

    @Override
    public void afterPropertiesSet()
        throws I18NException
    {
        if (getBrokers()==null) {
            throw new I18NException(Messages.NO_BROKERS);
        }
        if (getSelector()==null) {
            throw new I18NException(Messages.NO_SELECTOR);
        }
        if (getIncomingConnectionFactory()==null) {
            throw new I18NException(Messages.NO_INCOMING_CONNECTION_FACTORY);
        }
        if (getOutgoingConnectionFactory()==null) {
            throw new I18NException(Messages.NO_OUTGOING_CONNECTION_FACTORY);
        }
        if (getIDFactory()==null) {
            throw new I18NException(Messages.NO_ID_FACTORY);
        }
    }
}
