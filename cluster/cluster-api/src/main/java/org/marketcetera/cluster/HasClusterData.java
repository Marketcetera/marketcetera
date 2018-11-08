package org.marketcetera.cluster;

/* $License$ */

/**
 * Indicates that the implementing object has <code>ClusterData</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasClusterData
{
    /**
     * Gets the cluster data value.
     *
     * @return a <code>ClusterData</code> value
     */
    ClusterData getClusterData();
}
