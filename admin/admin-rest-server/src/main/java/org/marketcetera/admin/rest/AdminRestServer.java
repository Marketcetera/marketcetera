package org.marketcetera.admin.rest;

import java.security.Principal;
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
import org.marketcetera.fix.SimpleFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
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
{
    /**
     * Get permissions for the current user.
     *
     * @param inPrincipal a <code>Principal</code> value
     * @return a <code>Set&lt;Permission&gt;</code> value
     */
    @ResponseBody
    @RequestMapping(consumes={"application/json","application/xml"},method=RequestMethod.GET,produces={"application/json","application/xml"},value="/admin/getPermissions")
    @ApiOperation(value="Gets user permissions",response=Set.class,protocols= "http,https",
                  notes="Get the permissions for the user")
    @ApiResponses(value={ @ApiResponse(code=200,message="Successfully returned permissions"),
                          @ApiResponse(code=401,message="Not logged in") })
    public Set<Permission> getPermissionsForCurrentUser(Principal inPrincipal)
    {
        return authzService.findAllPermissionsByUsername(inPrincipal.getName());
    }
    @ResponseBody
    @RequestMapping(consumes={"application/json","application/xml"},method=RequestMethod.PUT,produces={"application/json","application/xml"},value="/admin/createFixSession")
    @ApiOperation(value="Create a new FIX session",response=Set.class,protocols= "http,https",
                  notes="Creates a new, disabled FIX session")
    @ApiResponses(value={ @ApiResponse(code=200,message="Successfully created FIX session"),
                          @ApiResponse(code=401,message="Not logged in") })
    public SimpleFixSession createFixSession(SimpleFixSession inFixSession)
    {
        /*
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received create FIX session for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.CreateFixSessionResponse.Builder responseBuilder = AdminRpc.CreateFixSessionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.AddSessionAction.name());
            if(inRequest.hasFixSession()) {
                AdminRpc.FixSession rpcFixSession = inRequest.getFixSession();
                SimpleFixSession fixSession = new SimpleFixSession();
                if(rpcFixSession.hasAcceptor()) {
                    fixSession.setIsAcceptor(rpcFixSession.getAcceptor());
                }
                if(rpcFixSession.hasAffinity()) {
                    fixSession.setAffinity(rpcFixSession.getAffinity());
                }
                if(rpcFixSession.hasBrokerId()) {
                    fixSession.setBrokerId(rpcFixSession.getBrokerId());
                }
                if(rpcFixSession.hasDescription()) {
                    fixSession.setDescription(rpcFixSession.getDescription());
                }
                if(rpcFixSession.hasHost()) {
                    fixSession.setHost(rpcFixSession.getHost());
                }
                if(rpcFixSession.hasName()) {
                    fixSession.setName(rpcFixSession.getName());
                }
                if(rpcFixSession.hasPort()) {
                    fixSession.setPort(rpcFixSession.getPort());
                }
                if(rpcFixSession.hasSessionId()) {
                    fixSession.setSessionId(rpcFixSession.getSessionId());
                }
                if(rpcFixSession.hasSessionSettings()) {
                    Map<String,String> sessionSettings = new HashMap<>();
                    BaseRpc.Properties rpcProperties = rpcFixSession.getSessionSettings();
                    for(BaseRpc.Property rpcProperty : rpcProperties.getPropertyList()) {
                        if(rpcProperty.hasKey()) {
                            sessionSettings.put(rpcProperty.getKey(),
                                                rpcProperty.getValue());
                        }
                    }
                    fixSession.setSessionSettings(sessionSettings);
                }
                FixSession newFixSession = brokerService.save(fixSession);
                AdminRpc.FixSession.Builder fixSessionBuilder = AdminRpc.FixSession.newBuilder();
                fixSessionBuilder.setAcceptor(newFixSession.isAcceptor());
                fixSessionBuilder.setAffinity(newFixSession.getAffinity());
                if(newFixSession.getBrokerId() != null) {
                    fixSessionBuilder.setBrokerId(newFixSession.getBrokerId());
                }
                if(newFixSession.getDescription() != null) {
                    fixSessionBuilder.setDescription(newFixSession.getDescription());
                }
                if(newFixSession.getHost() != null) {
                    fixSessionBuilder.setHost(newFixSession.getHost());
                }
                if(newFixSession.getName() != null) {
                    fixSessionBuilder.setName(newFixSession.getName());
                }
                fixSessionBuilder.setPort(newFixSession.getPort());
                if(newFixSession.getSessionId() != null) {
                    fixSessionBuilder.setSessionId(newFixSession.getSessionId());
                }
                BaseRpc.Properties.Builder propertiesBuilder = BaseRpc.Properties.newBuilder();
                for(Map.Entry<String,String> entry : newFixSession.getSessionSettings().entrySet()) {
                    BaseRpc.Property.Builder propertyBuilder = BaseRpc.Property.newBuilder();
                    if(entry.getKey() != null) {
                        propertyBuilder.setKey(entry.getKey());
                        propertyBuilder.setValue(entry.getValue());
                    }
                    propertiesBuilder.addProperty(propertyBuilder.build());
                }
                fixSessionBuilder.setSessionSettings(propertiesBuilder.build());
                responseBuilder.setFixSession(fixSessionBuilder.build());
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.CreateFixSessionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
         */
        throw new UnsupportedOperationException(); // TODO
        
    }
    public List<ActiveFixSession> readFixSessions()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public CollectionPageResponse<ActiveFixSession> readFixSessions(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void updateFixSession(String inIncomingName,
                                 FixSession inFixSession)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void enableFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void disableFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void deleteFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void stopFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void startFixSession(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public List<User> readUsers()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public CollectionPageResponse<User> readUsers(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public User createUser(User inNewUser,
                           String inPassword)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public User updateUser(String inUsername,
                           User inUpdatedUser)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void changeUserPassword(String inUsername,
                                   String inOldPassword,
                                   String inNewPassword)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void deleteUser(String inUsername)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void deactivateUser(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public Permission createPermission(Permission inPermission)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public List<Permission> readPermissions()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public CollectionPageResponse<Permission> readPermissions(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public Permission updatePermission(String inPermissionName,
                                       Permission inUpdatedPermission)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void deletePermission(String inPermissionName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public Role createRole(Role inRole)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public List<Role> readRoles()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public CollectionPageResponse<Role> readRoles(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void deleteRole(String inName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public Role updateRole(String inName,
                           Role inRole)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public InstanceData getInstanceData(int inAffinity)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void updateSequenceNumbers(String inSessionName,
                                      int inSenderSequenceNumber,
                                      int inTargetSequenceNumber)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void updateSenderSequenceNumber(String inSessionName,
                                           int inSenderSequenceNumber)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public void updateTargetSequenceNumber(String inSessionName,
                                           int inTargetSequenceNumber)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    public UserAttribute getUserAttribute(String inUsername,
                                          UserAttributeType inAttributeType)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
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
