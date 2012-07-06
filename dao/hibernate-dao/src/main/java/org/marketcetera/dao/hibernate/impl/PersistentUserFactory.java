package org.marketcetera.dao.hibernate.impl;

import org.marketcetera.core.systemmodel.User;
import org.marketcetera.core.systemmodel.UserFactory;
import org.marketcetera.core.attributes.ClassVersion;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates persistable {@link User} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentUserFactory.java 82320 2012-04-02 17:03:23Z colin $
 * @since $Release$
 */
@Component
@ClassVersion("$Id: PersistentUserFactory.java 82320 2012-04-02 17:03:23Z colin $")
public class PersistentUserFactory
        implements UserFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.UserFactory#create(java.lang.String, java.lang.String)
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
     * @see org.marketcetera.core.systemmodel.UserFactory#create()
     */
    @Override
    public User create()
    {
        return new PersistentUser();
    }
}
