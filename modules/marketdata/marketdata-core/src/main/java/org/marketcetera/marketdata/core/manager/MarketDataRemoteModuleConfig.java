package org.marketcetera.marketdata.core.manager;

import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Provides configuration options for {@link MarketDataRemoteModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRemoteModuleConfig
{
    /**
     * Get the threadpoolSize value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadpoolSize()
    {
        return threadpoolSize;
    }
    /**
     * Sets the threadpoolSize value.
     *
     * @param inThreadpoolSize an <code>int</code> value
     */
    public void setThreadpoolSize(int inThreadpoolSize)
    {
        threadpoolSize = inThreadpoolSize;
    }
    /**
     * Get the pollingInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getPollingInterval()
    {
        return pollingInterval;
    }
    /**
     * Sets the pollingInterval value.
     *
     * @param inPollingInterval a <code>long</code> value
     */
    public void setPollingInterval(long inPollingInterval)
    {
        pollingInterval = inPollingInterval;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Get the password value.
     *
     * @return a <code>String</code> value
     */
    public String getPassword()
    {
        return password;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
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
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
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
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /**
     * threadpool size value
     */
    private int threadpoolSize = 10;
    /**
     * polling interval value
     */
    private long pollingInterval = 1000;
    /**
     * username value
     */
    private String username;
    /**
     * password value
     */
    private String password;
    /**
     * hostname value
     */
    private String hostname;
    /**
     * port value
     */
    private int port;
    /**
     * provides context classes for marshalling and unmarshalling
     */
    private ContextClassProvider contextClassProvider;
}
