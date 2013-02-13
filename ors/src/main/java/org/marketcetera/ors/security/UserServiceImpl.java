package org.marketcetera.ors.security;

import org.marketcetera.persist.AbstractNDEntityService;
import org.springframework.stereotype.Component;

import com.mysema.query.types.path.EntityPathBase;

/* $License$ */

/**
 * Provides services to manage <code>User</code> objects.
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
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#getBaseType()
     */
    @Override
    protected EntityPathBase<User> getBaseType()
    {
        return QUser.user;
    }
}
