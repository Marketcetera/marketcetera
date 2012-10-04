package org.marketcetera.api.dao;

import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Creates <code>User</code> objects.
 *
 * @version $Id$
 * @since $Release$
 */
public interface UserFactory
{
    /**
     * Creates a <code>User</code> object with the given attributes.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>User</code> value
     */
    public User create(String inUsername,
                       String inPassword);
    /**
     * Creates a <code>User</code> object.
     *
     * @return a <code>User</code> value
     */
    public User create();
    /**
     * Creates a <code>User</code> object with the same attributes as the given <code>User</code>.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>User</code> value
     */
    public User create(User inUser);
}
