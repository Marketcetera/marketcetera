package org.marketcetera.admin.rpc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.AdminRpc;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.AdminRpc.ChangeUserPasswordRequest;
import org.marketcetera.admin.AdminRpc.ChangeUserPasswordResponse;
import org.marketcetera.admin.AdminRpc.CreateFixSessionRequest;
import org.marketcetera.admin.AdminRpc.CreateFixSessionResponse;
import org.marketcetera.admin.AdminRpc.CreatePermissionRequest;
import org.marketcetera.admin.AdminRpc.CreatePermissionResponse;
import org.marketcetera.admin.AdminRpc.CreateRoleRequest;
import org.marketcetera.admin.AdminRpc.CreateRoleResponse;
import org.marketcetera.admin.AdminRpc.CreateUserRequest;
import org.marketcetera.admin.AdminRpc.CreateUserResponse;
import org.marketcetera.admin.AdminRpc.DeactivateUserRequest;
import org.marketcetera.admin.AdminRpc.DeactivateUserResponse;
import org.marketcetera.admin.AdminRpc.DeleteFixSessionRequest;
import org.marketcetera.admin.AdminRpc.DeleteFixSessionResponse;
import org.marketcetera.admin.AdminRpc.DeletePermissionRequest;
import org.marketcetera.admin.AdminRpc.DeletePermissionResponse;
import org.marketcetera.admin.AdminRpc.DeleteRoleRequest;
import org.marketcetera.admin.AdminRpc.DeleteRoleResponse;
import org.marketcetera.admin.AdminRpc.DeleteUserRequest;
import org.marketcetera.admin.AdminRpc.DeleteUserResponse;
import org.marketcetera.admin.AdminRpc.DisableFixSessionRequest;
import org.marketcetera.admin.AdminRpc.DisableFixSessionResponse;
import org.marketcetera.admin.AdminRpc.EnableFixSessionRequest;
import org.marketcetera.admin.AdminRpc.EnableFixSessionResponse;
import org.marketcetera.admin.AdminRpc.InstanceDataRequest;
import org.marketcetera.admin.AdminRpc.InstanceDataResponse;
import org.marketcetera.admin.AdminRpc.PermissionsForUsernameRequest;
import org.marketcetera.admin.AdminRpc.PermissionsForUsernameResponse;
import org.marketcetera.admin.AdminRpc.ReadFixSessionAttributeDescriptorsRequest;
import org.marketcetera.admin.AdminRpc.ReadFixSessionAttributeDescriptorsResponse;
import org.marketcetera.admin.AdminRpc.ReadFixSessionsRequest;
import org.marketcetera.admin.AdminRpc.ReadFixSessionsResponse;
import org.marketcetera.admin.AdminRpc.ReadPermissionsRequest;
import org.marketcetera.admin.AdminRpc.ReadPermissionsResponse;
import org.marketcetera.admin.AdminRpc.ReadRolesRequest;
import org.marketcetera.admin.AdminRpc.ReadRolesResponse;
import org.marketcetera.admin.AdminRpc.ReadUserAttributeRequest;
import org.marketcetera.admin.AdminRpc.ReadUserAttributeResponse;
import org.marketcetera.admin.AdminRpc.ReadUsersRequest;
import org.marketcetera.admin.AdminRpc.ReadUsersResponse;
import org.marketcetera.admin.AdminRpc.StartFixSessionRequest;
import org.marketcetera.admin.AdminRpc.StartFixSessionResponse;
import org.marketcetera.admin.AdminRpc.StopFixSessionRequest;
import org.marketcetera.admin.AdminRpc.StopFixSessionResponse;
import org.marketcetera.admin.AdminRpc.UpdateFixSessionRequest;
import org.marketcetera.admin.AdminRpc.UpdateFixSessionResponse;
import org.marketcetera.admin.AdminRpc.UpdatePermissionRequest;
import org.marketcetera.admin.AdminRpc.UpdatePermissionResponse;
import org.marketcetera.admin.AdminRpc.UpdateRoleRequest;
import org.marketcetera.admin.AdminRpc.UpdateRoleResponse;
import org.marketcetera.admin.AdminRpc.UpdateSequenceNumbersRequest;
import org.marketcetera.admin.AdminRpc.UpdateSequenceNumbersResponse;
import org.marketcetera.admin.AdminRpc.UpdateUserRequest;
import org.marketcetera.admin.AdminRpc.UpdateUserResponse;
import org.marketcetera.admin.AdminRpc.WriteUserAttributeRequest;
import org.marketcetera.admin.AdminRpc.WriteUserAttributeResponse;
import org.marketcetera.admin.impl.PersistentPermission;
import org.marketcetera.admin.impl.PersistentRole;
import org.marketcetera.admin.impl.PersistentUserAttribute;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserAttributeService;
import org.marketcetera.fix.AcceptorSessionAttributes;
import org.marketcetera.fix.ClusteredBrokerStatus;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.SimpleFixSession;
import org.marketcetera.fix.store.MessageStoreSession;
import org.marketcetera.fix.store.MessageStoreSessionDao;
import org.marketcetera.ors.brokers.BrokerService;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.PageResponse;
import org.marketcetera.persist.Sort;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.BaseRpc;
import org.marketcetera.util.rpc.BaseRpcService;
import org.marketcetera.util.rpc.RpcServiceSpec;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import quickfix.SessionID;

/* $License$ */

