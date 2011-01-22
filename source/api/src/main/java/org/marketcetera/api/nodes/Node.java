package org.marketcetera.api.nodes;

import java.util.Set;

import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Node
        extends Lifecycle
{
    public NodeID getNodeID();
    public String getDescription();
    public Set<NodeCapability> getCapabilities();
    public NodeConfig getConfig();
}
