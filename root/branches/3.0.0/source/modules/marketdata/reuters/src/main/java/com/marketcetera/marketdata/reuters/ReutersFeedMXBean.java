package com.marketcetera.marketdata.reuters;

import javax.management.MXBean;

import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.module.DisplayName;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Defines the attributes of the {@link ReutersFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedMXBean.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
@MXBean(true)
@DisplayName("Management Interface for Reuters Marketdata Feed")
@ClassVersion("$Id: ReutersFeedMXBean.java 82351 2012-05-04 21:46:58Z colin $")
public interface ReutersFeedMXBean
        extends AbstractMarketDataModuleMXBean
{
    /**
     * Sets the server type value.
     *
     * @param inServerType a <code>String</code> value
     */
    @DisplayName("Server Type Value")
    public void setServerType(@DisplayName("Server Type Value")String inServerType);
    /**
     * Gets the server type value.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("Server Type Value")
    public String getServerType();
    /**
     * Sets the server list value.
     *
     * @param inServerList a <code>String</code> value
     */
    @DisplayName("Server List Value")
    public void setServerList(@DisplayName("Server List Value")String inServerList);
    /**
     * Gets the server list value.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("Server List Value")
    public String getServerList();
    /**
     * Sets the port number value.
     * 
     * @param inPortNumberValue a <code>String</code> value
     */
    @DisplayName("Port Number Value")
    public void setPortNumber(@DisplayName("Port Number Value")String inPortNumberValue);
    /**
     * Gets the port number value.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("Port Number Value")
    public String getPortNumber();
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    @DisplayName("Username Value")
    public void setUsername(@DisplayName("Username Value")String inUsername);
    /**
     * Gets the username value.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("Username Value")
    public String getUsername();
}
