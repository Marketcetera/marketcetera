package org.marketcetera.cluster;

import org.marketcetera.core.MutableDomainObjectFactory;

/* $License$ */

/**
 * Creates {@link MutableClusterData} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableClusterDataFactory
        extends ClusterDataFactory,MutableDomainObjectFactory<ClusterData,MutableClusterData>
{
}
