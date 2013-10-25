package org.marketcetera.ors.dao.impl;

import org.marketcetera.ors.dao.UserDao;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.security.SimpleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
@Transactional(readOnly=true)
public class UserServiceImpl
        implements UserService
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#findByName(java.lang.String)
     */
    @Override
    public SimpleUser findByName(String inUsername)
    {
        return userDao.findByName(inUsername);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#updateUserDataByName(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly=false)
    public void updateUserDataByName(String inUsername,
                                     String inUserData)
    {
        userDao.updateUserByName(inUsername,
                                 inUserData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#updateUserActiveStatus(java.lang.String, boolean)
     */
    @Override
    @Transactional(readOnly=false)
    public SimpleUser updateUserActiveStatus(String inUsername,
                                             boolean inIsActive)
    {
        return userDao.updateUserActiveStatus(inUsername,
                                              inIsActive);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#updateSuperUser(java.lang.String, boolean)
     */
    @Override
    @Transactional(readOnly=false)
    public void updateSuperUser(String inUsername,
                                boolean inIsSuperuser)
    {
        userDao.updateSuperUser(inUsername,
                                inIsSuperuser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#save(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false)
    public SimpleUser save(SimpleUser inUser)
    {
        return userDao.save(inUser);
    }
    /**
     * Get the userDao value.
     *
     * @return a <code>SimpleUserRepository</code> value
     */
    public UserDao getUserDao()
    {
        return userDao;
    }
    /**
     * Sets the userDao value.
     *
     * @param inUserDao a <code>SimpleUserRepository</code> value
     */
    public void setUserDao(UserDao inUserDao)
    {
        userDao = inUserDao;
    }
    /**
     * provides datastore access to user objects
     */
    @Autowired
    private UserDao userDao;
}
