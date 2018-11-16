package org.marketcetera.ors.security;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;

/* $License$ */

/**
 * Creates {@link User} objects.
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
    public SimpleUser create(String inName,
                             String inPassword,
                             String inDescription,
                             boolean inIsActive)
    {
        SimpleUser user = new SimpleUser();
        user.setName(inName);
        user.setPassword(inPassword.toCharArray());
        user.setDescription(inDescription);
        user.setActive(inIsActive);
        user.setSuperuser(false);
        return user;
    }
}
