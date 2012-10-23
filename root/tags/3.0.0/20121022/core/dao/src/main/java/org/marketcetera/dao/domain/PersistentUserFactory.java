package org.marketcetera.dao.domain;

import org.marketcetera.api.dao.UserFactory;
import org.marketcetera.api.security.User;

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
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.UserFactory#create(org.marketcetera.api.security.User)
     */
    @Override
    public User create(User inUser)
    {
        if(inUser instanceof PersistentUser) {
            return inUser;
        }
        return new PersistentUser(inUser);
    }
}
