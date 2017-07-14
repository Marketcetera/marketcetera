package com.marketcetera.admin.impl;

import com.marketcetera.admin.User;
import com.marketcetera.admin.UserFactory;

/* $License$ */

/**
 * Creates simple user objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleUserFactory
        implements UserFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserFactory#create(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public User create(String inName,
                       String inPassword,
                       String inDescription,
                       boolean inIsActive)
    {
        return new SimpleUser(inName,
                              inDescription,
                              inPassword,
                              inIsActive);
    }
}
