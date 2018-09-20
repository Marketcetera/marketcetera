package org.marketcetera.cluster.service;

import java.io.Serializable;
import java.util.SortedSet;

import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterWorkUnitDescriptor;

/**
 * Holds data about each cluster member.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HazelcastClusterService.java 16827 2016-05-24 14:40:08Z colin $
 * @since $Release$
 */
public interface ClusterMetaData
        extends Serializable
{
    /**
     * Get the clusterData value.
     *
     * @return a <code>ClusterData</code> value
     */
    ClusterData getClusterData();
    /**
     * Get the activeWorkUnits value.
     *
     * @return a <code>SortedSet&lt;? extends ClusterWorkUnitDescriptor&gt;</code> value
     */
    SortedSet<? extends ClusterWorkUnitDescriptor> getActiveWorkUnits();
}
