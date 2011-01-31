package org.marketcetera.server.config;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.Validate;
import org.marketcetera.api.server.ServerConfig;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class ServerConfigImpl
        implements ServerConfig, InitializingBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.Config#getID()
     */
    @Override
    public String getID()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.Config#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.server.ServerConfig#getHostname()
     */
    @Override
    public String getHostname()
    {
        return hostname;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.server.ServerConfig#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
    }
    /**
     * Get the id value.
     *
     * @return a <code>String</code> value
     */
    public String getId()
    {
        return id;
    }
    /**
     * Sets the id value.
     *
     * @param a <code>String</code> value
     */
    public void setId(String inId)
    {
        id = inId;
    }
    /**
     * Sets the description value.
     *
     * @param a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Sets the hostname value.
     *
     * @param a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Sets the port value.
     *
     * @param a <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(id,
                         "Server name must not be null");
        Validate.notNull(description,
                         "Server description must not be null");
        Validate.notNull(hostname,
                         "Server hostname must not be null");
        // TODO validate port
        // TODO resolve hostname?
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("ServerConfig [%s %s %s:%s]",
                             id,
                             description,
                             getHostname(),
                             port);
    }
    /**
     * 
     */
    private volatile String id;
    /**
     * 
     */
    private volatile String description;
    /**
     * 
     */
    private volatile String hostname;
    /**
     * 
     */
    private volatile int port;
}
