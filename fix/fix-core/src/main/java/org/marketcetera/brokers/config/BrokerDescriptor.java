package org.marketcetera.brokers.config;

import java.util.List;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.trade.BrokerID;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Describes a {@link Broker} in a Spring-compatible POJO.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BrokerDescriptor
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Get the id value.
     *
     * @return a <code>BrokerID</code> value
     */
    public BrokerID getId()
    {
        return id;
    }
    /**
     * Sets the id value.
     *
     * @param inId a <code>BrokerID</code> value
     */
    public void setId(BrokerID inId)
    {
        id = inId;
    }
    /**
     * Get the affinity value.
     *
     * @return a <code>int</code> value
     */
    public int getAffinity()
    {
        return affinity;
    }
    /**
     * Sets the affinity value.
     *
     * @param inAffinity a <code>int</code> value
     */
    public void setAffinity(int inAffinity)
    {
        affinity = inAffinity;
    }
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    public String getHost()
    {
        return host;
    }
    /**
     * Sets the host value.
     *
     * @param inHost a <code>String</code> value
     */
    public void setHost(String inHost)
    {
        host = inHost;
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Get the brokerAlgos value.
     *
     * @return a <code>List&gt;BrokerAlgoSpec&gt;</code> value
     */
    public List<BrokerAlgoSpec> getBrokerAlgos()
    {
        return brokerAlgos;
    }
    /**
     * Sets the brokerAlgos value.
     *
     * @param inBrokerAlgos a <code>List&gt;BrokerAlgoSpec&gt;</code> value
     */
    public void setBrokerAlgos(List<BrokerAlgoSpec> inBrokerAlgos)
    {
        brokerAlgos = inBrokerAlgos;
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
     * Get the orderModifiers value.
     *
     * @return a <code>List&gt;MessageModifier&gt;</code> value
     */
    public List<MessageModifier> getOrderModifiers()
    {
        return orderModifiers;
    }
    /**
     * Sets the orderModifiers value.
     *
     * @param inOrderModifiers a <code>List&gt;MessageModifier&gt;</code> value
     */
    public void setOrderModifiers(List<MessageModifier> inOrderModifiers)
    {
        orderModifiers = inOrderModifiers;
    }
    /**
     * Get the responseModifiers value.
     *
     * @return a <code>List&gt;MessageModifier&gt;</code> value
     */
    public List<MessageModifier> getResponseModifiers()
    {
        return responseModifiers;
    }
    /**
     * Sets the responseModifiers value.
     *
     * @param inResponseModifiers a <code>List&gt;MessageModifier&gt;</code> value
     */
    public void setResponseModifiers(List<MessageModifier> inResponseModifiers)
    {
        responseModifiers = inResponseModifiers;
    }
    /**
     * name value
     */
    private String name;
    /**
     * description value
     */
    private String description;
    /**
     * host value
     */
    private String host;
    /**
     * port value
     */
    private int port;
    /**
     * broker id value
     */
    private BrokerID id;
    /**
     * affinity value
     */
    private int affinity = 1;
    /**
     * broker algos value
     */
    private List<BrokerAlgoSpec> brokerAlgos = Lists.newArrayList();
    /**
     * session settings value
     */
    private SessionSettingsDescriptor sessionSettings;
    /**
     * order modifiers value
     */
    private List<MessageModifier> orderModifiers = Lists.newArrayList();
    /**
     * response modifiers value
     */
    private List<MessageModifier> responseModifiers = Lists.newArrayList();
}
