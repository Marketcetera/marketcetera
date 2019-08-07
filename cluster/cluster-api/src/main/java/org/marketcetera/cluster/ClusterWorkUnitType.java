package org.marketcetera.cluster;

/* $License$ */

/**
 * Indicates the type of work unit in a cluster.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.5.0
 */
public enum ClusterWorkUnitType
{
    /**
     * one and only one of these can exist in all members of the cluster
     */
    SINGLETON,
    /**
     * same as {@link SINGLETON}, but the uniqueness of this work unit is defined at runtime
     */
    SINGLETON_RUNTIME,
    /**
     * more than one of these can exist among all members of the cluster
     */
    REPLICATED;
}
