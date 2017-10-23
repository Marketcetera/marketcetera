package org.marketcetera.admin.user;

import org.marketcetera.admin.MutableUserFactory;
import org.marketcetera.admin.User;
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
        implements MutableUserFactory
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
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableUserFactory#create()
     */
    @Override
    public PersistentUser create()
    {
        return new PersistentUser();
    }
}
