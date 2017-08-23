package org.marketcetera.admin.user;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates {@link User} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class PersistentUserFactory
        implements UserFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserFactory#create(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public PersistentUser create(String inName,
                                 String inPassword,
                                 String inDescription,
                                 boolean inIsActive)
    {
        PersistentUser user = new PersistentUser();
        user.setName(inName);
        user.setPassword(inPassword.toCharArray());
        user.setDescription(inDescription);
        user.setActive(inIsActive);
        user.setSuperuser(false);
        return user;
    }
}
