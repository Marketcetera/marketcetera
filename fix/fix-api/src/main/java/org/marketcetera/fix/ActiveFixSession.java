package org.marketcetera.fix;

import java.util.Set;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.cluster.HasClusterData;
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
        extends DomainObject,HasMutableView<MutableActiveFixSession>,HasClusterData
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
     * Get the FIX session value.
     *
     * @return a <code>FixSession</code> value
     */
    FixSession getFixSession();
    /**
     * Get the brokerAlgos value.
     *
     * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    Set<BrokerAlgoSpec> getBrokerAlgos();
}
