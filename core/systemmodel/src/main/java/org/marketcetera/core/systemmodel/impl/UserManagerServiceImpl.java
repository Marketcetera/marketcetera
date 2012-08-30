package org.marketcetera.core.systemmodel.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.security.User;
import org.marketcetera.api.security.UserManagerService;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.api.dao.UserDao;

/**
 * Provides user manager services integrated with Shiro security.
 * 
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/21/12 1:32 AM
 */
public class UserManagerServiceImpl
        implements UserManagerService
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.UserManagerService#getUserByName(java.lang.String)
     */
    @Override
    public User getUserByName(String inUsername)
    {
        SLF4JLoggerProxy.trace(this,
                               "Entering getUserByName");
        inUsername = StringUtils.trimToNull(inUsername);
        Validate.notNull(inUsername);
        return userDao.getByName(inUsername);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.UserManagerService#addUser(org.marketcetera.api.security.User)
     */
    @Override
    public void addUser(User inData)
    {
        SLF4JLoggerProxy.trace(this,
                               "Entering addUser");
        Validate.notNull(inData);
        userDao.add(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.UserManagerService#saveUser(org.marketcetera.api.security.User)
     */
    @Override
    public void saveUser(User inData)
    {
        SLF4JLoggerProxy.trace(this,
                               "Entering saveUser");
        Validate.notNull(inData);
        userDao.save(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.UserManagerService#deleteUser(org.marketcetera.api.security.User)
     */
    @Override
    public void deleteUser(User inData)
    {
        SLF4JLoggerProxy.trace(this,
                               "Entering deleteUser");
        Validate.notNull(inData);
        userDao.delete(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.UserManagerService#getUserById(long)
     */
    @Override
    public User getUserById(long inId)
    {
        SLF4JLoggerProxy.trace(this,
                               "Entering getUserById");
        return userDao.getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.UserManagerService#getAllUsers()
     */
    @Override
    public List<User> getAllUsers()
    {
        SLF4JLoggerProxy.trace(this,
                               "Entering getAllUsers");
        return userDao.getAll();
    }
    /**
     * Sets the userDao value.
     *
     * @param a <code>UserDao</code> value
     */
    public void setUserDao(UserDao inUserDao)
    {
        userDao = inUserDao;
    }
    /**
     * provides access to user objects
     */
    private UserDao userDao;
}
