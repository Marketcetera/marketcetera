package org.marketcetera.dao.impl;

import org.marketcetera.dao.AuthorityDao;
import org.marketcetera.dao.DataAccessService;
import org.marketcetera.dao.GroupDao;
import org.marketcetera.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:39 AM
 */

public class DataAccessServiceImpl implements DataAccessService {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DataAccessServiceImpl.class);
    private UserDao userDao;
    private AuthorityDao authorityDao;
    private GroupDao groupDao;


    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public AuthorityDao getAuthorityDao() {
        return authorityDao;
    }

    @Override
    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setAuthorityDao(AuthorityDao authorityDao) {
        this.authorityDao = authorityDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }
}
