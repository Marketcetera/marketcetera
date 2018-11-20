package org.marketcetera.cluster;

import java.io.Serializable;

/* $License$ */

/**
 * Describes a cluster work unit.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.5.0
 */
public interface ClusterWorkUnitSpec
        extends Serializable,Comparable<ClusterWorkUnitSpec>
{
    /**
     * Get the workUnitId value.
     *
     * @return a <code>String</code> value
     */
    String getWorkUnitId();
    /**
     * Get the workUnitType value.
     *
     * @return a <code>ClusterWorkUnitType</code> value
     */
    ClusterWorkUnitType getWorkUnitType();
    /**
     * Get the workUnitUid value.
     *
     * @return a <code>String</code> value
     */
    String getWorkUnitUid();
    /**
     * indicates the master UID
     */
    public static final String MASTER_UID = "MASTER";
}
