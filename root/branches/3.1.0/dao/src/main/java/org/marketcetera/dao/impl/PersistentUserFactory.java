package org.marketcetera.dao.impl;

import org.marketcetera.systemmodel.User;
import org.marketcetera.systemmodel.UserFactory;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates persistable {@link User} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@ClassVersion("$Id$")
public class PersistentUserFactory
        implements UserFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.UserFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public User create(String inUsername,
                       String inPassword)
    {
        PersistentUser user = new PersistentUser();
        user.setUsername(inUsername);
        user.setPassword(inPassword);
        return user;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.UserFactory#create()
     */
    @Override
    public User create()
    {
        return new PersistentUser();
    }
}
