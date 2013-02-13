package org.marketcetera.ors.security;

import org.marketcetera.persist.NDEntityService;
import org.springframework.data.repository.NoRepositoryBean;

/* $License$ */

/**
 * Provides services for managing <code>User</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NoRepositoryBean
public interface UserService
        extends NDEntityService<User>
{
}
