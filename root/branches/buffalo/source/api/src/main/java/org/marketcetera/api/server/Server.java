package org.marketcetera.api.server;

import java.util.List;

import org.marketcetera.api.nodes.Node;
import org.marketcetera.api.nodes.NodeCapability;
import org.marketcetera.api.nodes.NodeID;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides Marketcetera server services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Server
        extends Lifecycle
{
    public List<Node> getNodes();
    public List<Node> getNodesFor(NodeCapability inRequestedCapability);
    public void addNode(Node inNode);
    public void removeNode(NodeID inNodeID)
            throws IllegalArgumentException;
}
