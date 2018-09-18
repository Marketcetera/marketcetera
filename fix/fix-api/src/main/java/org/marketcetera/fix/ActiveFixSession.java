package org.marketcetera.fix;

import org.marketcetera.cluster.ClusterData;
import org.marketcetera.core.DomainObject;
import org.marketcetera.core.HasMutableView;

/* $License$ */

/**
 * Provides information about an active FIX Session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ActiveFixSession
        extends DomainObject,HasMutableView<MutableActiveFixSession>
{
    /**
     * Get the next target sequence number.
     *
     * @return an <code>int</code> value
     */
    int getTargetSequenceNumber();
    /**
     * Get the next sender sequence number.
     *
     * @return an <code>int</code> value
     */
    int getSenderSequenceNumber();
    /**
     * Get the session status.
     *
     * @return a <code>FixSessionStatus</code> value
     */
    FixSessionStatus getStatus();
    /**
     * Get the cluster data value.
     *
     * @return a <code>ClusterData</code> value
     */
    ClusterData getClusterData();
    /**
     * Get the FIX session value.
     *
     * @return a <code>FixSession</code> value
     */
    FixSession getFixSession();
}
