package org.marketcetera.admin;

import java.util.List;
import java.util.Set;

import org.marketcetera.core.BaseClient;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;


/* $License$ */

/**
 * Provides access to admin services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AdminClient
        extends BaseClient
{
    /**
     * Get permissions for the current user.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    Set<String> getPermissionsForCurrentUser();
    /**
     * Get the list of users.
     *
     * @return a <code>List&lt;User&gt;</code> value
     */
    List<User> readUsers();
    /**
     * Get a list of users.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;User&gt;</code> value
     */
    CollectionPageResponse<User> readUsers(PageRequest inPageRequest);
    /**
     * Create the given user with the given password.
     *
     * @param inNewUser a <code>User</code> value
     * @param inPassword a <code>String</code> value
     */
    User createUser(User inNewUser,
                    String inPassword);
    /**
     * Update the given user with the given current username.
     *
     * @param inUsername a <code>String</code> value
     * @param inUpdatedUser a <code>User</code> value
     * @return a <code>User</code> value
     */
    User updateUser(String inUsername,
                    User inUpdatedUser);
    /**
     * Change the password of the given user.
     *
     * @param inUsername a <code>String</code> value
     * @param inOldPassword a <code>String</code> value
     * @param inNewPassword a <code>String</code> value
     */
    void changeUserPassword(String inUsername,
                            String inOldPassword,
                            String inNewPassword);
    /**
     * Delete the given user.
     *
     * @param inUsername a <code>String</code> value
     */
    void deleteUser(String inUsername);
    /**
     * Deactivate the user with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void deactivateUser(String inName);
    /**
     * Create a permission with the given attributes.
     *
     * @param inPermission a <code>Permission</code> value
     * @return a <code>Permission</code> value
     */
    Permission createPermission(Permission inPermission);
    /**
     * Read permissions.
     *
     * @return a <code>List&lt;Permission&gt;</code> value
     */
    List<Permission> readPermissions();
    /**
     * Read a page of permissions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;Permission&gt;</code> value
     */
    CollectionPageResponse<Permission> readPermissions(PageRequest inPageRequest);
    /**
     * Update the permission with the given name.
     *
     * @param inPermissionName a <code>String</code> value
     * @param inUpdatedPermission a <code>Permission</code> value
     * @return a <code>Permission</code> value
     */
    Permission updatePermission(String inPermissionName,
                                Permission inUpdatedPermission);
    /**
     * Delete the permission with the given name.
     *
     * @param inPermissionName a <code>String</code> value
     */
    void deletePermission(String inPermissionName);
    /**
     * Create the given role.
     *
     * @param inRole a <code>Role</code> value
     * @return a <code>Role</code> value
     */
    Role createRole(Role inRole);
    /**
     * Read roles.
     *
     * @return a <code>List&lt;Role&gt</code> value
     */
    List<Role> readRoles();
    /**
     * Read a page or roles.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;Role&gt;</code> value
     */
    CollectionPageResponse<Role> readRoles(PageRequest inPageRequest);
    /**
     * Delete the role with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void deleteRole(String inName);
    /**
     * Update the given role with the given original name.
     *
     * @param inName a <code>String</code> value
     * @param inRole a <code>Role</code> value
     * @return a <code>Role</code> value
     */
    Role updateRole(String inName,
                    Role inRole);
    /**
     * Get the user attribute for the given user and attribute type.
     *
     * @param inUsername a <code>String</code> value
     * @param inAttributeType a <code>UserAttributeType</code> value
     * @return a <code>UserAttribute</code> value or <code>null</code>
     */
    UserAttribute getUserAttribute(String inUsername,
                                   UserAttributeType inAttributeType);
    /**
     * Set the given user attribute for the given user and attribute type.
     *
     * @param inUsername a <code>String</code> value
     * @param inAttributeType a <code>UserAttributeType</code> value
     * @param inAttribute a <code>String</code> value or <code>null</code> to remove the attribute
     */
    void setUserAttribute(String inUsername,
                          UserAttributeType inAttributeType,
                          String inAttribute);
}
