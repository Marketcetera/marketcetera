package org.marketcetera.web.service.admin;

import java.util.Collection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixAdminClient;
import org.marketcetera.fix.FixAdminRpcClientFactory;
import org.marketcetera.fix.FixAdminRpcClientParameters;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionInstanceData;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.service.ConnectableService;

import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides access to admin services for a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * TODO separate out FIX admin services
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
        return VaadinSession.getCurrent().getAttribute(AdminClientService.class);
    }
    /**
     * Create a new AdminClientService instance.
     */
    public AdminClientService() {}
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
        if(adminClient.isRunning()) {
            SessionUser loggedInUser = new SessionUser(inUsername,
                                                       inPassword);
            // get the permissions available for this user
            Collection<Permission> permissions = getPermissions();
            // Store the current user in the service session
            loggedInUser.getPermissions().addAll(permissions);
            VaadinSession.getCurrent().setAttribute(SessionUser.class,
                                                    loggedInUser);
            VaadinSession.getCurrent().setAttribute(AdminClientService.class,
                                                    this);
        }
        if(fixAdminClient != null) {
            try {
                fixAdminClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing fix admin client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                fixAdminClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating fixAdmin client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        FixAdminRpcClientParameters fixParams = new FixAdminRpcClientParameters();
        fixParams.setHostname(inHostname);
        fixParams.setPort(inPort);
        fixParams.setUsername(inUsername);
        fixParams.setPassword(inPassword);
        fixAdminClient = fixAdminClientFactory.create(fixParams);
        fixAdminClient.start();
        if(adminClient.isRunning() && fixAdminClient.isRunning()) {
            VaadinSession.getCurrent().setAttribute(AdminClientService.class,
                                                    this);
        }
        return adminClient.isRunning() && fixAdminClient.isRunning();
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
     * Get the instance data for the given affinity.
     *
     * @param inAffinity an <code>int</code> value
     * @return an <code>InstanceData</code> value
     */
    public FixSessionInstanceData getFixSessionInstanceData(int inAffinity)
    {
        return fixAdminClient.getFixSessionInstanceData(inAffinity);
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
     * Sets the fixAdminClientFactory value.
     *
     * @param inFixAdminClientFactory a <code>FixAdminRpcClientFactory</code> value
     */
    public void setFixAdminClientFactory(FixAdminRpcClientFactory inFixAdminClientFactory)
    {
        fixAdminClientFactory = inFixAdminClientFactory;
    }
    /**
     * creates an admin client to connect to the admin server
     */
    private AdminRpcClientFactory adminClientFactory;
    /**
     * creates a FIX admin client to connect to the fix admin server
     */
    private FixAdminRpcClientFactory fixAdminClientFactory;
    /**
     * client object used to communicate with the server
     */
    private AdminClient adminClient;
    /**
     * provides access to FIX admin services
     */
    private FixAdminClient fixAdminClient;
}
