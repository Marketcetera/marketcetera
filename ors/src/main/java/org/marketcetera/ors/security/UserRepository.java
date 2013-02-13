package org.marketcetera.ors.security;

import org.marketcetera.persist.NDEntityRepository;

/* $License$ */

/**
 * Provides datastore access to <code>User</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface UserRepository
        extends NDEntityRepository<User>
{
}
