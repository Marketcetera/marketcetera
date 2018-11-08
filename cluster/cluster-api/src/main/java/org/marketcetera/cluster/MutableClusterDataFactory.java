package org.marketcetera.cluster;

/* $License$ */

/**
 * Creates {@link MutableClusterData} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableClusterDataFactory
        extends ClusterDataFactory
{
    /**
     * Create a new {@link MutableClusterData} object.
     *
     * @return a <code>MutableClusterData</code> value
     */
    MutableClusterData create();
    /**
     * Create a new {@link MutableClusterData} object.
     *
     * @param inClusterData a <code>ClusterData</code> value
     * @return a <code>MutableClusterData</code> value
     */
    MutableClusterData create(ClusterData inClusterData);
}
