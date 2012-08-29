package org.marketcetera.client.config;

import java.util.ArrayList;
import java.util.Collection;

import javax.jms.ConnectionFactory;

import org.marketcetera.client.OrderModifier;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

/**
 * The application's Spring-based configuration. A global singleton
 * instance of this class is created by Spring during the creation of
 * the application's {@link ApplicationContext}, and it contains all
 * end-user configuration of the application.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
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

    private ConnectionFactory mIncomingCF;
    private ConnectionFactory mOutgoingCF;
    private final Collection<OrderModifier> orderModifiers = new ArrayList<OrderModifier>();


    // CONSTRUCTORS.

    /**
     * Creates a new application configuration, which becomes the
     * global singleton.
     */

    public SpringConfig()
    {
        setSingleton(this);
    }

    /**
     * Creates a new application configuration with the given
     * properties. The new configuration becomes the global singleton.
     *
     * @param incomingCF The connection factory for incoming connections.
     * @param outgoingCF The connection factory for outgoing connections.
     * @param inOrderModifiers a <code>Collection&lt;OrderModifier&gt;</code> value or <code>null</code>
     */
    public SpringConfig(ConnectionFactory incomingCF,
                        ConnectionFactory outgoingCF,
                        Collection<OrderModifier> inOrderModifiers)
        throws I18NException
    {
        setIncomingConnectionFactory(incomingCF);
        setOutgoingConnectionFactory(outgoingCF);
        if(inOrderModifiers != null) {
            orderModifiers.addAll(inOrderModifiers);
        }
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
     * Get the orderModifiers value.
     *
     * @return a <code>Collection&lt;OrderModifier&gt;</code> value
     */
    public Collection<OrderModifier> getOrderModifiers()
    {
        return Collections.unmodifiableCollection(orderModifiers);
    }
    /**
     * Sets the orderModifiers value.
     *
     * @param a <code>Collection&lt;OrderModifier&gt;</code> value
     */
    public void setOrderModifiers(Collection<OrderModifier> inOrderModifiers)
    {
        synchronized(orderModifiers) {
            orderModifiers.clear();
            if(inOrderModifiers != null) {
                orderModifiers.addAll(inOrderModifiers);
            }
        }
    }
    // InitializingBean.

    @Override
    public void afterPropertiesSet()
        throws I18NException
    {
        if (getIncomingConnectionFactory()==null) {
            throw new I18NException(Messages.NO_INCOMING_CONNECTION_FACTORY);
        }
        if (getOutgoingConnectionFactory()==null) {
            throw new I18NException(Messages.NO_OUTGOING_CONNECTION_FACTORY);
        }
    }
}
