package org.marketcetera.cluster;

import org.marketcetera.cluster.service.ClusterMember;

/* $License$ */

/**
 * Creates {@link ClusterMember} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterMemberFactory
{
    /**
     * Create a <code>ClusterMember</code> object.
     *
     * @param inClusterMember a <code>ClusterMember</code> value
     * @return a <code>ClusterMember</code> value
     */
    public ClusterMember create(ClusterMember inClusterMember);
    /**
     * Create a <code>ClusterMember</code> object.
     *
     * @param inUuid a <code>String</code> value
     * @return a <code>ClusterMember</code> value
     */
    public ClusterMember create(String inUuid);
}
