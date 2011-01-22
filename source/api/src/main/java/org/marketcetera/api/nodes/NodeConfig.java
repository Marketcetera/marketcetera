package org.marketcetera.api.nodes;

import java.util.Set;

import org.marketcetera.api.Config;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NodeConfig
        extends Config
{
    public Set<NodeCapability> getCapabilities();
}
