package org.marketcetera.cluster;

import java.io.Serializable;

import org.marketcetera.cluster.service.ClusterService;

/* $License$ */

/**
 * Provides common behavior for cluster tasks.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractClusterTask.java 17134 2017-01-27 16:41:54Z colin $
 * @since 2.5.0
 */
public interface ClusterTask
        extends Serializable
{
    /**
     * Gets the executor pool to use for this task.
     *
     * @return a <code>String</code> value
     */
    String getPoolName();
    /**
     * Get the clusterService value.
     *
     * @return a <code>ClusterService</code> value
     */
    ClusterService getClusterService();
    /**
     * Set the clusterService value.
     *
     * @param inClusterService a <code>ClusterService</code> value
     */
    void setClusterService(ClusterService inClusterService);
    /**
     * Get the requiredWorkUnitId value.
     *
     * @return a <code>String</code> value
     */
    String getRequiredWorkUnitId();
    /**
     * Set the requiredWorkUnitId value.
     *
     * @param inRequiredWorkUnitId a <code>String</code> value
     */
    void setRequiredWorkUnitId(String inRequiredWorkUnitId);
}
