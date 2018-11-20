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
public interface ClusterWorkUnitDescriptor
        extends Serializable
{
    /**
     * Get the workUnitSpec value.
     *
     * @return a <code>ClusterWorkUnitSpec</code> value
     */
    ClusterWorkUnitSpec getWorkUnitSpec();
    /**
     * Get the workUnitMember value.
     *
     * @return a <code>String</code> value
     */
    String getWorkUnitMember();
    /**
     * Get the timestamp value.
     *
     * @return a <code>long</code> value
     */
    long getTimestamp();
}
