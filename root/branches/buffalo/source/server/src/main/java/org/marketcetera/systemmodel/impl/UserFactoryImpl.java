package org.marketcetera.systemmodel.impl;

import java.util.Properties;

import org.marketcetera.systemmodel.User;
import org.marketcetera.systemmodel.UserFactory;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class UserFactoryImpl
        implements UserFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.UserFactory#create(java.lang.String, java.lang.String, java.lang.String, boolean, java.util.Properties)
     */
    @Override
    public User create(String inName,
                       String inHashedPassword,
                       String inDescription,
                       boolean inActive,
                       Properties inUserData)
    {
        return new UserImpl(System.nanoTime(),
                            inName,
                            inHashedPassword,
                            inDescription,
                            inActive,
                            inUserData);
    }
}
