package org.marketcetera.dao.impl;

import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.dao.DataAccessService;

/**
 * @version $Id$
 * @date 6/29/12 12:39 AM
 */

public class DataAccessServiceImpl implements DataAccessService {
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
