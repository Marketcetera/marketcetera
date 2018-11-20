package org.marketcetera.fix;

import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.cluster.ClusterData;

/* $License$ */

/**
 * Creates {@link MutableActiveFixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableActiveFixSessionFactory
        extends ActiveFixSessionFactory
{
    /**
     * Create a <code>MutableActiveFixSession</code> object.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>MutableActiveFixSession</code> value
     */
    @Override
    MutableActiveFixSession create(ActiveFixSession inFixSession);
    /**
     * Create a <code>MutableActiveFixSession</code> object.
     *
     * @return a <code>MutableActiveFixSession</code> value
     */
    MutableActiveFixSession create();
    /**
     * Create a <code>MutableActiveFixSession</code> object.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inClusterData a <code>ClusterData</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     * @param inSessionCustomization a <code>SessionCustomization</code> value
     * @return a <code>MutableActiveFixSession</code> value
     */
    @Override
    MutableActiveFixSession create(FixSession inUnderlyingFixSession,
                                   ClusterData inInstanceData,
                                   FixSessionStatus inBrokerStatus,
                                   SessionCustomization inSessionCustomization);
}
