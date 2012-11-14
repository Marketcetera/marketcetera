package org.marketcetera.core.node;

import java.util.Collection;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NodeMaster
{
    public void register(Node inNode);
    public void unregister(Node inNode);
    public Collection<Node> getNodes();
}
