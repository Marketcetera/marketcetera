package org.marketcetera.fix;

import java.util.Set;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.core.MutableDomainObject;

/* $License$ */

/**
 * Provides a mutable {@link ActiveFixSession} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableActiveFixSession
        extends MutableDomainObject<ActiveFixSession>,ActiveFixSession
{
    /**
     * Set the next target sequence number.
     *
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    void setTargetSequenceNumber(int inTargetSequenceNumber);
    /**
     * Set the next sender sequence number.
     *
     * @param inSenderSequenceNumber an <code>int</code> value
     */
    void setSenderSequenceNumber(int inSenderSequenceNumber);
    /**
     * Set the session status.
     *
     * @param inFixSessionStatus a <code>FixSessionStatus</code> value
     */
    void setStatus(FixSessionStatus inFixSessionStatus);
    /**
     * Set the FIX session value.
     *
     * @param inFixSession a <code>FixSession</code> value
     */
    void setFixSession(FixSession inFixSession);
    /**
     * Get the cluster data value.
     *
     * @param inClusterData a <code>ClusterData</code> value
     */
    void setClusterData(ClusterData inClusterData);
    /**
     * Set the brokerAlgos value.
     *
     * @param inBrokerAlgoSpecs a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    void setBrokerAlgos(Set<BrokerAlgoSpec> inBrokerAlgoSpecs);
}
