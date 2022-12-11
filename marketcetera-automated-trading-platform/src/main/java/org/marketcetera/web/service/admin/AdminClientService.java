package org.marketcetera.web.service.admin;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ConnectableService;
import org.marketcetera.web.service.ServiceManager;

/* $License$ */

/**
 * Provides access to admin services for a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminClientService
        implements ConnectableService
{
    /**
     * Get the <code>AdminClientService</code> instance for the current session.
     *
     * @return an <code>AdminClientService</code> value or <code>null</code>
     */
    public static AdminClientService getInstance()
    {
        return ServiceManager.getInstance().getService(AdminClientService.class);
    }
    /**
     * Create a new AdminClientService instance.
     */
    public AdminClientService() {}
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return adminClient != null && adminClient.isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#disconnect()
     */
    @Override
    public void disconnect()
    {
        if(adminClient != null) {
            try {
                adminClient.close();
            } catch (IOException e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        adminClient = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean connect(String inUsername,
                           String inPassword,
                           String inHostname,
                           int inPort)
            throws Exception
    {
        if(adminClient != null) {
            try {
                adminClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing admin client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                adminClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating admin client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        AdminRpcClientParameters params = new AdminRpcClientParameters();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        adminClient = adminClientFactory.create(params);
        adminClient.start();
        return adminClient.isRunning();
    }
    /**
     * Create the given user.
     *
     * @param inSubject a <code>User</code> value
     */
    public void createUser(User inSubject,
                           String inPassword)
    {
        adminClient.createUser(inSubject,
                               inPassword);
    }
    /**
     * Get the users under the aegis of the given user.
     *
     * @return a <code>Collection&lt;User&gt;</code>
     */
    public Collection<User> getUsers()
    {
        return adminClient.readUsers();
    }
    /**
     * Get the user attribute described with the given attributes.
     *
     * @param inUsername a <code>String</code> value
     * @param inAttributeType a <code>UserAttributeType</code> value
     * @return a <code>UserAttribute</code> value
     */
    public UserAttribute getUserAttribute(String inUsername,
                                          UserAttributeType inAttributeType)
    {
        return adminClient.getUserAttribute(inUsername,
                                            inAttributeType);
    }
    /**
     * Set the user attribute described with the given attributes.
     *
     * @param inUsername a <code>String</code> value
     * @param inAttributeType a <code>UserAttributeType</code> value
     * @param inAttribute a <code>String</code>value
     */
    public void setUserAttribute(String inUsername,
                                 UserAttributeType inAttributeType,
                                 String inAttribute)
    {
        adminClient.setUserAttribute(inUsername,
                                     inAttributeType,
                                     inAttribute);
    }
    /**
     * Get a page of users.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;User&gt;</code> value
     */
    public CollectionPageResponse<User> getUsers(PageRequest inPageRequest)
    {
        return adminClient.readUsers(inPageRequest);
    }
    /**
     * Update the given user with the given original name.
     *
     * @param inName a <code>String</code> value
     * @param inSubject a <code>User</code> value
     */
    public void updateUser(String inName,
                           User inSubject)
    {
        adminClient.updateUser(inName,
                               inSubject);
    }
    /**
     * Delete the user with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void deleteUser(String inName)
    {
        adminClient.deleteUser(inName);
    }
    /**
     * Deactivate the user with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void deactivateUser(String inName)
    {
        adminClient.deactivateUser(inName);
    }
    /**
     * Get roles.
     *
     * @return a <code>Collection&lt;Role&gt;</code> value
     */
    public Collection<Role> getRoles()
    {
        return adminClient.readRoles();
    }
    /**
     * Get roles.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>Collection&lt;Permission&gt;</code> value
     */
    public CollectionPageResponse<Role> getRoles(PageRequest inPageRequest)
    {
        return adminClient.readRoles(inPageRequest);
    }
    /**
     * Get permissions.
     *
     * @return a <code>Collection&lt;Permission&gt;</code> value
     */
    public Collection<Permission> getPermissions()
    {
        return adminClient.readPermissions();
    }
    /**
     * Get permissions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>Collection&lt;Permission&gt;</code> value
     */
    public CollectionPageResponse<Permission> getPermissions(PageRequest inPageRequest)
    {
        return adminClient.readPermissions(inPageRequest);
    }
    /**
     * Get the permissions assigned to the current user.
     *
     * @return a <code>Set&lt;Permission&gt;</code> value
     */
    public Set<Permission> getPermissionsForUser()
    {
        return adminClient.getPermissionsForCurrentUser();
    }
    /**
     * Delete the permission with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void deletePermission(String inName)
    {
        adminClient.deletePermission(inName);
    }
    /**
     * Delete the role with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void deleteRole(String inName)
    {
        adminClient.deleteRole(inName);
    }
    /**
     * Update the given role with the given original name.
     *
     * @param inName a <code>String</code> value
     * @param inRole a <code>Role</code> value
     */
    public void updateRole(String inName,
                           Role inRole)
    {
        adminClient.updateRole(inName,
                               inRole);
    }
    /**
     * Create the given role.
     *
     * @param inRole a <code>Role</code> value
     */
    public void createRole(Role inRole)
    {
        adminClient.createRole(inRole);
    }
    /**
     * Create the given permission.
     *
     * @param inPermission a <code>Permission</code> value
     */
    public void createPermission(Permission inPermission)
    {
        adminClient.createPermission(inPermission);
    }
    /**
     * Update the given permission.
     *
     * @param inPermissionName a <code>String</code> value
     * @param inPermission a <code>Permission</code> value
     */
    public void updatePermission(String inPermissionName,
                                 Permission inPermission)
    {
        adminClient.updatePermission(inPermissionName,
                                     inPermission);
    }
    /**
     * Get the current user.
     *
     * @return a <code>User</code> value
     */
    public User getCurrentUser()
    {
        return adminClient.getCurrentUser();
    }
    /**
     * Sets the adminClientFactory value.
     *
     * @param inAdminClientFactory an <code>AdminRpcClientFactory</code> value
     */
    public void setAdminClientFactory(AdminRpcClientFactory inAdminClientFactory)
    {
        adminClientFactory = inAdminClientFactory;
    }
    /**
     * creates an admin client to connect to the admin server
     */
    private AdminRpcClientFactory adminClientFactory;
    /**
     * client object used to communicate with the server
     */
    private AdminClient adminClient;
}
