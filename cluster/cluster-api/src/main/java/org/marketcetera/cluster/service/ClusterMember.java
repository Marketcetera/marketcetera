package org.marketcetera.cluster.service;

import org.marketcetera.core.DomainObject;

/* $License$ */

/**
 * Identifies a member of the cluster.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterMember
        extends DomainObject
{
    /**
     * Get the uuid for the cluster member.
     *
     * @return a <code>String</code> value
     */
    String getUuid();
}
