package org.marketcetera.cluster;

import org.marketcetera.cluster.service.ClusterMember;

/* $License$ */

/**
 * Provides a POJO {@link ClusterMember} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleClusterMember
        extends AbstractClusterMember
{
    /**
     * Create a new SimpleClusterMember instance.
     *
     * @param inClusterMember a <code>ClusterMember</code> value
     */
    public SimpleClusterMember(ClusterMember inClusterMember)
    {
        super(inClusterMember.getUuid());
    }
    /**
     * Create a new SimpleClusterMember instance.
     *
     * @param inUuid a <code>String</code> value
     */
    public SimpleClusterMember(String inUuid)
    {
        super(inUuid);
    }
    /**
     * Create a new SimpleClusterMember instance.
     */
    public SimpleClusterMember()
    {
        super(null);
    }
}
