package org.marketcetera.node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.nodes.NodeCapability;
import org.marketcetera.api.nodes.NodeConfig;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NodeConfigImpl
        implements NodeConfig
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
     * @see org.marketcetera.api.nodes.NodeConfig#getCapabilities()
     */
    @Override
    public Set<NodeCapability> getCapabilities()
    {
        return capabilities;
    }
    public NodeConfigImpl(String id,
                          String description,
                          Set<NodeCapability> capabilities)
    {
        this.id = StringUtils.trimToNull(id);
        Validate.notNull(this.id,
                         "Node id must not be null");
        this.description = StringUtils.trimToNull(description);
        Validate.notNull(this.description,
                         "Node " + this.id + " description must not be null");
        this.capabilities = Collections.unmodifiableSet(new HashSet<NodeCapability>(capabilities));
    }
    private final String id;
    private final String description;
    private final Set<NodeCapability> capabilities;
}
