package org.marketcetera.dao;

/* $License$ */

import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.api.dao.UserDao;

/**
 * Provides access to database services.
 *
 * @version $Id: DataAccessService.java 82315 2012-03-17 01:58:54Z colin $
 * @since $Release$
 */
public interface DataAccessService
{
    /**
     * Gets the <code>UserDao</code> value.
     *
     * @return a <code>UserDao</code> value
     */
    public UserDao getUserDao();
    /**
     * Gets the <code>PermissionDao</code> value.
     *
     * @return an <code>PermissionDao</code> value
     */
    public PermissionDao getPermissionDao();
    /**
     * Gets the <code>RoleDao</code> value.
     *
     * @return a <code>Role</code> value
     */
    public RoleDao getRoleDao();
}
