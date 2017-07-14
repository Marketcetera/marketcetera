package com.marketcetera.matp.cluster;

import java.io.Serializable;

/* $License$ */

/**
 * Describes this cluster instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterData
        extends Serializable
{
    /**
     * Gets the instance number.
     *
     * @return an <code>int</code> value
     */
    int getInstanceNumber();
    /**
     * Gets the host number.
     *
     * @return an <code>int</code> value
     */
    int getHostNumber();
    /**
     * Gets the system-assigned host id.
     *
     * @return a <code>String</code> value
     */
    String getHostId();
    /**
     * Gets the total number of instances.
     *
     * @return an <code>int</code> value
     */
    int getTotalInstances();
    /**
     * Get the unique identifier for this cluster member.
     *
     * @return a <code>String</code> value
     */
    String getUuid();
}
