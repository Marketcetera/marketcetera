package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates <code>User</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserFactory.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: UserFactory.java 82316 2012-03-21 21:13:27Z colin $")
public interface UserFactory
{
    /**
     * Creates a <code>User</code> object with the given attributes.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>User</code> value
     */
    public User create(String inUsername,
                       String inPassword);
    /**
     * Creates a <code>User</code> object.
     *
     * @return a <code>User</code> value
     */
    public User create();
}
