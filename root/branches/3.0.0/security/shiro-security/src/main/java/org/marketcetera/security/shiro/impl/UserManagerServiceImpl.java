package org.marketcetera.security.shiro.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.security.User;
import org.marketcetera.api.security.UserManagerService;
import org.marketcetera.api.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides user manager services integrated with Shiro security.
 * 
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/21/12 1:32 AM
 */
public class UserManagerServiceImpl implements UserManagerService {
    private static final Logger log = LoggerFactory.getLogger(UserManagerServiceImpl.class);
    @Override
    public User getUserByName(String inUsername) {
        log.trace("Entering getUserByName");
        inUsername = StringUtils.trimToNull(inUsername);
        Validate.notNull(inUsername);
        return userDao.getByName(inUsername);
    }

    @Override
    public void addUser(User inData) {
        log.trace("Entering addUser");
        Validate.notNull(inData);
        userDao.add(inData);
    }

    @Override
    public void saveUser(User inData) {
        log.trace("Entering saveUser");
        Validate.notNull(inData);
        userDao.save(inData);
    }

    @Override
    public void deleteUser(User inData) {
        log.trace("Entering deleteUser");
        Validate.notNull(inData);
        userDao.delete(inData);
    }

    @Override
    public User getUserById(long inId) {
        log.trace("Entering getUserById");
        return userDao.getById(inId);
    }

    @Override
    public List<User> getAllUsers() {
        log.trace("Entering getAllUsers");
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
