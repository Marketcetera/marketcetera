package org.marketcetera.dao.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.api.dao.*;
import org.marketcetera.api.security.AssignToRole;
import org.marketcetera.api.security.Provisioning;
import org.marketcetera.api.security.ProvisioningManager;
import org.marketcetera.api.security.User;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides provisioning management services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ProvisioningManagerImpl
        implements ProvisioningManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.ProvisioningManagerService#provision(org.marketcetera.api.security.Provisioning)
     */
    @Override
    public void provision(Provisioning inProvisioning)
    {
        for(Permission permission : inProvisioning.getPermissions()) {
            try {
                permissionDao.add(permission);
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Error writing {}, skipping", // TODO message catalog
                                      permission);
            }
        }
        for(User user : inProvisioning.getUsers()) {
            try {
                userDao.add(user);
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Error writing {}, skipping", // TODO message catalog
                                      user);
            }
        }
        for(Role role : inProvisioning.getRoles()) {
            try {
                roleDao.add(role);
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Error writing {}, skipping", // TODO message catalog
                                      role);
            }
        }
        for(AssignToRole assignToRole : inProvisioning.getAssignments()) {
            SLF4JLoggerProxy.debug(this,
                                   "Performing assignment {}",
                                   assignToRole);
            // the elements to assign to the role are supposed to already exist, so find them
            String roleName = StringUtils.trimToNull(assignToRole.getRole());
            if(roleName == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Cannot perform assignment {} because no role name was provided, skipping", // TODO message catalog
                                      assignToRole);
                continue;
            }
            MutableRole roleToModify;
            try {
                roleToModify = roleDao.getByName(roleName);
            } catch (NoResultException e) {
                SLF4JLoggerProxy.warn(this,
                                      "Cannot perform assignment {} because no role by that name exists, skipping", // TODO message catalog
                                      assignToRole);
                continue;
            }
            // retrieve the permissions to add (note that this implementation consciously chooses to remove existing permissions/users in favor of the new list)
            Set<Permission> permissionsToAdd = new HashSet<Permission>();
            for(String permissionName : assignToRole.getPermissions()) {
                permissionName = StringUtils.trimToNull(permissionName);
                if(permissionName != null) {
                    try {
                        Permission permission = permissionDao.getByName(permissionName);
                        permissionsToAdd.add(permission);
                    } catch (NoResultException e) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot assign permission {} to role {} because no permission by that name exists", // TODO message catalog
                                              permissionName,
                                              assignToRole);
                    }
                }
            }
            // retrieve the users to add (note that this implementation consciously chooses to remove existing permissions/users in favor of the new list)
            Set<User> usersToAdd = new HashSet<User>();
            for(String username : assignToRole.getUsers()) {
                username = StringUtils.trimToNull(username);
                if(username != null) {
                    try {
                        User user = userDao.getByName(username);
                        usersToAdd.add(user);
                    } catch (NoResultException e) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot assign user {} to role {} because no user by that name exists", // TODO message catalog
                                              username,
                                              assignToRole);
                    }
                }
            }
            // tie it all together
            roleToModify.setPermissions(permissionsToAdd);
            roleToModify.setUsers(usersToAdd);
            roleDao.save(roleToModify);
        }
        SLF4JLoggerProxy.info(this,
                              "Roles are now: {}", // TODO message catalog
                              roleDao.getAll());
        SLF4JLoggerProxy.info(this,
                              "Users are now: {}", // TODO message catalog
                              userDao.getAll());
    }
    /**
     * Sets the permissionDao value.
     *
     * @param inPermissionDao a <code>PermissionDao</code> value
     */
    public void setPermissionDao(PermissionDao inPermissionDao)
    {
        permissionDao = inPermissionDao;
    }
    /**
     * Sets the roleDao value.
     *
     * @param inRoleDao a <code>RoleDao</code> value
     */
    public void setRoleDao(RoleDao inRoleDao)
    {
        roleDao = inRoleDao;
    }
    /**
     * Sets the userDao value.
     *
     * @param inUserDao a <code>UserDao</code> value
     */
    public void setUserDao(UserDao inUserDao)
    {
        userDao = inUserDao;
    }
    /**
     * provides access to the role datastore
     */
    private RoleDao roleDao;
    /**
     * provides access to the permission datastore
     */
    private PermissionDao permissionDao;
    /**
     * provides access to the user datastore
     */
    private UserDao userDao;
}
