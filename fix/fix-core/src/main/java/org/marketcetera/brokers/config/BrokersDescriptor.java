package org.marketcetera.brokers.config;

import java.util.Collection;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Describes a collection of {@link Broker} objects in a Spring-compatible POJO.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BrokersDescriptor
{
    /**
     * Get the brokers value.
     *
     * @return a <code>Collection&lt;BrokerDescriptor&gt;</code> value
     */
    public Collection<BrokerDescriptor> getBrokers()
    {
        return brokers;
    }
    /**
     * Sets the brokers value.
     *
     * @param inBrokers a <code>Collection&lt;BrokerDescriptor&gt;</code> value
     */
    public void setBrokers(Collection<BrokerDescriptor> inBrokers)
    {
        brokers = inBrokers;
    }
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>SessionSettingsDescriptor</code> value
     */
    public SessionSettingsDescriptor getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>SessionSettingsDescriptor</code> value
     */
    public void setSessionSettings(SessionSettingsDescriptor inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * brokers value
     */
    private Collection<BrokerDescriptor> brokers = Lists.newArrayList();
    /**
     * session settings that apply to all brokers
     */
    private SessionSettingsDescriptor sessionSettings;
}
