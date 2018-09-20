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
}
