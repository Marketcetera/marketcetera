package org.marketcetera.admin.rest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.InstanceData;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/* $License$ */

/**
 * Provides a REST {@link AdminClient} server implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Controller
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties("admin")
@Api(value="Admin server operations")
public class AdminRestServer
        implements AdminClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#getPermissionsForCurrentUser()
     */
    @Override
    @ResponseBody
    @RequestMapping(consumes={"application/json","application/xml"},method=RequestMethod.GET,produces= {"application/json","application/xml"},value="/admin/getPermissions")
    @ApiOperation(value="Gets user permissions",response=Set.class,protocols= "http,https",
                  notes="Get the permissions for the user")
    @ApiResponses(value={ @ApiResponse(code=200,message="Successfully returned permissions"),
                          @ApiResponse(code=401,message="Not logged in") })
    public Set<Permission> getPermissionsForCurrentUser()
    {
        return authzService.findAllPermissionsByUsername(sessionHolder.getUser());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#createFixSession(org.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession createFixSession(FixSession inFixSession)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readFixSessions()
     */
    @Override
    public List<ActiveFixSession> readFixSessions()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readFixSessions(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<ActiveFixSession> readFixSessions(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#updateFixSession(java.lang.String, org.marketcetera.fix.FixSession)
     */
    @Override
    public void updateFixSession(String inIncomingName,
                                 FixSession inFixSession)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#enableFixSession(java.lang.String)
     */
    @Override
    public void enableFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#disableFixSession(java.lang.String)
     */
    @Override
    public void disableFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#deleteFixSession(java.lang.String)
     */
    @Override
    public void deleteFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#stopFixSession(java.lang.String)
     */
    @Override
    public void stopFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#startFixSession(java.lang.String)
     */
    @Override
    public void startFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readUsers()
     */
    @Override
    public List<User> readUsers()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readUsers(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<User> readUsers(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#createUser(org.marketcetera.admin.User, java.lang.String)
     */
    @Override
    public User createUser(User inNewUser,
                           String inPassword)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#updateUser(java.lang.String, org.marketcetera.admin.User)
     */
    @Override
    public User updateUser(String inUsername,
                           User inUpdatedUser)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#changeUserPassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void changeUserPassword(String inUsername,
                                   String inOldPassword,
                                   String inNewPassword)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#deleteUser(java.lang.String)
     */
    @Override
    public void deleteUser(String inUsername)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#deactivateUser(java.lang.String)
     */
    @Override
    public void deactivateUser(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#createPermission(org.marketcetera.admin.Permission)
     */
    @Override
    public Permission createPermission(Permission inPermission)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readPermissions()
     */
    @Override
    public List<Permission> readPermissions()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readPermissions(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<Permission> readPermissions(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#updatePermission(java.lang.String, org.marketcetera.admin.Permission)
     */
    @Override
    public Permission updatePermission(String inPermissionName,
                                       Permission inUpdatedPermission)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#deletePermission(java.lang.String)
     */
    @Override
    public void deletePermission(String inPermissionName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#createRole(org.marketcetera.admin.Role)
     */
    @Override
    public Role createRole(Role inRole)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readRoles()
     */
    @Override
    public List<Role> readRoles()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#readRoles(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<Role> readRoles(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#deleteRole(java.lang.String)
     */
    @Override
    public void deleteRole(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#updateRole(java.lang.String, org.marketcetera.admin.Role)
     */
    @Override
    public Role updateRole(String inName,
                           Role inRole)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#getInstanceData(int)
     */
    @Override
    public InstanceData getInstanceData(int inAffinity)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#getFixSessionAttributeDescriptors()
     */
    @Override
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#updateSequenceNumbers(java.lang.String, int, int)
     */
    @Override
    public void updateSequenceNumbers(String inSessionName,
                                      int inSenderSequenceNumber,
                                      int inTargetSequenceNumber)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#updateSenderSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateSenderSequenceNumber(String inSessionName,
                                           int inSenderSequenceNumber)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#updateTargetSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateTargetSequenceNumber(String inSessionName,
                                           int inTargetSequenceNumber)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#getUserAttribute(java.lang.String, org.marketcetera.admin.UserAttributeType)
     */
    @Override
    public UserAttribute getUserAttribute(String inUsername,
                                          UserAttributeType inAttributeType)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.admin.AdminClient#setUserAttribute(java.lang.String, org.marketcetera.admin.UserAttributeType, java.lang.String)
     */
    @Override
    public void setUserAttribute(String inUsername,
                                 UserAttributeType inAttributeType,
                                 String inAttribute)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}
