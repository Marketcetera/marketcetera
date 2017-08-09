package org.marketcetera.admin;

/* $License$ */

/**
 * Create <code>User</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface UserFactory
{
    /**
     * Create user objects.
     *
     * @param inName a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @param inIsActive a <code>boolean</code> value
     * @return a <code>User</code> value
     */
    User create(String inName,
                String inPassword,
                String inDescription,
                boolean inIsActive);
}
