package org.marketcetera.admin.rpc;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.MutablePermission;
import org.marketcetera.admin.MutableRole;
import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.MutableUserAttribute;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserAttributeService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.admin.AdminRpc;
import com.marketcetera.admin.AdminRpc.ChangeUserPasswordRequest;
import com.marketcetera.admin.AdminRpc.ChangeUserPasswordResponse;
import com.marketcetera.admin.AdminRpc.CreatePermissionRequest;
import com.marketcetera.admin.AdminRpc.CreatePermissionResponse;
import com.marketcetera.admin.AdminRpc.CreateRoleRequest;
import com.marketcetera.admin.AdminRpc.CreateRoleResponse;
import com.marketcetera.admin.AdminRpc.CreateUserRequest;
import com.marketcetera.admin.AdminRpc.CreateUserResponse;
import com.marketcetera.admin.AdminRpc.DeactivateUserRequest;
import com.marketcetera.admin.AdminRpc.DeactivateUserResponse;
import com.marketcetera.admin.AdminRpc.DeletePermissionRequest;
import com.marketcetera.admin.AdminRpc.DeletePermissionResponse;
import com.marketcetera.admin.AdminRpc.DeleteRoleRequest;
import com.marketcetera.admin.AdminRpc.DeleteRoleResponse;
import com.marketcetera.admin.AdminRpc.DeleteUserRequest;
import com.marketcetera.admin.AdminRpc.DeleteUserResponse;
import com.marketcetera.admin.AdminRpc.PermissionsForUsernameRequest;
import com.marketcetera.admin.AdminRpc.PermissionsForUsernameResponse;
import com.marketcetera.admin.AdminRpc.ReadPermissionsRequest;
import com.marketcetera.admin.AdminRpc.ReadPermissionsResponse;
import com.marketcetera.admin.AdminRpc.ReadRolesRequest;
import com.marketcetera.admin.AdminRpc.ReadRolesResponse;
import com.marketcetera.admin.AdminRpc.ReadUserAttributeRequest;
import com.marketcetera.admin.AdminRpc.ReadUserAttributeResponse;
import com.marketcetera.admin.AdminRpc.ReadUsersRequest;
import com.marketcetera.admin.AdminRpc.ReadUsersResponse;
import com.marketcetera.admin.AdminRpc.UpdatePermissionRequest;
import com.marketcetera.admin.AdminRpc.UpdatePermissionResponse;
import com.marketcetera.admin.AdminRpc.UpdateRoleRequest;
import com.marketcetera.admin.AdminRpc.UpdateRoleResponse;
import com.marketcetera.admin.AdminRpc.UpdateUserRequest;
import com.marketcetera.admin.AdminRpc.UpdateUserResponse;
import com.marketcetera.admin.AdminRpc.WriteUserAttributeRequest;
import com.marketcetera.admin.AdminRpc.WriteUserAttributeResponse;
import com.marketcetera.admin.AdminRpcServiceGrpc;
import com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides admin services via RPC.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,AdminRpcServiceGrpc.AdminRpcServiceImplBase>
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
            throws Exception
    {
        service = new Service();
        super.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#getServiceDescription()
     */
    @Override
    protected String getServiceDescription()
    {
        return DESCRIPTION;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#getService()
     */
    @Override
    protected AdminRpcServiceImplBase getService()
    {
        return service;
    }
    /**
     * Admin RPC Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: MarketDataRpcService.java 17251 2016-09-08 23:18:29Z colin $
     * @since $Release$
     */
    private class Service
            extends AdminRpcServiceGrpc.AdminRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            AdminRpcService.this.doLogin(inRequest,
                                         inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            AdminRpcService.this.doLogout(inRequest,
                                          inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            AdminRpcService.this.doHeartbeat(inRequest,
                                             inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#getPermissionsForUsername(com.marketcetera.admin.AdminRpc.PermissionsForUsernameRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getPermissionsForUsername(PermissionsForUsernameRequest inRequest,
                                              StreamObserver<PermissionsForUsernameResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received get permissions for user request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.PermissionsForUsernameResponse.Builder responseBuilder = AdminRpc.PermissionsForUsernameResponse.newBuilder();
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
                AdminRpc.PermissionsForUsernameResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#createUser(com.marketcetera.admin.AdminRpc.CreateUserRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createUser(CreateUserRequest inRequest,
                               StreamObserver<CreateUserResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received create user request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.CreateUserResponse.Builder responseBuilder = AdminRpc.CreateUserResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.CreateUserAction.name());
                if(inRequest.hasUser()) {
                    AdminRpc.User rpcUser = inRequest.getUser();
                    String username = StringUtils.trimToNull(rpcUser.getName());
                    User newUser = userService.findByName(username);
                    Validate.isTrue(newUser == null,
                                    "User: " + username + " already exists");
                    newUser = userFactory.create(rpcUser.getName(),
                                                 inRequest.getPassword(),
                                                 rpcUser.getDescription(),
                                                 rpcUser.getActive());
                    newUser = userService.save(newUser);
                    AdminRpcUtil.getRpcUser(newUser).ifPresent(value->responseBuilder.setUser(value));
                }
                AdminRpc.CreateUserResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#readUsers(com.marketcetera.admin.AdminRpc.ReadUsersRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void readUsers(ReadUsersRequest inRequest,
                              StreamObserver<ReadUsersResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received read users request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.ReadUsersResponse.Builder responseBuilder = AdminRpc.ReadUsersResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ReadUserAction.name());
                PageRequest pageRequest = inRequest.hasPage()?PagingRpcUtil.getPageRequest(inRequest.getPage()):PageRequest.ALL;
                CollectionPageResponse<User> userPage = userService.findAll(pageRequest);
                userPage.getElements().forEach(value->AdminRpcUtil.getRpcUser(value).ifPresent(innerValue->responseBuilder.addUser(innerValue)));
                responseBuilder.setPage(PagingRpcUtil.getPageResponse(pageRequest,
                                                                      userPage));
                AdminRpc.ReadUsersResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#updateUser(com.marketcetera.admin.AdminRpc.UpdateUserRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void updateUser(UpdateUserRequest inRequest,
                               StreamObserver<UpdateUserResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received update user request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.UpdateUserResponse.Builder responseBuilder = AdminRpc.UpdateUserResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.UpdateUserAction.name());
                User existingUser = userService.findByName(inRequest.getUsername());
                Validate.isTrue(existingUser != null,
                                "Unknown user: " + inRequest.getUsername());
                if(inRequest.hasUser()) {
                    if(existingUser instanceof MutableUser) {
                        MutableUser mutableUser = (MutableUser)existingUser;
                        AdminRpc.User rpcUser = inRequest.getUser();
                        mutableUser.setIsActive(rpcUser.getActive());
                        mutableUser.setName(rpcUser.getName());
                        mutableUser.setDescription(rpcUser.getDescription());
                        // don't change id or password!
                        existingUser = userService.save(mutableUser);
                        AdminRpcUtil.getRpcUser(existingUser).ifPresent(value->responseBuilder.setUser(value));
                    } else {
                        throw new IllegalStateException("User service returned a non-mutable user - check configuration");
                    }
                }
                AdminRpc.UpdateUserResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#deleteUser(com.marketcetera.admin.AdminRpc.DeleteUserRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteUser(DeleteUserRequest inRequest,
                               StreamObserver<DeleteUserResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received delete user request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.DeleteUserResponse.Builder responseBuilder = AdminRpc.DeleteUserResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.DeleteUserAction.name());
                User existingUser = userService.findByName(inRequest.getUsername());
                Validate.isTrue(existingUser != null,
                                "Unknown user: " + inRequest.getUsername());
                userService.delete(existingUser);
                AdminRpc.DeleteUserResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#deactivateUser(com.marketcetera.admin.AdminRpc.DeactivateUserRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deactivateUser(DeactivateUserRequest inRequest,
                                   StreamObserver<DeactivateUserResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received deactivate user request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.DeactivateUserResponse.Builder responseBuilder = AdminRpc.DeactivateUserResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.DeleteUserAction.name());
                User existingUser = userService.findByName(inRequest.getUsername());
                Validate.isTrue(existingUser != null,
                                "Unknown user: " + inRequest.getUsername());
                if(existingUser instanceof MutableUser) {
                    MutableUser mutableUser = (MutableUser)existingUser;
                    mutableUser.setIsActive(false);
                    existingUser = userService.save(mutableUser);
                } else {
                    throw new IllegalStateException("User service returned a non-mutable user - check configuration");
                }
                AdminRpc.DeactivateUserResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#changeUserPassword(com.marketcetera.admin.AdminRpc.ChangeUserPasswordRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void changeUserPassword(ChangeUserPasswordRequest inRequest,
                                       StreamObserver<ChangeUserPasswordResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received change user password request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.ChangeUserPasswordResponse.Builder responseBuilder = AdminRpc.ChangeUserPasswordResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ChangeUserPasswordAction.name());
                User existingUser = userService.findByName(inRequest.getUsername());
                Validate.isTrue(existingUser != null,
                                "Unknown user: " + inRequest.getUsername());
                userService.changeUserPassword(existingUser,
                                               inRequest.getOldPassword(),
                                               inRequest.getNewPassword());
                AdminRpc.ChangeUserPasswordResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#createPermission(com.marketcetera.admin.AdminRpc.CreatePermissionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createPermission(CreatePermissionRequest inRequest,
                                     StreamObserver<CreatePermissionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received create permission request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.CreatePermissionResponse.Builder responseBuilder = AdminRpc.CreatePermissionResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.CreatePermissionAction.name());
                if(inRequest.hasPermission()) {
                    AdminRpc.Permission rpcPermission = inRequest.getPermission();
                    String name = StringUtils.trimToNull(rpcPermission.getName());
                    Permission newPermission = authzService.findPermissionByName(name);
                    Validate.isTrue(newPermission == null,
                                    "Permission: " + name + " already exists");
                    newPermission = permissionFactory.create(rpcPermission.getName(),
                                                             rpcPermission.getDescription());
                    newPermission = authzService.save(newPermission);
                    AdminRpcUtil.getRpcPermission(newPermission).ifPresent(value->responseBuilder.setPermission(value));
                }
                AdminRpc.CreatePermissionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#readPermissions(com.marketcetera.admin.AdminRpc.ReadPermissionsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void readPermissions(ReadPermissionsRequest inRequest,
                                    StreamObserver<ReadPermissionsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received read permissions request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.ReadPermissionsResponse.Builder responseBuilder = AdminRpc.ReadPermissionsResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ReadPermissionAction.name());
                PageRequest pageRequest = inRequest.hasPage()?PagingRpcUtil.getPageRequest(inRequest.getPage()):PageRequest.ALL;
                CollectionPageResponse<Permission> permissionPage = authzService.findAllPermissions(pageRequest);
                permissionPage.getElements().forEach(permission->AdminRpcUtil.getRpcPermission(permission).ifPresent(rpcPermission->responseBuilder.addPermission(rpcPermission)));
                responseBuilder.setPage(PagingRpcUtil.getPageResponse(pageRequest,
                                                                      permissionPage));
                AdminRpc.ReadPermissionsResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#updatePermission(com.marketcetera.admin.AdminRpc.UpdatePermissionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void updatePermission(UpdatePermissionRequest inRequest,
                                     StreamObserver<UpdatePermissionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received update permission request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.UpdatePermissionResponse.Builder responseBuilder = AdminRpc.UpdatePermissionResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.UpdatePermissionAction.name());
                Permission existingPermission = authzService.findPermissionByName(inRequest.getPermissionName());
                Validate.isTrue(existingPermission != null,
                                "Unknown permission: " + inRequest.getPermissionName());
                if(inRequest.hasPermission()) {
                    if(existingPermission instanceof MutablePermission) {
                        MutablePermission mutablePermission = (MutablePermission)existingPermission;
                        AdminRpc.Permission rpcUser = inRequest.getPermission();
                        mutablePermission.setName(rpcUser.getName());
                        mutablePermission.setDescription(rpcUser.getDescription());
                        existingPermission = authzService.save(mutablePermission);
                        AdminRpcUtil.getRpcPermission(existingPermission).ifPresent(value->responseBuilder.setPermission(value));
                    } else {
                        throw new IllegalStateException("User service returned a non-mutable permission - check configuration");
                    }
                }
                AdminRpc.UpdatePermissionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#deletePermission(com.marketcetera.admin.AdminRpc.DeletePermissionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deletePermission(DeletePermissionRequest inRequest,
                                     StreamObserver<DeletePermissionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received delete permission request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.DeletePermissionResponse.Builder responseBuilder = AdminRpc.DeletePermissionResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.DeletePermissionAction.name());
                Permission existingPermission = authzService.findPermissionByName(inRequest.getPermissionName());
                Validate.isTrue(existingPermission != null,
                                "Unknown permission: " + inRequest.getPermissionName());
                authzService.deletePermission(inRequest.getPermissionName());
                AdminRpc.DeletePermissionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#createRole(com.marketcetera.admin.AdminRpc.CreateRoleRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createRole(CreateRoleRequest inRequest,
                               StreamObserver<CreateRoleResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received create role request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.CreateRoleResponse.Builder responseBuilder = AdminRpc.CreateRoleResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.CreateRoleAction.name());
                if(inRequest.hasRole()) {
                    AdminRpc.Role rpcRole = inRequest.getRole();
                    String name = StringUtils.trimToNull(rpcRole.getName());
                    Role newRole = authzService.findRoleByName(name);
                    Validate.isTrue(newRole == null,
                                    "Role: " + name + " already exists");
                    newRole = roleFactory.create(rpcRole.getName(),
                                                 rpcRole.getDescription());
                    for(AdminRpc.Permission rpcPermission : rpcRole.getPermissionList()) {
                        Permission permission = authzService.findPermissionByName(rpcPermission.getName());
                        if(permission == null) {
                            SLF4JLoggerProxy.warn(AdminRpcService.this,
                                                  "Skipping unknown permission {}",
                                                  rpcPermission.getName());
                        } else {
                            newRole.getPermissions().add(permission);
                        }
                    }
                    for(AdminRpc.User rpcUser : rpcRole.getUserList()) {
                        User user = userService.findByName(rpcUser.getName());
                        if(user == null) {
                            SLF4JLoggerProxy.warn(AdminRpcService.this,
                                                  "Skipping unknown user {}",
                                                  rpcUser.getName());
                        } else {
                            newRole.getSubjects().add(user);
                        }
                    }
                    newRole = authzService.save(newRole);
                    AdminRpcUtil.getRpcRole(newRole).ifPresent(value->responseBuilder.setRole(value));
                }
                AdminRpc.CreateRoleResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#readRoles(com.marketcetera.admin.AdminRpc.ReadRolesRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void readRoles(ReadRolesRequest inRequest,
                              StreamObserver<ReadRolesResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received read roles request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.ReadRolesResponse.Builder responseBuilder = AdminRpc.ReadRolesResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ReadRoleAction.name());
                PageRequest pageRequest = inRequest.hasPage()?PagingRpcUtil.getPageRequest(inRequest.getPage()):PageRequest.ALL;
                CollectionPageResponse<Role> rolePage = authzService.findAllRoles(pageRequest);
                rolePage.getElements().forEach(role->AdminRpcUtil.getRpcRole(role).ifPresent(rpcRole->responseBuilder.addRole(rpcRole)));
                responseBuilder.setPage(PagingRpcUtil.getPageResponse(pageRequest,
                                                                      rolePage));
                AdminRpc.ReadRolesResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#updateRole(com.marketcetera.admin.AdminRpc.UpdateRoleRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void updateRole(UpdateRoleRequest inRequest,
                               StreamObserver<UpdateRoleResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received update role request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.UpdateRoleResponse.Builder responseBuilder = AdminRpc.UpdateRoleResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.UpdateRoleAction.name());
                Role existingRole = authzService.findRoleByName(inRequest.getRoleName());
                Validate.isTrue(existingRole != null,
                                "Unknown role: " + inRequest.getRoleName());
                if(inRequest.hasRole()) {
                    AdminRpc.Role rpcRole = inRequest.getRole();
                    if(existingRole instanceof MutableRole) {
                        MutableRole mutableRole = (MutableRole)existingRole;
                        mutableRole.getPermissions().clear();
                        mutableRole.getSubjects().clear();
                        for(AdminRpc.Permission rpcPermission : rpcRole.getPermissionList()) {
                            Permission permission = authzService.findPermissionByName(rpcPermission.getName());
                            if(permission == null) {
                                SLF4JLoggerProxy.warn(AdminRpcService.this,
                                                      "Skipping unknown permission {}",
                                                      rpcPermission.getName());
                            } else {
                                mutableRole.getPermissions().add(permission);
                            }
                        }
                        for(AdminRpc.User rpcUser : rpcRole.getUserList()) {
                            User user = userService.findByName(rpcUser.getName());
                            if(user == null) {
                                SLF4JLoggerProxy.warn(AdminRpcService.this,
                                                      "Skipping unknown user {}",
                                                      rpcUser.getName());
                            } else {
                                mutableRole.getSubjects().add(user);
                            }
                        }
                        mutableRole.setDescription(StringUtils.trimToNull(inRequest.getRole().getDescription()));
                        mutableRole.setName(StringUtils.trimToNull(inRequest.getRole().getName()));
                        existingRole = authzService.save(mutableRole);
                        AdminRpcUtil.getRpcRole(existingRole).ifPresent(value->responseBuilder.setRole(value));
                    } else {
                        throw new IllegalStateException("User attribute service returned a non-mutable user attribute - check configuration");
                    }
                }
                AdminRpc.UpdateRoleResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#deleteRole(com.marketcetera.admin.AdminRpc.DeleteRoleRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteRole(DeleteRoleRequest inRequest,
                               StreamObserver<DeleteRoleResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received delete role request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.DeleteRoleResponse.Builder responseBuilder = AdminRpc.DeleteRoleResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.DeleteRoleAction.name());
                Role existingRole = authzService.findRoleByName(inRequest.getRoleName());
                Validate.isTrue(existingRole != null,
                                "Unknown role: " + inRequest.getRoleName());
                authzService.deleteRole(inRequest.getRoleName());
                AdminRpc.DeleteRoleResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#readUserAttribute(com.marketcetera.admin.AdminRpc.ReadUserAttributeRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void readUserAttribute(ReadUserAttributeRequest inRequest,
                                      StreamObserver<ReadUserAttributeResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received read user attribute request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.ReadUserAttributeResponse.Builder responseBuilder = AdminRpc.ReadUserAttributeResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ReadUserAttributeAction.name());
                UserAttributeType userAttributeType = AdminRpcUtil.getUserAttributeType(inRequest.getAttributeType()).orElse(null);
                User user = userService.findByName(inRequest.getUsername());
                if(user == null) {
                    throw new IllegalArgumentException("Unknown user: '" + inRequest.getUsername()+"'");
                }
                UserAttribute userAttribute = userAttributeService.getUserAttribute(user,
                                                                                    userAttributeType);
                AdminRpcUtil.getRpcUserAttribute(userAttribute).ifPresent(value->responseBuilder.setUserAttribute(value));
                AdminRpc.ReadUserAttributeResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceImplBase#writeUserAttribute(com.marketcetera.admin.AdminRpc.WriteUserAttributeRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void writeUserAttribute(WriteUserAttributeRequest inRequest,
                                       StreamObserver<WriteUserAttributeResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Received write user attribute request {} from {}",
                                       inRequest,
                                       sessionHolder);
                AdminRpc.WriteUserAttributeResponse.Builder responseBuilder = AdminRpc.WriteUserAttributeResponse.newBuilder();
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.WriteUserAttributeAction.name());
                UserAttributeType userAttributeType = AdminRpcUtil.getUserAttributeType(inRequest.getAttributeType()).orElse(null);
                User user = userService.findByName(inRequest.getUsername());
                if(user == null) {
                    throw new IllegalArgumentException("Unknown user: '" + inRequest.getUsername()+"'");
                }
                UserAttribute userAttribute = userAttributeService.getUserAttribute(user,
                                                                                    userAttributeType);
                String attributeValue = StringUtils.trimToNull(inRequest.getAttribute());
                if(attributeValue == null) {
                    if(userAttribute != null) {
                        userAttributeService.delete(userAttribute);
                    }
                } else {
                    if(userAttribute == null) {
                        userAttribute = userAttributeFactory.create(user,
                                                                    userAttributeType,
                                                                    inRequest.getAttribute());
                    } else {
                        if(userAttribute instanceof MutableUserAttribute) {
                            MutableUserAttribute mutableUserAttribute = (MutableUserAttribute)userAttribute;
                            mutableUserAttribute.setAttribute(inRequest.getAttribute());
                        } else {
                            throw new IllegalStateException("User attribute service returned a non-mutable user attribute - check configuration");
                        }
                    }
                    userAttribute = userAttributeService.save(userAttribute);
                }
                AdminRpc.WriteUserAttributeResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                StatusRuntimeException sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
                inResponseObserver.onError(sre);
                throw sre;
            }
        }
