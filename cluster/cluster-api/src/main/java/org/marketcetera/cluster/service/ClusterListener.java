package org.marketcetera.cluster.service;

/* $License$ */

/**
 * Listens for changes to the cluster membership or state.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterListener
{
    /**
     * Indicate that the given cluster member has joined the cluster.
     *
     * @param inAddedMember a <code>ClusterMember</code> value
     */
    void memberAdded(ClusterMember inAddedMember);
    /**
     * Indicate that the given cluster member has been removed from the cluster.
     *
     * @param inRemovedMember a <code>ClusterMember</code> value
     */
    void memberRemoved(ClusterMember inRemovedMember);
    /**
     * Indicate that the given cluster member has changed.
     *
     * @param inChangedMember a <code>ClusterMember</code> value
     */
    void memberChanged(ClusterMember inChangedMember);
}
