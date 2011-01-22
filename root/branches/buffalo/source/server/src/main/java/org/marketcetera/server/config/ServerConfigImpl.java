package org.marketcetera.server.config;

import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.nodes.Node;
import org.marketcetera.api.nodes.NodeConfig;
import org.marketcetera.api.server.ServerConfig;
import org.marketcetera.util.misc.ClassVersion;

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
        implements ServerConfig
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
     * @see org.marketcetera.api.server.ServerConfig#getNodes()
     */
    @Override
    public Set<Node> getNodes()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.server.ServerConfig#setNodes(java.util.Set)
     */
    @Override
    public void setNodes(Set<Node> inNodes)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("ServerConfig [id=%s, description=%s]",
                             id,
                             description);
    }
    /**
     * 
     *
     *
     * @return
     */
    public static ServerConfigImpl getInstance()
    {
        synchronized(ServerConfigImpl.class) {
            return instance;
        }
    }
    public ServerConfigImpl(String id,
                            String description,
                            Set<NodeConfig> nodes)
    {
        this.id = StringUtils.trimToNull(id);
        this.description = StringUtils.trimToNull(description);
        Validate.notNull(this.id,
                         "Server name must not be null");
        Validate.notNull(this.description,
                         "Server description must not be null");
        synchronized(ServerConfigImpl.class) {
            instance = this;
        }
    }
    /**
     * 
     */
    @GuardedBy("SpringConfig.class")
    private static ServerConfigImpl instance;
    private final String id;
    private final String description;
}
