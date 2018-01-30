package org.marketcetera.webui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides the server connection configuration.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class HostnameConfiguration
{
    /**
     * Create a new HostnameConfiguration instance.
     */
    public HostnameConfiguration()
    {
        instance = this;
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
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
     * Get the instance value.
     *
     * @return a <code>HostnameConfiguration</code> value
     */
    public static HostnameConfiguration getInstance()
    {
        return instance;
    }
    /**
     * hostname to connect to
     */
    @Value("${host.name}")
    private String hostname;
    /**
     * port to connect to
     */
    @Value("${host.port}")
    private int port;
    /**
     * static instance value
     */
    private static HostnameConfiguration instance;
}
