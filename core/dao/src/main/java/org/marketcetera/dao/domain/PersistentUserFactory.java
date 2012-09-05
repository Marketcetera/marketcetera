package org.marketcetera.dao.domain;

import org.marketcetera.api.dao.UserFactory;

/* $License$ */

/**
 * Creates <code>PersistentUser</code> objects.
 *
 * @version $Id$
 * @since $Release$
 */
public class PersistentUserFactory
        implements UserFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.UserFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public PersistentUser create(String inUsername,
                                 String inPassword)
    {
        return new PersistentUser(inUsername,
                                  inPassword);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.UserFactory#create()
     */
    @Override
    public PersistentUser create()
    {
        return new PersistentUser();
    }
}
