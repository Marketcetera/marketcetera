package org.marketcetera.security;

import org.marketcetera.persist.NDEntityService;
import org.springframework.data.repository.NoRepositoryBean;

/* $License$ */

/**
 *
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
