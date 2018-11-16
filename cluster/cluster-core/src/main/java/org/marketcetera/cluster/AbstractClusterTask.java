package org.marketcetera.cluster;

import org.marketcetera.cluster.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Provides common behavior for cluster tasks.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractClusterTask.java 17134 2017-01-27 16:41:54Z colin $
 * @since 2.5.0
 */
public abstract class AbstractClusterTask
        implements ClusterTask
{
    /**
     * Gets the executor pool to use for this task.
     *
     * @return a <code>String</code> value
     */
    public String getPoolName()
    {
        return DEFAULT_POOL_NAME;
    }
    /**
     * Get the clusterService value.
     *
     * @return a <code>ClusterService</code> value
     */
    public ClusterService getClusterService()
    {
        return clusterService;
    }
    /**
     * Sets the clusterService value.
     *
     * @param inClusterService a <code>ClusterService</code> value
     */
    public void setClusterService(ClusterService inClusterService)
    {
        clusterService = inClusterService;
    }
    /**
     * Get the requiredWorkUnitId value.
     *
     * @return a <code>String</code> value
     */
    public String getRequiredWorkUnitId()
    {
        return requiredWorkUnitId;
    }
    /**
     * Sets the requiredWorkUnitId value.
     *
     * @param inRequiredWorkUnitId a <code>String</code> value
     */
    public void setRequiredWorkUnitId(String inRequiredWorkUnitId)
    {
        requiredWorkUnitId = inRequiredWorkUnitId;
    }
    /**
     * optional work unit spec id that a cluster member must have to execute this task
     */
    private String requiredWorkUnitId;
    /**
     * provides access to cluster services
     */
    @Autowired
    private transient ClusterService clusterService;
    /**
     * indicates the default pool name to use
     */
    protected static final String DEFAULT_POOL_NAME = "default";
    private static final long serialVersionUID = -3064326853064407278L;
}
