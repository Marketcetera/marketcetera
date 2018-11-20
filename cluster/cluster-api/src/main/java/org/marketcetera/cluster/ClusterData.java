package org.marketcetera.cluster;

import java.io.Serializable;

import org.marketcetera.core.DomainObject;
import org.marketcetera.core.HasMutableView;

/* $License$ */

/**
 * Describes this cluster instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClusterData
        extends DomainObject,HasMutableView<MutableClusterData>,Serializable
{
    /**
     * Get the instanceNumber value.
     *
     * @return an <code>int</code> value
     */
    int getInstanceNumber();
    /**
     * Get the hostNumber value.
     *
     * @return an <code>int</code> value
     */
    int getHostNumber();
    /**
     * Get the hostId value.
     *
     * @return a <code>String</code> value
     */
    String getHostId();
    /**
     * Get the totalInstances value.
     *
     * @return an <code>int</code> value
     */
    int getTotalInstances();
    /**
     * Get the uuid value.
     *
     * @return a <code>String</code> value
     */
    String getUuid();
}