//        /* (non-Javadoc)
//         * @see com.marketcetera.fix.ClusterRpcServiceGrpc.ClusterRpcServiceImplBase#getInstanceData(com.marketcetera.fix.ClusterRpc.InstanceDataRequest, io.grpc.stub.StreamObserver)
//         */
//        @Override
//        public void getInstanceData(InstanceDataRequest inRequest,
//                                    StreamObserver<InstanceDataResponse> inResponseObserver)
//        {
//            try {
//                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
//                SLF4JLoggerProxy.trace(ClusterRpcService.this,
//                                       "Received get instance data {} from {}",
//                                       inRequest,
//                                       sessionHolder);
//                authzService.authorize(sessionHolder.getUser(),
//                                       AdminPermissions.ReadInstanceDataAction.name());
//                ClusterRpc.InstanceDataResponse.Builder responseBuilder = ClusterRpc.InstanceDataResponse.newBuilder();
//                            AdminRpc.InstanceData.Builder instanceDataBuilder = AdminRpc.InstanceData.newBuilder();
//                            AcceptorSessionAttributes acceptorSessionAttributes = brokerService.getFixSettingsFor(inRequest.getAffinity());
//                            if(acceptorSessionAttributes.getHost() != null) {
//                                instanceDataBuilder.setHostname(acceptorSessionAttributes.getHost());
//                            }
//                            instanceDataBuilder.setPort(acceptorSessionAttributes.getPort());
//                            responseBuilder.setInstanceData(instanceDataBuilder.build());
//                                
////                AcceptorSessionAttributes acceptorSessionAttributes = brokerService.getFixSettingsFor(inRequest.getAffinity());
////                responseBuilder.setInstanceData(ClusterRpcUtil.getRpcInstanceData(acceptorSessionAttributes));
////                ClusterRpc.InstanceDataResponse response = responseBuilder.build();
////                SLF4JLoggerProxy.trace(ClusterRpcService.this,
////                                       "Returning {}",
////                                       response);
////                inResponseObserver.onNext(response);
////                inResponseObserver.onCompleted();
//                throw new UnsupportedOperationException();
//            } catch (Exception e) {
//                if(e instanceof StatusRuntimeException) {
//                    throw (StatusRuntimeException)e;
//                }
//                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
//            }
//        }
    }
    /**
     * provides the RPC service
     */
    private Service service;
    /**
     * creates {@link UserAttribute} objects
     */
    @Autowired
    private UserAttributeFactory userAttributeFactory;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * provides access to user attribute services
     */
    @Autowired
    private UserAttributeService userAttributeService;
    /**
     * creates {@link Permission} objects
     */
    @Autowired
    private PermissionFactory permissionFactory;
    /**
     * creates {@link Role} objects
     */
    @Autowired
    private RoleFactory roleFactory;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * description of the service
     */
    private static final String DESCRIPTION = "Admin RPC Service"; //$NON-NLS-1$
}
