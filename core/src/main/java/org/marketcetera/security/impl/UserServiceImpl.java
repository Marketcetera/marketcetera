package org.marketcetera.security.impl;

import org.marketcetera.persist.AbstractNDEntityService;
import org.marketcetera.security.User;
import org.marketcetera.security.UserRepository;
import org.marketcetera.security.UserService;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class UserServiceImpl
        extends AbstractNDEntityService<User>
        implements UserService
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#getRepositoryType()
     */
    @Override
    protected Class<UserRepository> getRepositoryType()
    {
        return UserRepository.class;
    }
}
