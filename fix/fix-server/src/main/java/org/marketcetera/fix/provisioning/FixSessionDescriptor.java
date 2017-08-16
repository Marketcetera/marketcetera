package org.marketcetera.fix.provisioning;

import org.marketcetera.fix.FixSession;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Describes a {@link FixSession} in a Spring-compatible POJO.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionDescriptor
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
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /**
     * Sets the id value.
     *
     * @param inId a <code>BrokerID</code> value
     */
    public void setBrokerId(BrokerID inId)
    {
        brokerId = inId;
    }
    /**
     * Get the affinity value.
     *
     * @return an <code>int</code> value
     */
    public int getAffinity()
    {
        return affinity;
    }
    /**
     * Sets the affinity value.
     *
     * @param inAffinity an <code>int</code> value
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
    private BrokerID brokerId;
    /**
     * affinity value
     */
    private int affinity = 1;
    /**
     * session settings
     */
    private SessionSettingsDescriptor sessionSettings;
}
