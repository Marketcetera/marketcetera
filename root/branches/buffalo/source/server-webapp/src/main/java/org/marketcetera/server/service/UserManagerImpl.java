package org.marketcetera.server.service;

import java.util.List;

import org.marketcetera.systemmodel.User;
import org.marketcetera.systemmodel.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class UserManagerImpl
        implements UserManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.UserManager#getUsers()
     */
    @Override
    @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    public List<User> getUsers()
    {
        return userDao.getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.UserManager#write(org.marketcetera.systemmodel.User)
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void write(User inUser)
    {
        userDao.write(inUser);
    }
    /**
     * data access object to use for access to persistent users
     */
    @Autowired
    private UserDao userDao;
}
