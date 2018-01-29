package com.marketcetera.web.services;

import java.util.Collection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.cluster.InstanceData;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixAdminClient;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;

import com.marketcetera.web.config.AppConfiguration;
import com.marketcetera.web.config.HostnameConfiguration;
import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides access to admin services for a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminClientService
{
    /**
     * Get the <code>AdminClientService</code> instance for the current session.
     *
     * @return an <code>AdminClientService</code> value or <code>null</code>
     */
    public static AdminClientService getInstance()
    {
        return VaadinSession.getCurrent().getAttribute(AdminClientService.class);
    }
    /**
     * Create a new AdminClientService instance.
     *
     * @param inUsername a <code>String</code> value
     */
    public AdminClientService(String inUsername)
    {
        username = inUsername;
        applicationContext = AppConfiguration.getApplicationContext();
        adminClientFactory = applicationContext.getBean(AdminRpcClientFactory.class);
        hostname = HostnameConfiguration.getInstance().getHostname();
        port = HostnameConfiguration.getInstance().getPort();
    }
    /**
     * Connect to the Admin server.
     *
     * @param inPassword a <code>String</code> value
     * @return a <code>boolean</code> value
     * @throws Exception if an error occurs connecting
     */
    public boolean connect(String inPassword)
            throws Exception
    {
        if(adminClient != null) {
            try {
                adminClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing admin client for {}: {}",
                                      username,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                adminClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating admin client for {} to {}:{}",
                               username,
                               hostname,
                               port);
        AdminRpcClientParameters params = new AdminRpcClientParameters();
        params.setHostname(hostname);
        params.setPort(port);
        params.setUsername(username);
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
     * Get FIX sessions.
     *
     * @return a <code>Collection&lt;ActiveFixSession&gt;</code> value
     */
    public Collection<ActiveFixSession> getFixSessions()
    {
        return fixAdminClient.readFixSessions();
    }
    /**
     * Get a page of FIX sessions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;ActiveFixSession&gt;</code> value
     */
    public CollectionPageResponse<ActiveFixSession> getFixSessions(PageRequest inPageRequest)
    {
        return fixAdminClient.readFixSessions(inPageRequest);
    }
    /**
     * Get the instance data for the given affinity.
     *
     * @param inAffinity an <code>int</code> value
     * @return an <code>InstanceData</code> value
     */
    public InstanceData getInstanceData(int inAffinity)
    {
        return fixAdminClient.getInstanceData(inAffinity);
    }
    /**
     * Get the FIX session attribute descriptors.
     *
     * @return a <code>Collection&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        return fixAdminClient.getFixSessionAttributeDescriptors();
    }
    /**
     * Create a new FIX session.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     */
    public FixSession createFixSession(FixSession inFixSession)
    {
        return fixAdminClient.createFixSession(inFixSession);
    }
    /**
     * Update the FIX session with the given original name.
     *
     * @param inIncomingName a <code>String</code> value
     * @param inFixSession a <code>FixSession</code> value
     */
    public void updateFixSession(String inIncomingName,
                                 FixSession inFixSession)
    {
        fixAdminClient.updateFixSession(inIncomingName,
                                        inFixSession);
    }
    /**
     * Enable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void enableSession(String inName)
    {
        fixAdminClient.enableFixSession(inName);
    }
    /**
     * Disable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void disableSession(String inName)
    {
        fixAdminClient.disableFixSession(inName);
    }
    /**
     * Delete the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void deleteSession(String inName)
    {
        fixAdminClient.deleteFixSession(inName);
    }
    /**
     * Stop the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void stopSession(String inName)
    {
        fixAdminClient.stopFixSession(inName);
    }
    /**
     * Start the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void startSession(String inName)
    {
        fixAdminClient.startFixSession(inName);
    }
    /**
     * Update sender and target sequence numbers for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    public void updateSequenceNumbers(String inSessionName,
                                      int inSenderSequenceNumber,
                                      int inTargetSequenceNumber)
    {
        fixAdminClient.updateSequenceNumbers(inSessionName,
                                             inSenderSequenceNumber,
                                             inTargetSequenceNumber);
    }
    /**
     * Update the sender sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     */
    public void updateSenderSequenceNumber(String inSessionName,
                                           int inSenderSequenceNumber)
    {
        fixAdminClient.updateSenderSequenceNumber(inSessionName,
                                                  inSenderSequenceNumber);
    }
    /**
     * Update the target sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    public void updateTargetSequenceNumber(String inSessionName,
                                           int inTargetSequenceNumber)
    {
        fixAdminClient.updateTargetSequenceNumber(inSessionName,
                                                  inTargetSequenceNumber);
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
     * server hostname to connect to
     */
    private String hostname;
    /**
     * server port to connect to
     */
    private int port;
    /**
     * application context value
     */
    private ApplicationContext applicationContext;
    /**
     * user that owns this connection
     */
    private final String username;
    /**
     * creates an admin client to connect to the admin server
     */
    private AdminRpcClientFactory adminClientFactory;
    /**
     * client object used to communicate with the server
     */
    private AdminClient adminClient;
    /**
     * provides access to FIX admin services
     */
    private FixAdminClient fixAdminClient;
}
