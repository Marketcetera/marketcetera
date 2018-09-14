package org.marketcetera.fix.impl;

import org.marketcetera.fix.ActiveFixSession;
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
}
