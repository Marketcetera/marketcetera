package org.marketcetera.admin;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.rpc.BaseClient;



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
     * Create the given FIX session.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     */
    FixSession createFixSession(FixSession inFixSession);
    /**
     * Get current FIX sessions with their status.
     *
     * @return a <code>List&lt;ActiveFixSession&gt;</code> value
     */
    List<ActiveFixSession> readFixSessions();
    /**
     * Read a page of FIX sessions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;ActiveFixSession&gt;</code> value
     */
    CollectionPageResponse<ActiveFixSession> readFixSessions(PageRequest inPageRequest);
    /**
     * Update the given FIX session with the given original name.
     *
     * @param inIncomingName a <code>String</code> value
     * @param inFixSession a <code>FixSession</code> value
     */
    void updateFixSession(String inIncomingName,
                          FixSession inFixSession);
    /**
     * Enable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void enableFixSession(String inName);
    /**
     * Disable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void disableFixSession(String inName);
    /**
     * Delete the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void deleteFixSession(String inName);
    /**
     * Stop the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void stopFixSession(String inName);
    /**
     * Start the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void startFixSession(String inName);
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
     * Get the instance data for the given affinity.
     *
     * @param inAffinity an <code>int</code> value
     * @return an <code>InstanceData</code> value
     */
    InstanceData getInstanceData(int inAffinity);
    /**
     * Get the FIX session attribute descriptors.
     *
     * @return a <code>Collection&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors();
    /**
     * Update sender and target sequence numbers for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    void updateSequenceNumbers(String inSessionName,
                               int inSenderSequenceNumber,
                               int inTargetSequenceNumber);
    /**
     * Update the sender sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     */
    void updateSenderSequenceNumber(String inSessionName,
                                    int inSenderSequenceNumber);
    /**
     * Update the target sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    void updateTargetSequenceNumber(String inSessionName,
                                    int inTargetSequenceNumber);
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
     * @param inAttribute a <code>String</code> value
     */
    void setUserAttribute(String inUsername,
                          UserAttributeType inAttributeType,
                          String inAttribute);
}
