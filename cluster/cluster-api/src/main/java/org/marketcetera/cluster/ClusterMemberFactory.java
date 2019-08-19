package org.marketcetera.cluster;

import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.core.DomainObjectFactory;

/* $License$ */

/**
 * Creates {@link ClusterMember} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterMemberFactory
        extends DomainObjectFactory<ClusterMember>
{
    /**
     * Create a <code>ClusterMember</code> object.
     *
     * @param inUuid a <code>String</code> value
     * @return a <code>ClusterMember</code> value
     */
    public ClusterMember create(String inUuid);
}
