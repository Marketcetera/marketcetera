package org.marketcetera.cluster;

import org.marketcetera.core.BaseClient;

/* $License$ */

/**
 * Provides access to cluster services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterClient
        extends BaseClient
{
    /**
     * Get the instance data for the given affinity.
     *
     * @param inAffinity an <code>int</code> value
     * @return an <code>InstanceData</code> value
     */
    InstanceData getInstanceData(int inAffinity);
}
