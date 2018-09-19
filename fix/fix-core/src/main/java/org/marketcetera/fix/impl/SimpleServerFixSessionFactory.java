package org.marketcetera.fix.impl;

import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.fix.ServerFixSessionFactory;

/* $License$ */

/**
 * Creates new {@link SimpleServerFixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleServerFixSessionFactory
        implements ServerFixSessionFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.DomainObjectFactory#create(org.marketcetera.core.DomainObject)
     */
    @Override
    public ServerFixSession create(ServerFixSession inServerFixSession)
    {
        return new SimpleServerFixSession(inServerFixSession);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSessionFactory#create(org.marketcetera.fix.ActiveFixSession, org.marketcetera.brokers.SessionCustomization)
     */
    @Override
    public ServerFixSession create(ActiveFixSession inActiveFixSession,
                                   SessionCustomization inSessionCustomization)
    {
        return new SimpleServerFixSession(inActiveFixSession,
                                          inSessionCustomization);
    }
}
