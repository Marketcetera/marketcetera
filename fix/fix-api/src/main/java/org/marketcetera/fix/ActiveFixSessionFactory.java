package org.marketcetera.fix;

import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.cluster.ClusterData;

/* $License$ */

/**
 * Creates {@link ActiveFixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ActiveFixSessionFactory
{
    /**
     * Create an <code>ActiveFixSession</code> object.
     *
     * @param inFixSession an <code>ActiveFixSession</code> value
     * @return an <code>ActiveFixSession</code> value
     */
    ActiveFixSession create(ActiveFixSession inFixSession);
    /**
     * Create an <code>ActiveFixSession</code> object.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inClusterData a <code>ClusterData</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     * @param inSessionCustomization a <code>SessionCustomization</code> value
     * @return an <code>ActiveFixSession</code> value
     */
    ActiveFixSession create(FixSession inUnderlyingFixSession,
                            ClusterData inInstanceData,
                            FixSessionStatus inBrokerStatus,
                            SessionCustomization inSessionCustomization);
}
