package org.marketcetera.cluster;

import org.marketcetera.core.DomainObjectFactory;

/* $License$ */

/**
 * Creates {@link ClusterData} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterDataFactory
        extends DomainObjectFactory<ClusterData>
{
    /**
     * Create a {@link ClusterDataFactory} object.
     *
     * @param inTotalInstances an <code>int</code> value
     * @param inHostId a <code>String</code> value
     * @param inHostNumber an <code>int</code> value
     * @param inInstanceNumber an <code>int</code> value
     * @param inMemberUuid a <code>String</code> value
     * @return a <code>ClusterData</code> value
     */
    ClusterData create(int inTotalInstances,
                       String inHostId,
                       int inHostNumber,
                       int inInstanceNumber,
                       String inMemberUuid);
}
