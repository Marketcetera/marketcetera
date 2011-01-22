package org.marketcetera.api.server;

import java.util.Set;

import org.marketcetera.api.Config;
import org.marketcetera.api.nodes.Node;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServerConfig
        extends Config
{
    public Set<Node> getNodes();
    public void setNodes(Set<Node> inNodes);
}
