package org.marketcetera.server.service.impl;

import java.util.List;

import org.marketcetera.server.service.UserManager;
import org.marketcetera.systemmodel.User;
import org.marketcetera.systemmodel.persistence.UserDao;
import org.marketcetera.util.misc.ClassVersion;
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
@ClassVersion("$Id$")
class UserManagerImpl
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
     * @see org.marketcetera.server.service.UserManager#write(org.marketcetera.systemmodel.UserImpl)
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void write(User inUserImpl)
    {
        userDao.write(inUserImpl);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.UserManager#getByName(java.lang.String)
     */
    @Override
    @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    public User getByName(String inUsername)
    {
        return userDao.getByName(inUsername);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.UserManager#getById(long)
     */
    @Override
    @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    public User getById(long inUserID)
    {
        return userDao.getById(inUserID);
    }
    /**
     * data access object to use for access to persistent users
     */
    @Autowired
    private UserDao userDao;
}
