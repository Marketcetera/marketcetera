package org.marketcetera.admin.service;

import java.util.List;
import java.util.Set;

import org.marketcetera.admin.NotAuthorizedException;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.SupervisorPermission;
import org.marketcetera.admin.User;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;

/* $License$ */

/**
 * Provides services related to users and permissions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AuthorizationService.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public interface AuthorizationService
{
    /**
     * Saves the given <code>Permission</code>.
     *
     * @param inPermission a <code>Permission</code> value
     * @return a <code>Permission</code> value
     */
    Permission save(Permission inPermission);
    /**
     * Finds the <code>Permission</code> with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>Permission</code> value or <code>null</code>
     */
    Permission findPermissionByName(String inName);
    /**
     * Finds the <code>Role</code> with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>Role</code> value or <code>null</code>
     */
    Role findRoleByName(String inName);
    /**
     * Saves the given <code>Role</code>.
     *
     * @param inRole a <code>Role</code> value
     * @return a <code>Role</code> value
     */
    Role save(Role inRole);
    /**
     * Find all <code>Role</code> values.
     *
     * @return a <code>List&lt;Role&gt;</code> value
     */
    List<Role> findAllRoles();
    /**
     * Find a page of <code>Role</code> values.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;Role&gt;</code> value
     */
    CollectionPageResponse<Role> findAllRoles(PageRequest inPageRequest);
    /**
     * Delete the role with the given name.
     *
     * @param inRoleName a <code>String</code> value
     */
    void deleteRole(String inRoleName);
    /**
     * Validate that the user with the given username has been granted the permission with the given permission name.
     *
     * @param inUsername a <code>String</code> value
     * @param inPermissionName a <code>String</code> value
     * @throws IllegalArgumentException if the user or permission name is invalid
     * @throws NotAuthorizedException if the user and permission are valid but the user has not been granted the permission
     */
    void authorize(String inUsername,
                   String inPermissionName);
    /**
     * Validate that the user with the given username has been granted the permission with the given permission name.
     * 
     * <p>This method will return a boolean value indicating if the permission is granted or not.
     *
     * @param inUsername a <code>String</code> value
     * @param inPermissionName a <code>String</code> value
     * @return a <code>boolean</code> value
     * @throws IllegalArgumentException if the user or permission name is invalid
     */
    boolean authorizeNoException(String inUsername,
                                 String inPermissionName);
    /**
     * Get the users that have the given supervisor permission over the given user.
     *
     * @param inUsername a <code>String</code> value
     * @param inPermissionName a <code>String</code> value
     * @return a <code>Set&lt;User&gt;</code> value
     * @throws IllegalArgumentException if the user or permission name is invalid
     */
    Set<User> getSupervisorsFor(String inUsername,
                                String inPermissionName);
    /**
     * Finds all <code>Permission</code> values granted to the user with the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>Set&lt;Permissions&gt;</code> value
     */
    Set<Permission> findAllPermissionsByUsername(String inUsername);
    /**
     * Find all <code>Permission</code> values.
     *
     * @return a <code>List&lt;Permission&gt;</code> value
     */
    List<Permission> findAllPermissions();
    /**
     * Find some <code>Permission</code> values.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;Permission&gt;</code> value
     */
    CollectionPageResponse<Permission> findAllPermissions(PageRequest inPageRequest);
    /**
     * Delete the <code>Permission</code> with the given name.
     *
     * @param inPermissionName a <code>String</code> value
     */
    void deletePermission(String inPermissionName);
    /**
     * Find the given supervisor permission by name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>SupervisorPermission</code> or <code>null</code>
     */
    SupervisorPermission findSupervisorPermissionByName(String inName);
    /**
     * Save the given supervisor permission value.
     *
     * @param inSupervisorPermission a <code>SupervisorPermission</code> value
     * @return a <code>SupervisorPermission</code> value
     */
    SupervisorPermission save(SupervisorPermission inSupervisorPermission);
    /**
     * Get the users over which the given user has the given permission.
     *
     * @param inSupervisorUser a <code>User</code> value
     * @param inPermissionName a <code>String</code> value
     * @return a <code>Set&lt;User&gt;</code> value
     */
    Set<User> getSubjectUsersFor(User inSupervisorUser,
                                 String inPermissionName);
}
