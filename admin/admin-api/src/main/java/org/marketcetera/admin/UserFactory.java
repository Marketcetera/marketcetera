package org.marketcetera.admin;

import org.marketcetera.core.Factory;

/* $License$ */

/**
 * Create <code>User</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface UserFactory
        extends Factory<User>
{
    /**
     * Create user objects.
     *
     * @param inName a <code>String</code> value
     * @param inHashedPassword a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @param inIsActive a <code>boolean</code> value
     * @return a <code>User</code> value
     */
    User create(String inName,
                String inHashedPassword,
                String inDescription,
                boolean inIsActive);
}
