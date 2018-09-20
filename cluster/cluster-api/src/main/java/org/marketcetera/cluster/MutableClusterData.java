package org.marketcetera.cluster;

import org.marketcetera.core.MutableDomainObject;

/* $License$ */

/**
 * Provides a mutable {@link ClusterData} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableClusterData
        extends ClusterData,MutableDomainObject<ClusterData>
{
    /**
     * Set the instanceNumber value.
     *
     * @param inInstanceNumber an <code>int</code> value
     */
    void setInstanceNumber(int inInstanceNumber);
    /**
     * Set the hostNumber value.
     *
     * @param inHostNumber an <code>int</code> value
     */
    void setHostNumber(int inHostNumber);
    /**
     * Set the hostId value.
     *
     * @param inHostId a <code>String</code> value
     */
    void setHostId(String inHostId);
    /**
     * Set the totalInstances value.
     *
     * @param inTotalInstances an <code>int</code> value
     */
    void setTotalInstances(int inTotalInstances);
    /**
     * Sets the uuid value.
     *
     * @param inUuid a <code>String</code> value
     */
    void setUuid(String inUuid);
}