/**
 * Provides admin services via RPC.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcService<SessionClazz>
        extends BaseRpcService<SessionClazz>
        implements RpcServiceSpec<SessionClazz>,AdminRpc.AdminRpcService.BlockingInterface
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#createRole(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.CreateRoleRequest)
     */
    @Override
    public CreateRoleResponse createRole(RpcController inController,
                                         CreateRoleRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received add role for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.CreateRoleResponse.Builder responseBuilder = AdminRpc.CreateRoleResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.CreateRoleAction.name());
            if(inRequest.hasRole()) {
                AdminRpc.Role rpcRole = inRequest.getRole();
                String roleName = StringUtils.trimToNull(rpcRole.getName());
                Role role = authzService.findRoleByName(roleName);
                Validate.isTrue(role == null,
                                 "Role: " + roleName + " already exists");
                PersistentRole newRole = new PersistentRole();
                if(rpcRole.hasDescription()) {
                    newRole.setDescription(rpcRole.getDescription());
                } else {
                    newRole.setDescription(null);
                }
                if(rpcRole.hasName()) {
                    newRole.setName(rpcRole.getName());
                } else {
                    newRole.setName(null);
                }
                for(String permissionName : inRequest.getPermissionNameList()) {
                    Permission permission = authzService.findPermissionByName(permissionName);
                    if(permission == null) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot find permission with name '{}' to add to role '{}'",
                                              permissionName,
                                              newRole.getName());
                    } else {
                        newRole.getPermissions().add(permission);
                    }
                }
                for(String username : inRequest.getUsernameList()) {
                    User user = userService.findByName(username);
                    if(user == null) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot find user with name '{}' to add to role '{}'",
                                              username,
                                              newRole.getName());
                    } else {
                        newRole.getSubjects().add(user);
                    }
                }
                role = authzService.save(newRole);
                AdminRpc.Role.Builder roleBuilder = AdminRpc.Role.newBuilder();
                if(role.getDescription() != null) {
                    roleBuilder.setDescription(role.getDescription());
                }
                if(role.getName() != null) {
                    roleBuilder.setName(role.getName());
                }
                responseBuilder.setRole(roleBuilder.build());
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.CreateRoleResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#readRoles(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.ReadRolesRequest)
     */
    @Override
    public ReadRolesResponse readRoles(RpcController inController,
                                       ReadRolesRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received get roles for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.ReadRolesResponse.Builder responseBuilder = AdminRpc.ReadRolesResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ReadRoleAction.name());
            PageRequest pageRequest = null;
            if(inRequest.hasPage()) {
                pageRequest = getPageRequest(inRequest.getPage());
            }
            CollectionPageResponse<Role> pagedResponse = null;
            Collection<Role> roles = null;
            if(pageRequest == null) {
                roles = authzService.findAllRoles();
            } else {
                pagedResponse = authzService.findAllRoles(pageRequest);
                roles = pagedResponse.getElements();
            }
            if(pagedResponse != null) {
                responseBuilder.setPage(getPageResponse(pagedResponse));
            }
            AdminRpc.Role.Builder roleBuilder = AdminRpc.Role.newBuilder();
            for(Role role : roles) {
                if(role.getDescription() != null) {
                    roleBuilder.setDescription(role.getDescription());
                }
                roleBuilder.setName(role.getName());
                AdminRpc.Permission.Builder permissionBuilder = AdminRpc.Permission.newBuilder();
                for(Permission permission : role.getPermissions()) {
                    if(permission.getDescription() != null) {
                        permissionBuilder.setDescription(permission.getDescription());
                    }
                    if(permission.getName() != null) {
                        permissionBuilder.setName(permission.getName());
                    }
                    roleBuilder.addPermission(permissionBuilder.build());
                    permissionBuilder.clear();
                }
                AdminRpc.User.Builder subjectBuilder = AdminRpc.User.newBuilder();
                for(User subject : role.getSubjects()) {
                    subjectBuilder.setActive(subject.isActive());
                    if(subject.getDescription() != null) {
                        subjectBuilder.setDescription(subject.getDescription());
                    }
                    if(subject.getName() != null) {
                        subjectBuilder.setName(subject.getName());
                    }
                    roleBuilder.addUser(subjectBuilder.build());
                    subjectBuilder.clear();
                }
                responseBuilder.addRole(roleBuilder.build());
                roleBuilder.clear();
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.ReadRolesResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#updateRole(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.UpdateRoleRequest)
     */
    @Override
    public UpdateRoleResponse updateRole(RpcController inController,
                                         UpdateRoleRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received update role for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.UpdateRoleResponse.Builder responseBuilder = AdminRpc.UpdateRoleResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.UpdateRoleAction.name());
            PersistentRole role = (PersistentRole)authzService.findRoleByName(inRequest.getRoleName());
            Validate.notNull(role,
                             "Unknown role: " + inRequest.getRoleName());
            if(inRequest.hasRole()) {
                AdminRpc.Role rpcRole = inRequest.getRole();
                String description = null;
                String name = null;
                if(rpcRole.hasDescription()) {
                    description = StringUtils.trimToNull(rpcRole.getDescription());
                }
                if(rpcRole.hasName()) {
                    name = StringUtils.trimToNull(rpcRole.getName());
                }
                role.setName(name);
                role.setDescription(description);
                role.getPermissions().clear();
                role.getSubjects().clear();
                for(String permissionName : inRequest.getPermissionNameList()) {
                    Permission permission = authzService.findPermissionByName(permissionName);
                    if(permission == null) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot find permission with name '{}' to add to role '{}'",
                                              permissionName,
                                              role.getName());
                    } else {
                        role.getPermissions().add(permission);
                    }
                }
                for(String username : inRequest.getUsernameList()) {
                    User user = userService.findByName(username);
                    if(user == null) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot find user with name '{}' to add to role '{}'",
                                              username,
                                              role.getName());
                    } else {
                        role.getSubjects().add(user);
                    }
                }
                role = (PersistentRole)authzService.save(role);
                AdminRpc.Role.Builder roleBuilder = AdminRpc.Role.newBuilder();
                if(role.getDescription() != null) {
                    roleBuilder.setDescription(role.getDescription());
                }
                if(role.getName() != null) {
                    roleBuilder.setName(role.getName());
                }
                AdminRpc.Permission.Builder permissionBuilder = AdminRpc.Permission.newBuilder();
                for(Permission permission : role.getPermissions()) {
                    if(permission.getDescription() != null) {
                        permissionBuilder.setDescription(permission.getDescription());
                    }
                    if(permission.getName() != null) {
                        permissionBuilder.setName(permission.getName());
                    }
                    roleBuilder.addPermission(permissionBuilder.build());
                    permissionBuilder.clear();
                }
                AdminRpc.User.Builder subjectBuilder = AdminRpc.User.newBuilder();
                for(User subject : role.getSubjects()) {
                    subjectBuilder.setActive(subject.isActive());
                    if(subject.getDescription() != null) {
                        subjectBuilder.setDescription(subject.getDescription());
                    }
                    if(subject.getName() != null) {
                        subjectBuilder.setName(subject.getName());
                    }
                    roleBuilder.addUser(subjectBuilder.build());
                    subjectBuilder.clear();
                }
                responseBuilder.setRole(roleBuilder.build());
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.UpdateRoleResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#deleteRole(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.DeleteRoleRequest)
     */
    @Override
    public DeleteRoleResponse deleteRole(RpcController inController,
                                         DeleteRoleRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received delete role for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.DeleteRoleResponse.Builder responseBuilder = AdminRpc.DeleteRoleResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.DeleteRoleAction.name());
            Role role = authzService.findRoleByName(inRequest.getRoleName());
            Validate.notNull(role,
                             "Unknown role: " + inRequest.getRoleName());
            authzService.deleteRole(role.getName());
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.DeleteRoleResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#getInstanceData(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.InstanceDataRequest)
     */
    @Override
    public InstanceDataResponse getInstanceData(RpcController inController,
                                                InstanceDataRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received get instance data for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.InstanceDataResponse.Builder responseBuilder = AdminRpc.InstanceDataResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ReadInstanceDataAction.name());
            AdminRpc.InstanceData.Builder instanceDataBuilder = AdminRpc.InstanceData.newBuilder();
            AcceptorSessionAttributes acceptorSessionAttributes = brokerService.getFixSettingsFor(inRequest.getAffinity());
            if(acceptorSessionAttributes.getHost() != null) {
                instanceDataBuilder.setHostname(acceptorSessionAttributes.getHost());
            }
            instanceDataBuilder.setPort(acceptorSessionAttributes.getPort());
            responseBuilder.setInstanceData(instanceDataBuilder.build());
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.InstanceDataResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#getUsers(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.ReadUsersRequest)
     */
    @Override
    public ReadUsersResponse readUsers(RpcController inController,
                                       ReadUsersRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received get users for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.ReadUsersResponse.Builder responseBuilder = AdminRpc.ReadUsersResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ReadUserAction.name());
            PageRequest pageRequest = null;
            if(inRequest.hasPage()) {
                pageRequest = getPageRequest(inRequest.getPage());
            }
            CollectionPageResponse<User> pagedResponse = null;
            Collection<User> users = null;
            if(pageRequest == null) {
                users = Lists.newArrayList();
                Collection<SimpleUser> castUsers = userService.findAll();
                for(SimpleUser castUser : castUsers) {
                    users.add(castUser);
                }
            } else {
                pagedResponse = userService.findAll(pageRequest);
                users = pagedResponse.getElements();
            }
            if(pagedResponse != null) {
                responseBuilder.setPage(getPageResponse(pagedResponse));
            }
            for(User user : users) {
                responseBuilder.addUser(buildRpcUser(user));
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.ReadUsersResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#createUser(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.CreateUserRequest)
     */
    @Override
    public CreateUserResponse createUser(RpcController inController,
                                         CreateUserRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received add user for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.CreateUserResponse.Builder responseBuilder = AdminRpc.CreateUserResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.CreateUserAction.name());
            if(inRequest.hasUser()) {
                AdminRpc.User rpcUser = inRequest.getUser();
                String username = StringUtils.trimToNull(rpcUser.getName());
                SimpleUser simpleUser = userService.findByName(username);
                Validate.isTrue(simpleUser == null,
                                 "User: " + username + " already exists");
                simpleUser = new SimpleUser();
                if(rpcUser.hasActive()) {
                    simpleUser.setActive(rpcUser.getActive());
                }
                if(rpcUser.hasDescription()) {
                    simpleUser.setDescription(rpcUser.getDescription());
                } else {
                    simpleUser.setDescription(null);
                }
                if(rpcUser.hasName()) {
                    simpleUser.setName(rpcUser.getName());
                } else {
                    simpleUser.setName(null);
                }
                if(inRequest.hasPassword()) {
                    simpleUser.setPassword(inRequest.getPassword().toCharArray());
                }
                simpleUser = userService.save(simpleUser);
                AdminRpc.User.Builder userBuilder = AdminRpc.User.newBuilder();
                userBuilder.setActive(simpleUser.isActive());
                if(simpleUser.getDescription() != null) {
                    userBuilder.setDescription(simpleUser.getDescription());
                }
                if(simpleUser.getName() != null) {
                    userBuilder.setName(simpleUser.getName());
                }
                responseBuilder.setUser(userBuilder.build());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.CreateUserResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#deleteUser(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.DeleteUserRequest)
     */
    @Override
    public DeleteUserResponse deleteUser(RpcController inController,
                                         DeleteUserRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received delete user for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.DeleteUserResponse.Builder responseBuilder = AdminRpc.DeleteUserResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.DeleteUserAction.name());
            SimpleUser simpleUser = userService.findByName(inRequest.getUsername());
            Validate.notNull(simpleUser,
                             "Unknown user: " + inRequest.getUsername());
            userService.delete(simpleUser);
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.DeleteUserResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#deactivateUser(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.DeactivateUserRequest)
     */
    @Override
    public DeactivateUserResponse deactivateUser(RpcController inController,
                                                 DeactivateUserRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received deactivate user for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.DeactivateUserResponse.Builder responseBuilder = AdminRpc.DeactivateUserResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.UpdateUserAction.name());
            SimpleUser simpleUser = userService.findByName(inRequest.getUsername());
            Validate.notNull(simpleUser,
                             "Unknown user: " + inRequest.getUsername());
            userService.updateUserActiveStatus(inRequest.getUsername(),
                                               false);
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.DeactivateUserResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#updateUser(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.UpdateUserRequest)
     */
    @Override
    public UpdateUserResponse updateUser(RpcController inController,
                                         UpdateUserRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received update user for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.UpdateUserResponse.Builder responseBuilder = AdminRpc.UpdateUserResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.UpdateUserAction.name());
            SimpleUser simpleUser = userService.findByName(inRequest.getUsername());
            Validate.notNull(simpleUser,
                             "Unknown user: " + inRequest.getUsername());
            if(inRequest.hasUser()) {
                AdminRpc.User rpcUser = inRequest.getUser();
                if(rpcUser.hasActive()) {
                    simpleUser.setActive(rpcUser.getActive());
                }
                if(rpcUser.hasDescription()) {
                    simpleUser.setDescription(rpcUser.getDescription());
                } else {
                    simpleUser.setDescription(null);
                }
                if(rpcUser.hasName()) {
                    simpleUser.setName(rpcUser.getName());
                } else {
                    simpleUser.setName(null);
                }
                // don't set password with this method
                simpleUser = userService.save(simpleUser);
                AdminRpc.User.Builder userBuilder = AdminRpc.User.newBuilder();
                userBuilder.setActive(simpleUser.isActive());
                userBuilder.setDescription(simpleUser.getDescription());
                userBuilder.setName(simpleUser.getName());
                responseBuilder.setUser(userBuilder.build());
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.UpdateUserResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#changeUserPassword(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.ChangeUserPasswordRequest)
     */
    @Override
    public ChangeUserPasswordResponse changeUserPassword(RpcController inController,
                                                         ChangeUserPasswordRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received change user password for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.ChangeUserPasswordResponse.Builder responseBuilder = AdminRpc.ChangeUserPasswordResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ChangeUserPasswordAction.name());
            SimpleUser simpleUser = userService.findByName(inRequest.getUsername());
            Validate.notNull(simpleUser,
                             "Unknown user: " + inRequest.getUsername());
            simpleUser.changePassword(inRequest.getOldPassword().toCharArray(),
                                      inRequest.getNewPassword().toCharArray());
            simpleUser = userService.save(simpleUser);
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.ChangeUserPasswordResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#createPermission(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.CreatePermissionRequest)
     */
    @Override
    public CreatePermissionResponse createPermission(RpcController inController,
                                                     CreatePermissionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received create permission for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.CreatePermissionResponse.Builder responseBuilder = AdminRpc.CreatePermissionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.CreatePermissionAction.name());
            if(inRequest.hasPermission()) {
                AdminRpc.Permission rpcPermission = inRequest.getPermission();
                String permissionName = StringUtils.trimToNull(rpcPermission.getName());
                Permission permission = authzService.findPermissionByName(permissionName);
                Validate.isTrue(permission == null,
                                "Permission: " + permissionName + " already exists");
                String description = null;
                if(rpcPermission.hasDescription()) {
                    description = StringUtils.trimToNull(rpcPermission.getDescription());
                }
                permission = permissionFactory.create(permissionName,
                                                      description);
                permission = authzService.save(permission);
                AdminRpc.Permission.Builder permissionBuilder = AdminRpc.Permission.newBuilder();
                if(permission.getDescription() != null) {
                    permissionBuilder.setDescription(permission.getDescription());
                }
                if(permission.getName() != null) {
                    permissionBuilder.setName(permission.getName());
                }
                responseBuilder.setPermission(permissionBuilder.build());
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.CreatePermissionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#readPermissions(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.ReadPermissionsRequest)
     */
    @Override
    public ReadPermissionsResponse readPermissions(RpcController inController,
                                                   ReadPermissionsRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received read permissions for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.ReadPermissionsResponse.Builder responseBuilder = AdminRpc.ReadPermissionsResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ReadPermissionAction.name());
            PageRequest pageRequest = null;
            if(inRequest.hasPage()) {
                pageRequest = getPageRequest(inRequest.getPage());
            }
            CollectionPageResponse<Permission> pagedResponse = null;
            Collection<Permission> permissions = null;
            if(pageRequest == null) {
                permissions = authzService.findAllPermissions();
            } else {
                pagedResponse = authzService.findAllPermissions(pageRequest);
                permissions = pagedResponse.getElements();
            }
            if(pagedResponse != null) {
                responseBuilder.setPage(getPageResponse(pagedResponse));
            }
            AdminRpc.Permission.Builder permissionBuilder = AdminRpc.Permission.newBuilder();
            for(Permission permission : permissions) {
                if(permission.getDescription() != null) {
                    permissionBuilder.setDescription(permission.getDescription());
                }
                if(permission.getName() != null) {
                    permissionBuilder.setName(permission.getName());
                }
                responseBuilder.addPermission(permissionBuilder.build());
                permissionBuilder.clear();
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.ReadPermissionsResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#updatePermission(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.UpdatePermissionRequest)
     */
    @Override
    public UpdatePermissionResponse updatePermission(RpcController inController,
                                                     UpdatePermissionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received update permission for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.UpdatePermissionResponse.Builder responseBuilder = AdminRpc.UpdatePermissionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.UpdatePermissionAction.name());
            PersistentPermission permission = (PersistentPermission)authzService.findPermissionByName(inRequest.getPermissionName());
            Validate.notNull(permission,
                             "Unknown permission: " + inRequest.getPermissionName());
            if(inRequest.hasPermission()) {
                AdminRpc.Permission rpcPermission = inRequest.getPermission();
                String description = null;
                String name = null;
                if(rpcPermission.hasDescription()) {
                    description = StringUtils.trimToNull(rpcPermission.getDescription());
                }
                if(rpcPermission.hasName()) {
                    name = StringUtils.trimToNull(rpcPermission.getName());
                }
                permission.setName(name);
                permission.setDescription(description);
                permission = (PersistentPermission)authzService.save(permission);
                AdminRpc.Permission.Builder permissionBuilder = AdminRpc.Permission.newBuilder();
                permissionBuilder.setDescription(permission.getDescription());
                permissionBuilder.setName(permission.getName());
                responseBuilder.setPermission(permissionBuilder.build());
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.UpdatePermissionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#deletePermission(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.DeletePermissionRequest)
     */
    @Override
    public DeletePermissionResponse deletePermission(RpcController inController,
                                                     DeletePermissionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received delete permission for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.DeletePermissionResponse.Builder responseBuilder = AdminRpc.DeletePermissionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.DeletePermissionAction.name());
            Permission permission = authzService.findPermissionByName(inRequest.getPermissionName());
            Validate.notNull(permission,
                             "Unknown permission: " + inRequest.getPermissionName());
            authzService.deletePermission(permission.getName());
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.DeletePermissionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#getPermissionsForUsername(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.PermissionsForUsernameRequest)
     */
    @Override
    public PermissionsForUsernameResponse getPermissionsForUsername(RpcController inController,
                                                                    PermissionsForUsernameRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received get permissions for username for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.PermissionsForUsernameResponse.Builder responseBuilder = AdminRpc.PermissionsForUsernameResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            Set<Permission> internalPermissions = authzService.findAllPermissionsByUsername(sessionHolder.getUser());
            if(internalPermissions != null) {
                for(Permission internalPermission : internalPermissions) {
                    try {
                        responseBuilder.addPermissions(internalPermission.getName());
                    } catch (IllegalArgumentException e) {
                        SLF4JLoggerProxy.warn(this,
                                              "{} is not a valid permission name, skipping",
                                              internalPermission);
                    }
                }
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.PermissionsForUsernameResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#createFixSession(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.CreateFixSessionRequest)
     */
    @Override
    public CreateFixSessionResponse createFixSession(RpcController inController,
                                                     CreateFixSessionRequest inRequest)
            throws ServiceException
    {
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
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#updateFixSession(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.UpdateFixSessionRequest)
     */
    @Override
    public UpdateFixSessionResponse updateFixSession(RpcController inController,
                                                     UpdateFixSessionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received update FIX session for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.UpdateFixSessionResponse.Builder responseBuilder = AdminRpc.UpdateFixSessionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.EditSessionAction.name());
            FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
            if(fixSession == null) {
                throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
            }
            if(inRequest.hasFixSession()) {
                AdminRpc.FixSession rpcFixSession = inRequest.getFixSession();
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
                    fixSession.getSessionSettings().clear();
                    fixSession.getSessionSettings().putAll(sessionSettings);
                }
                brokerService.save(fixSession);
            }
        } catch (Exception e) {
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.UpdateFixSessionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               sessionId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.rpc.AdminRpc.AdminRpcService.BlockingInterface#getFixSessions(com.google.protobuf.RpcController, com.marketcetera.admin.rpc.AdminRpc.ReadFixSessionsRequest)
     */
    @Override
    public ReadFixSessionsResponse readFixSessions(RpcController inController,
                                                   ReadFixSessionsRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received getFixSessions request from {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.ReadFixSessionsResponse.Builder responseBuilder = AdminRpc.ReadFixSessionsResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ViewSessionAction.name());
            PageRequest pageRequest = null;
            if(inRequest.hasPage()) {
                pageRequest = getPageRequest(inRequest.getPage());
            }
            CollectionPageResponse<FixSession> pagedResponse = null;
            Collection<FixSession> sessions = null;
            if(pageRequest == null) {
                sessions = brokerService.findFixSessions();
            } else {
                pagedResponse = brokerService.findFixSessions(pageRequest);
                sessions = pagedResponse.getElements();
            }
            if(pagedResponse != null) {
                responseBuilder.setPage(getPageResponse(pagedResponse));
            }
            SortedMap<BrokerID,FixSessionHolder> fixSessions = new TreeMap<>();
            for(FixSession session : sessions) {
                if(session.isDeleted()) {
                    continue;
                }
                BrokerID brokerId = new BrokerID(session.getBrokerId());
                ClusteredBrokerStatus brokerStatus = brokerService.getBrokerStatus(brokerId);
                if(brokerStatus != null) {
                    if(session.isEnabled() && !brokerStatus.getStatus().isPrimary()) {
                        // this means it's a backup status from another node or it's a brand-new acceptor node that hasn't been created yet because nobody's tried to connect to it
                        if(fixSessions.containsKey(brokerId)) {
                            // there's already a session entry for this broker, don't replace it
                            continue;
                        }
                        // there's nothing yet for this broker, so add this one in
                    }
                }
                FixSessionHolder fixSession = new FixSessionHolder();
                fixSession.brokerId = brokerId;
                fixSession.brokerStatus = brokerStatus;
                fixSession.fixSession = session;
                fixSessions.put(brokerId,
                                fixSession);
            }
            AdminRpc.FixSession.Builder fixSessionBuilder = AdminRpc.FixSession.newBuilder();
            AdminRpc.ActiveFixSession.Builder activeFixSessionBuilder = AdminRpc.ActiveFixSession.newBuilder();
            for(FixSessionHolder displaySession : fixSessions.values()) {
                MessageStoreSession messageStoreSession = fixSessionStoreDao.findBySessionId(displaySession.fixSession.getSessionId());
                fixSessionBuilder.setAcceptor(displaySession.fixSession.isAcceptor());
                fixSessionBuilder.setAffinity(displaySession.fixSession.getAffinity());
                fixSessionBuilder.setBrokerId(displaySession.brokerId.getValue());
                if(displaySession.fixSession.getDescription() != null) {
                    fixSessionBuilder.setDescription(displaySession.fixSession.getDescription());
                }
                activeFixSessionBuilder.setEnabled(displaySession.fixSession.isEnabled());
                fixSessionBuilder.setHost(displaySession.fixSession.getHost());
                if(messageStoreSession == null) {
                    activeFixSessionBuilder.setSenderSeqNum(0);
                    activeFixSessionBuilder.setTargetSeqNum(0);
                } else {
                    activeFixSessionBuilder.setSenderSeqNum(messageStoreSession.getSenderSeqNum());
                    activeFixSessionBuilder.setTargetSeqNum(messageStoreSession.getTargetSeqNum());
                }
                activeFixSessionBuilder.setInstance(displaySession.brokerStatus.getHost());
                fixSessionBuilder.setName(displaySession.fixSession.getName());
                fixSessionBuilder.setPort(displaySession.fixSession.getPort());
                fixSessionBuilder.setSessionId(displaySession.fixSession.getSessionId());
                BaseRpc.Properties.Builder propertiesBuilder = BaseRpc.Properties.newBuilder();
                for(Map.Entry<String,String> entry : displaySession.fixSession.getSessionSettings().entrySet()) {
                    BaseRpc.Property.Builder propertyBuilder = BaseRpc.Property.newBuilder();
                    if(entry.getKey() != null) {
                        propertyBuilder.setKey(entry.getKey());
                    }
                    if(entry.getValue() != null) {
                        propertyBuilder.setValue(entry.getValue());
                    }
                    propertiesBuilder.addProperty(propertyBuilder.build());
                }
                fixSessionBuilder.setSessionSettings(propertiesBuilder.build());
                ClusteredBrokerStatus brokerStatus = displaySession.brokerStatus;
                if(brokerStatus == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} has no broker status",
                                           displaySession.brokerId);
                    activeFixSessionBuilder.setStatus(FixSessionStatus.UNKNOWN.name());
                } else {
                    activeFixSessionBuilder.setStatus(brokerStatus.getStatus().name());
                    if(displaySession.fixSession.isEnabled()) {
                        activeFixSessionBuilder.setInstance(brokerStatus.getClusterData().toString());
                    } else {
                        activeFixSessionBuilder.setInstance("disabled");
                    }
                }
                activeFixSessionBuilder.setFixSession(fixSessionBuilder.build());
                responseBuilder.addFixSession(activeFixSessionBuilder.build());
                fixSessionBuilder.clear();
                activeFixSessionBuilder.clear();
            }
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.ReadFixSessionsResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#readFixSessionAttributeDescriptors(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.ReadFixSessionAttributeDescriptorsRequest)
     */
    @Override
    public ReadFixSessionAttributeDescriptorsResponse readFixSessionAttributeDescriptors(RpcController inController,
                                                                                         ReadFixSessionAttributeDescriptorsRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received read FIX session attribute descriptors request from {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId);
        AdminRpc.ReadFixSessionAttributeDescriptorsResponse.Builder responseBuilder = AdminRpc.ReadFixSessionAttributeDescriptorsResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ReadFixSessionAttributeDescriptorsAction.name());
            Collection<FixSessionAttributeDescriptor> descriptors = brokerService.getFixSessionAttributeDescriptors();
            AdminRpc.FixSessionAttributeDescriptor.Builder descriptorBuilder = AdminRpc.FixSessionAttributeDescriptor.newBuilder();
            for(FixSessionAttributeDescriptor descriptor : descriptors) {
                if(descriptor.getAdvice() != null) {
                    descriptorBuilder.setAdvice(descriptor.getAdvice());
                }
                if(descriptor.getDefaultValue() != null) {
                    descriptorBuilder.setDefaultValue(descriptor.getDefaultValue());
                }
                if(descriptor.getDescription() != null) {
                    descriptorBuilder.setDescription(descriptor.getDescription());
                }
                if(descriptor.getName() != null) {
                    descriptorBuilder.setName(descriptor.getName());
                }
                if(descriptor.getPattern() != null) {
                    descriptorBuilder.setPattern(descriptor.getPattern());
                }
                descriptorBuilder.setRequired(descriptor.isRequired());
                responseBuilder.addFixSessionAttributeDescriptors(descriptorBuilder.build());
                descriptorBuilder.clear();
            }
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.ReadFixSessionAttributeDescriptorsResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#enableFixSession(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.EnableFixSessionRequest)
     */
    @Override
    public EnableFixSessionResponse enableFixSession(RpcController inController,
                                                     EnableFixSessionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received enableFixSession request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getName());
        AdminRpc.EnableFixSessionResponse.Builder responseBuilder = AdminRpc.EnableFixSessionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.EnableSessionAction.name());
            FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
            if(fixSession == null) {
                throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
            }
            brokerService.enableSession(new SessionID(fixSession.getSessionId()));
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.EnableFixSessionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#disableFixSession(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.DisableFixSessionRequest)
     */
    @Override
    public DisableFixSessionResponse disableFixSession(RpcController inController,
                                                       DisableFixSessionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received enableFixSession request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getName());
        AdminRpc.DisableFixSessionResponse.Builder responseBuilder = AdminRpc.DisableFixSessionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.DisableSessionAction.name());
            FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
            if(fixSession == null) {
                throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
            }
            brokerService.disableSession(new SessionID(fixSession.getSessionId()));
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.DisableFixSessionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#deleteFixSession(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.DeleteFixSessionRequest)
     */
    @Override
    public DeleteFixSessionResponse deleteFixSession(RpcController inController,
                                                     DeleteFixSessionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received deleteFixSession request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getName());
        AdminRpc.DeleteFixSessionResponse.Builder responseBuilder = AdminRpc.DeleteFixSessionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.DeleteSessionAction.name());
            FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
            if(fixSession == null) {
                throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
            }
            brokerService.delete(new SessionID(fixSession.getSessionId()));
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.DeleteFixSessionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#startFixSession(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.StartFixSessionRequest)
     */
    @Override
    public StartFixSessionResponse startFixSession(RpcController inController,
                                                   StartFixSessionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received startFixSession request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getName());
        AdminRpc.StartFixSessionResponse.Builder responseBuilder = AdminRpc.StartFixSessionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.StartSessionAction.name());
            FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
            if(fixSession == null) {
                throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
            }
            brokerService.startSession(new SessionID(fixSession.getSessionId()));
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.StartFixSessionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#stopFixSession(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.StopFixSessionRequest)
     */
    @Override
    public StopFixSessionResponse stopFixSession(RpcController inController,
                                                 StopFixSessionRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received stopFixSession request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getName());
        AdminRpc.StopFixSessionResponse.Builder responseBuilder = AdminRpc.StopFixSessionResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.StopSessionAction.name());
            FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
            if(fixSession == null) {
                throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
            }
            brokerService.stopSession(new SessionID(fixSession.getSessionId()));
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.StopFixSessionResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#updateSequenceNumbers(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.UpdateSequenceNumbersRequest)
     */
    @Override
    public UpdateSequenceNumbersResponse updateSequenceNumbers(RpcController inController,
                                                               UpdateSequenceNumbersRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received update sequence numbers request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getName());
        AdminRpc.UpdateSequenceNumbersResponse.Builder responseBuilder = AdminRpc.UpdateSequenceNumbersResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.UpdateSequenceAction.name());
            FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
            if(fixSession == null) {
                throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
            }
            ClusteredBrokerStatus brokerStatus = brokerService.getBrokerStatus(new BrokerID(fixSession.getBrokerId()));
            if(brokerStatus.getStatus().isStarted()) {
                throw new IllegalArgumentException("FIX session " + inRequest.getName() + " is running");
            }
            // TODO this should be moved to a service with transactions
            MessageStoreSession sessionInfo = fixSessionStoreDao.findBySessionId(fixSession.getSessionId());
            if(sessionInfo == null) {
                throw new IllegalArgumentException("No FIX session store with name '" + inRequest.getName() + "'. The session may need to be started first.");
            }
            if(inRequest.hasSenderSequenceNumber()) {
                sessionInfo.setSenderSeqNum(inRequest.getSenderSequenceNumber());
            }
            if(inRequest.hasTargetSequenceNumber()) {
                sessionInfo.setTargetSeqNum(inRequest.getTargetSequenceNumber());
            }
            fixSessionStoreDao.save(sessionInfo);
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.UpdateSequenceNumbersResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#readUserAttribute(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.ReadUserAttributeRequest)
     */
    @Override
    public ReadUserAttributeResponse readUserAttribute(RpcController inController,
                                                       ReadUserAttributeRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received read user attribute request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getUsername());
        AdminRpc.ReadUserAttributeResponse.Builder responseBuilder = AdminRpc.ReadUserAttributeResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.ReadUserAttributeAction.name());
            UserAttributeType userAttributeType = UserAttributeType.valueOf(inRequest.getAttributeType());
            User user = userService.findByName(inRequest.getUsername());
            if(user == null) {
                throw new IllegalArgumentException("Unknown user: '" + inRequest.getUsername()+"'");
            }
            UserAttribute userAttribute = userAttributeService.getUserAttribute(user,
                                                                                userAttributeType);
            if(userAttribute != null) {
                AdminRpc.UserAttribute.Builder userAttributeBuilder = AdminRpc.UserAttribute.newBuilder();
                if(userAttribute.getAttribute() != null) {
                    userAttributeBuilder.setAttribute(userAttribute.getAttribute());
                }
                if(userAttribute.getAttributeType() != null) {
                    userAttributeBuilder.setAttributeType(userAttribute.getAttributeType().name());
                }
                if(userAttribute.getUser() != null) {
                    userAttributeBuilder.setUser(buildRpcUser(userAttribute.getUser()));
                }
                responseBuilder.setUserAttribute(userAttributeBuilder.build());
            }
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.ReadUserAttributeResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface#writeUserAttribute(com.google.protobuf.RpcController, com.marketcetera.admin.AdminRpc.WriteUserAttributeRequest)
     */
    @Override
    public WriteUserAttributeResponse writeUserAttribute(RpcController inController,
                                                         WriteUserAttributeRequest inRequest)
            throws ServiceException
    {
        String sessionId = inRequest.getSessionId();
        SLF4JLoggerProxy.trace(this,
                               "{} received write user attribute request from {} for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               sessionId,
                               inRequest.getUsername());
        AdminRpc.WriteUserAttributeResponse.Builder responseBuilder = AdminRpc.WriteUserAttributeResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(sessionId);
        try {
            SessionHolder<SessionClazz> sessionHolder = getServerServices().validateAndReturnSession(sessionId);
            authzService.authorize(sessionHolder.getUser(),
                                   AdminPermissions.WriteUserAttributeAction.name());
            UserAttributeType userAttributeType = UserAttributeType.valueOf(inRequest.getAttributeType());
            User user = userService.findByName(inRequest.getUsername());
            if(user == null) {
                throw new IllegalArgumentException("Unknown user: '" + inRequest.getUsername()+"'");
            }
            PersistentUserAttribute userAttribute = (PersistentUserAttribute)userAttributeService.getUserAttribute(user,
                                                                                                                   userAttributeType);
            if(userAttribute == null) {
                userAttribute = (PersistentUserAttribute)userAttributeFactory.create(user,
                                                                                     userAttributeType,
                                                                                     inRequest.getAttribute());
            } else {
                userAttribute.setAttribute(inRequest.getAttribute());
            }
            userAttributeService.save(userAttribute);
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
        }
        responseBuilder.setStatus(statusBuilder.build());
        AdminRpc.WriteUserAttributeResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               response);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.RpcServiceSpec#getDescription()
     */
    @Override
    public String getDescription()
    {
        return DESCRIPTION;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.RpcServiceSpec#generateService()
     */
    @Override
    public BlockingService generateService()
    {
        return AdminRpc.AdminRpcService.newReflectiveBlockingService(this);
    }
    /**
     * Build an RPC user from the given user object.
     *
     * @param inUser a <code>User</code> value
     * @return an <code>AdminRpc.User</code> value
     */
    private AdminRpc.User buildRpcUser(User inUser)
    {
        AdminRpc.User.Builder userBuilder = AdminRpc.User.newBuilder();
        userBuilder.setActive(inUser.isActive());
        if(inUser.getDescription() != null) {
            userBuilder.setDescription(inUser.getDescription());
        }
        userBuilder.setName(inUser.getName());
        return userBuilder.build();
    }
    /**
     * Get an RPC page response from the given page response object.
     *
     * @param inPagedResponse a <code>PageResponse</code> value
     * @return a <code>BaseRpc.PageResponse</code> value
     */
    private BaseRpc.PageResponse getPageResponse(PageResponse inPagedResponse)
    {
        BaseRpc.PageResponse.Builder pageResponseBuilder = BaseRpc.PageResponse.newBuilder();
        pageResponseBuilder.setPageMaxSize(inPagedResponse.getPageMaxSize());
        pageResponseBuilder.setPageNumber(inPagedResponse.getPageNumber());
        pageResponseBuilder.setPageSize(inPagedResponse.getPageSize());
        pageResponseBuilder.setTotalPages(inPagedResponse.getTotalPages());
        pageResponseBuilder.setTotalSize(inPagedResponse.getTotalSize());
        if(inPagedResponse.getSortOrder() != null && !inPagedResponse.getSortOrder().isEmpty()) {
            BaseRpc.SortOrder.Builder sortOrderBuilder = BaseRpc.SortOrder.newBuilder();
            BaseRpc.Sort.Builder sortBuilder = BaseRpc.Sort.newBuilder();
            for(Sort sort : inPagedResponse.getSortOrder()) {
                if(sort.getDirection() != null) {
                    sortBuilder.setDirection(sort.getDirection()==SortDirection.ASCENDING?BaseRpc.SortDirection.ASCENDING:BaseRpc.SortDirection.DESCENDING);
                }
                if(sort.getProperty() != null) {
                    sortBuilder.setProperty(sort.getProperty());
                }
                sortOrderBuilder.addSort(sortBuilder.build());
                sortBuilder.clear();
            }
            pageResponseBuilder.setSortOrder(sortOrderBuilder.build());
        }
        return pageResponseBuilder.build();
    }
    /**
     * Get a page request from the given RPC page request object.
     *
     * @param inRpcPageRequest a <code>BaseRpc.PageRequest</code> value
     * @return a <code>PageRequest</code> value
     */
    private PageRequest getPageRequest(BaseRpc.PageRequest inRpcPageRequest)
    {
        PageRequest pageRequest = new PageRequest();
        if(inRpcPageRequest.hasPage()) {
            pageRequest.setPageNumber(inRpcPageRequest.getPage());
        }
        if(inRpcPageRequest.hasSize()) {
            pageRequest.setPageSize(inRpcPageRequest.getSize());
        }
        List<Sort> sortOrder = Lists.newArrayList();
        if(inRpcPageRequest.hasSortOrder()) {
            for(BaseRpc.Sort rpcSort : inRpcPageRequest.getSortOrder().getSortList()) {
                Sort sort = new Sort();
                if(rpcSort.hasDirection()) {
                    sort.setDirection(rpcSort.getDirection()==BaseRpc.SortDirection.ASCENDING?SortDirection.ASCENDING:SortDirection.DESCENDING);
                }
                if(rpcSort.hasProperty()) {
                    sort.setProperty(rpcSort.getProperty());
                }
                sortOrder.add(sort);
            }
        }
        pageRequest.setSortOrder(sortOrder);
        return pageRequest;
    }
    /**
     * Holds the status for a particular session for a particular cluster member.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class FixSessionHolder
            implements Comparable<FixSessionHolder>
    {
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(FixSessionHolder inO)
        {
            return new CompareToBuilder().append(brokerId,inO.brokerId).toComparison();
        }
        /**
         * broker id value
         */
        private BrokerID brokerId;
        /**
         * fix session value
         */
        private FixSession fixSession;
        /**
         * broker status value
         */
        private ClusteredBrokerStatus brokerStatus;
    }
    /**
     * creates {@link UserAttribute} objects
     */
    @Autowired
    private UserAttributeFactory userAttributeFactory;
    /**
     * provides access to user attribute services
     */
    @Autowired
    private UserAttributeService userAttributeService;
    /**
     * provides access to the FIX session data store
     */
    @Autowired
    private MessageStoreSessionDao fixSessionStoreDao;
    /**
     * creates {@link Permission} objects
     */
    @Autowired
    private PermissionFactory permissionFactory;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * provides access to core broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * description of the service
     */
    private static final String DESCRIPTION = "MATP Admin RPC Service"; //$NON-NLS-1$
}
