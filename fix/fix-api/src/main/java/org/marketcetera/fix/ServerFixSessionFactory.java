package org.marketcetera.fix;

import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.core.DomainObjectFactory;

/* $License$ */

/**
 * Creates <code>ServerFixSession</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServerFixSessionFactory
        extends DomainObjectFactory<ServerFixSession>
{
    /**
     * Create a <code>ServerFixSession</code> object.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     * @param inSessionCustomization a <code>SessionCustomization</code> value or <code>null</code>
     * @return a <code>ServerFixSession</code> value
     */
    ServerFixSession create(ActiveFixSession inActiveFixSession,
                            SessionCustomization inSessionCustomization);
}
