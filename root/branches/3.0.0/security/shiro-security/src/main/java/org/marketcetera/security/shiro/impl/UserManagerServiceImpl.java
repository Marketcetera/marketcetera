package org.marketcetera.security.shiro.impl;

import java.util.List;

import org.marketcetera.api.security.User;
import org.marketcetera.api.security.UserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/21/12 1:32 AM
 */

public class UserManagerServiceImpl implements UserManagerService {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UserManagerServiceImpl.class);


    @Override
    public User getUserByName(String inUsername) {
        log.trace("Entering getUserByName");
        return null;
    }

    @Override
    public void addUser(User inData) {
        log.trace("Entering addUser");

    }

    @Override
    public void saveUser(User inData) {
        log.trace("Entering saveUser");

    }

    @Override
    public void deleteUser(User inData) {
        log.trace("Entering deleteUser");

    }

    @Override
    public User getUserById(long inId) {
        log.trace("Entering getUserById");
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        log.trace("Entering getAllUsers");
        return null;
    }
}
