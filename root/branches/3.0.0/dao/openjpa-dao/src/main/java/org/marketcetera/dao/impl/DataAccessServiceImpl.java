package org.marketcetera.dao.impl;

import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.dao.DataAccessService;
import org.marketcetera.api.dao.UserDao;
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
    private PermissionDao permissionDao;
    private RoleDao roleDao;


    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public PermissionDao getPermissionDao() {
        return permissionDao;
    }

    @Override
    public RoleDao getRoleDao() {
        return roleDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
}
