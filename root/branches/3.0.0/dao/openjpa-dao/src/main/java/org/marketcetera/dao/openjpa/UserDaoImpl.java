package org.marketcetera.dao.openjpa;

import java.util.List;

import org.marketcetera.dao.UserDao;
import org.marketcetera.systemmodel.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:40 AM
 */

public class UserDaoImpl implements UserDao {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);


    @Override
    public User getByName(String inUsername) {
        log.trace("Entering getByName");
        return null;
    }

    @Override
    public void add(User inData) {
        log.trace("Entering add");

    }

    @Override
    public void save(User inData) {
        log.trace("Entering save");

    }

    @Override
    public void delete(User inData) {
        log.trace("Entering delete");

    }

    @Override
    public User getById(long inId) {
        log.trace("Entering getById");
        return null;
    }

    @Override
    public List<User> getAll() {
        log.trace("Entering getAll");
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.trace("Entering loadUserByUsername");
        return null;
    }
}
