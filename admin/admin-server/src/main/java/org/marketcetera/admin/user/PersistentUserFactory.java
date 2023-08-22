package org.marketcetera.admin.user;

import org.marketcetera.admin.MutableUserFactory;
import org.marketcetera.admin.User;

/* $License$ */

/**
 * Creates {@link User} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentUserFactory
        implements MutableUserFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.Factory#create(java.lang.Object)
     */
    @Override
    public PersistentUser create(User inUser)
    {
        return create(inUser.getName(),
                      inUser.getHashedPassword(),
                      inUser.getDescription(),
                      inUser.isActive());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserFactory#create(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public PersistentUser create(String inName,
                                 String inHashedPassword,
                                 String inDescription,
                                 boolean inIsActive)
    {
        PersistentUser user = new PersistentUser();
        user.setName(inName);
        user.setHashedPassword(inHashedPassword);
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
