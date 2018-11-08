package org.marketcetera.cluster;

import org.marketcetera.cluster.service.ClusterMember;

/* $License$ */

/**
 * Creates {@link SimpleClusterMember} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleClusterMemberFactory
        implements ClusterMemberFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.DomainObjectFactory#create(org.marketcetera.core.DomainObject)
     */
    @Override
    public SimpleClusterMember create(ClusterMember inClusterMember)
    {
        return new SimpleClusterMember(inClusterMember);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.cluster.ClusterMemberFactory#create(java.lang.String)
     */
    @Override
    public SimpleClusterMember create(String inUuid)
    {
        return new SimpleClusterMember(inUuid);
    }
}
