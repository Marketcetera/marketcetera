package org.marketcetera.fix.impl;

import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.MutableActiveFixSession;
import org.marketcetera.fix.MutableActiveFixSessionFactory;

/* $License$ */

/**
 * Creates {@link MutableActiveFixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleActiveFixSessionFactory
        implements MutableActiveFixSessionFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSessionFactory#create(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public MutableActiveFixSession create(ActiveFixSession inFixSession)
    {
        return new SimpleActiveFixSession(inFixSession);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSessionFactory#create()
     */
    @Override
    public MutableActiveFixSession create()
    {
        return new SimpleActiveFixSession();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSessionFactory#create(org.marketcetera.fix.FixSession, org.marketcetera.cluster.ClusterData, org.marketcetera.fix.FixSessionStatus, org.marketcetera.brokers.SessionCustomization)
     */
    @Override
    public MutableActiveFixSession create(FixSession inUnderlyingFixSession,
                                          ClusterData inInstanceData,
                                          FixSessionStatus inBrokerStatus,
                                          SessionCustomization inSessionCustomization)
    {
        return new SimpleActiveFixSession(inUnderlyingFixSession,
                                          inInstanceData,
                                          inBrokerStatus,
                                          inSessionCustomization);
    }
}
