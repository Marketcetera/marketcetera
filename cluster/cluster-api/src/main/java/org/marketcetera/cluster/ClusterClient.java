package org.marketcetera.cluster;

import java.util.Collection;

import org.marketcetera.cluster.service.ClusterMember;
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
     * Get the cluster members;
     *
     * @return a <code>Collection&lt;ClusterMember&gt;</code> value
     */
    Collection<ClusterMember> getClusterMembers();
}
