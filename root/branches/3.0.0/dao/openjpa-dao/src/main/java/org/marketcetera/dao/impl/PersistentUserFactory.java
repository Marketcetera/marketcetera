package org.marketcetera.dao.impl;

import org.marketcetera.core.systemmodel.UserFactory;

/* $License$ */

/**
 * Creates <code>PersistentUser</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentUserFactory
        implements UserFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.UserFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public PersistentUser create(String inUsername,
                                 String inPassword)
    {
        return new PersistentUser(inUsername,
                                  inPassword);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.UserFactory#create()
     */
    @Override
    public PersistentUser create()
    {
        return new PersistentUser();
    }
}
