package com.marketcetera.admin;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.service.PasswordService;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceBlockingStub;
import com.marketcetera.admin.AdminRpcServiceGrpc.AdminRpcServiceStub;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC-based {@link AdminClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcClient
        extends AbstractRpcClient<AdminRpcServiceBlockingStub,AdminRpcServiceStub,AdminRpcClientParameters>
        implements AdminClient
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#getPermissionsForUsername()
     */
    @Override
    public Set<String> getPermissionsForCurrentUser()
    {
        return executeCall(new Callable<Set<String>>() {
            @Override
            public Set<String> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} getting permissions for self",
                                       getSessionId());
                AdminRpc.PermissionsForUsernameRequest.Builder requestBuilder = AdminRpc.PermissionsForUsernameRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                AdminRpc.PermissionsForUsernameRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.PermissionsForUsernameResponse response = getBlockingStub().getPermissionsForUsername(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Set<String> results = Sets.newHashSet(response.getPermissionsList());
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#changeUserPassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void changeUserPassword(String inUsername,
                                   String inOldPassword,
                                   String inNewPassword)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} changing password for {}",
                                       getSessionId(),
                                       inUsername);
                AdminRpc.ChangeUserPasswordRequest.Builder requestBuilder = AdminRpc.ChangeUserPasswordRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                String value = StringUtils.trimToNull(inUsername);
                if(value != null) {
                    requestBuilder.setUsername(value);
                }
                value = StringUtils.trimToNull(inOldPassword);
                if(value != null) {
                    requestBuilder.setOldPassword(passwordService.getHash(inOldPassword));
                }
                value = StringUtils.trimToNull(inNewPassword);
                if(value != null) {
                    requestBuilder.setNewPassword(passwordService.getHash(inNewPassword));
                }
                AdminRpc.ChangeUserPasswordRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending request",
                                       getSessionId());
                AdminRpc.ChangeUserPasswordResponse response = getBlockingStub().changeUserPassword(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#createUser(com.marketcetera.admin.User, java.lang.String)
     */
    @Override
    public User createUser(User inNewUser,
                           String inPassword)
    {
        return executeCall(new Callable<User>() {
            @Override
            public User call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} creating new user {}",
                                       getSessionId(),
                                       inNewUser);
                AdminRpc.CreateUserRequest.Builder requestBuilder = AdminRpc.CreateUserRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setUser(AdminUtil.getRpcUser(inNewUser));
                requestBuilder.setPassword(passwordService.getHash(inPassword));
                AdminRpc.CreateUserRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.CreateUserResponse response = getBlockingStub().createUser(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                User result = AdminUtil.getUser(response.getUser(),
                                                userFactory);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#updateUser(java.lang.String, com.marketcetera.admin.User)
     */
    @Override
    public User updateUser(String inUsername,
                           User inUser)
    {
        return executeCall(new Callable<User>() {
            @Override
            public User call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} updating user {} {}",
                                       getSessionId(),
                                       inUsername,
                                       inUser);
                AdminRpc.UpdateUserRequest.Builder requestBuilder = AdminRpc.UpdateUserRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setUser(AdminUtil.getRpcUser(inUser));
                requestBuilder.setUsername(inUsername);
                AdminRpc.UpdateUserRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.UpdateUserResponse response = getBlockingStub().updateUser(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                User result = AdminUtil.getUser(response.getUser(),
                                                userFactory);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#deleteUser(java.lang.String)
     */
    @Override
    public void deleteUser(String inUsername)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} deleting user {}",
                                       getSessionId(),
                                       inUsername);
                AdminRpc.DeleteUserRequest.Builder requestBuilder = AdminRpc.DeleteUserRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                String value = StringUtils.trimToNull(inUsername);
                if(value != null) {
                    requestBuilder.setUsername(value);
                }
                AdminRpc.DeleteUserRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending request",
                                       getSessionId());
                AdminRpc.DeleteUserResponse response = getBlockingStub().deleteUser(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#deactivateUser(java.lang.String)
     */
    @Override
    public void deactivateUser(String inUsername)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} deactivating user {}",
                                       getSessionId(),
                                       inUsername);
                AdminRpc.DeactivateUserRequest.Builder requestBuilder = AdminRpc.DeactivateUserRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                String value = StringUtils.trimToNull(inUsername);
                if(value != null) {
                    requestBuilder.setUsername(value);
                }
                AdminRpc.DeactivateUserRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending request",
                                       getSessionId());
                AdminRpc.DeactivateUserResponse response = getBlockingStub().deactivateUser(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#createRole(com.marketcetera.admin.Role)
     */
    @Override
    public Role createRole(Role inRole)
    {
        return executeCall(new Callable<Role>() {
            @Override
            public Role call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} creating new Role {}",
                                       getSessionId(),
                                       inRole);
                AdminRpc.CreateRoleRequest.Builder requestBuilder = AdminRpc.CreateRoleRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setRole(AdminUtil.getRpcRole(inRole));
                AdminRpc.CreateRoleRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.CreateRoleResponse response = getBlockingStub().createRole(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Role result = AdminUtil.getRole(response.getRole(),
                                                roleFactory);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readRoles()
     */
    @Override
    public List<Role> readRoles()
    {
        return Lists.newArrayList(readRoles(new PageRequest(0,Integer.MAX_VALUE)).getElements());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readRoles(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<Role> readRoles(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<Role>>() {
            @Override
            public CollectionPageResponse<Role> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} reading roles {}",
                                       getSessionId(),
                                       inPageRequest);
                AdminRpc.ReadRolesRequest.Builder requestBuilder = AdminRpc.ReadRolesRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPage(PagingUtil.buildPageRequest(inPageRequest.getPageNumber(),
                                                                   inPageRequest.getPageSize()));
                AdminRpc.ReadRolesRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.ReadRolesResponse response = getBlockingStub().readRoles(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                List<Role> results = Lists.newArrayList();
                for(AdminRpc.Role rpcRole : response.getRoleList()) {
                    results.add(AdminUtil.getRole(rpcRole,
                                                  roleFactory));
                }
                CollectionPageResponse<Role> result = new CollectionPageResponse<>();
                if(response.hasPage()) {
                    PagingUtil.addPageToResponse(response.getPage(),
                                                 result);
                }
                result.setElements(results);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#updateRole(java.lang.String, com.marketcetera.admin.Role)
     */
    @Override
    public Role updateRole(String inName,
                           Role inRole)
    {
        return executeCall(new Callable<Role>() {
            @Override
            public Role call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} updating role {} {}",
                                       getSessionId(),
                                       inName,
                                       inRole);
                AdminRpc.UpdateRoleRequest.Builder requestBuilder = AdminRpc.UpdateRoleRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setRole(AdminUtil.getRpcRole(inRole));
                requestBuilder.setRoleName(inName);
                AdminRpc.UpdateRoleRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.UpdateRoleResponse response = getBlockingStub().updateRole(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Role result = AdminUtil.getRole(response.getRole(),
                                                roleFactory);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#deleteRole(java.lang.String)
     */
    @Override
    public void deleteRole(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} deleting role {}",
                                       getSessionId(),
                                       inName);
                AdminRpc.DeleteRoleRequest.Builder requestBuilder = AdminRpc.DeleteRoleRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                String value = StringUtils.trimToNull(inName);
                if(value != null) {
                    requestBuilder.setRoleName(value);
                }
                AdminRpc.DeleteRoleRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending request",
                                       getSessionId());
                AdminRpc.DeleteRoleResponse response = getBlockingStub().deleteRole(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#getUsers()
     */
    @Override
    public List<User> readUsers()
    {
        return Lists.newArrayList(readUsers(new PageRequest(0,Integer.MAX_VALUE)).getElements());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readUsers(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<User> readUsers(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<User>>() {
            @Override
            public CollectionPageResponse<User> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} reading users {}",
                                       getSessionId(),
                                       inPageRequest);
                AdminRpc.ReadUsersRequest.Builder requestBuilder = AdminRpc.ReadUsersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPage(PagingUtil.buildPageRequest(inPageRequest.getPageNumber(),
                                                                   inPageRequest.getPageSize()));
                AdminRpc.ReadUsersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.ReadUsersResponse response = getBlockingStub().readUsers(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                List<User> results = Lists.newArrayList();
                for(BaseRpc.User rpcUser : response.getUserList()) {
                    results.add(AdminUtil.getUser(rpcUser,
                                                  userFactory));
                }
                CollectionPageResponse<User> result = new CollectionPageResponse<>();
                if(response.hasPage()) {
                    PagingUtil.addPageToResponse(response.getPage(),
                                                 result);
                }
                result.setElements(results);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#createPermission(com.marketcetera.admin.Permission)
     */
    @Override
    public Permission createPermission(Permission inPermission)
    {
        return executeCall(new Callable<Permission>() {
            @Override
            public Permission call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} creating {}",
                                       getSessionId(),
                                       inPermission);
                AdminRpc.CreatePermissionRequest.Builder requestBuilder = AdminRpc.CreatePermissionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPermission(AdminUtil.getRpcPermission(inPermission));
                AdminRpc.CreatePermissionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.CreatePermissionResponse response = getBlockingStub().createPermission(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Permission result = AdminUtil.getPermission(response.getPermission(),
                                                            permissionFactory);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#readPermissions()
     */
    @Override
    public List<Permission> readPermissions()
    {
        return Lists.newArrayList(readPermissions(new PageRequest(0,Integer.MAX_VALUE)).getElements());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readPermissions(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<Permission> readPermissions(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<Permission>>() {
            @Override
            public CollectionPageResponse<Permission> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} reading permissions {}",
                                       getSessionId(),
                                       inPageRequest);
                AdminRpc.ReadPermissionsRequest.Builder requestBuilder = AdminRpc.ReadPermissionsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPage(PagingUtil.buildPageRequest(inPageRequest.getPageNumber(),
                                                                   inPageRequest.getPageSize()));
                AdminRpc.ReadPermissionsRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.ReadPermissionsResponse response = getBlockingStub().readPermissions(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                List<Permission> results = Lists.newArrayList();
                for(AdminRpc.Permission rpcPermission : response.getPermissionList()) {
                    results.add(AdminUtil.getPermission(rpcPermission,
                                                        permissionFactory));
                }
                CollectionPageResponse<Permission> result = new CollectionPageResponse<>();
                if(response.hasPage()) {
                    PagingUtil.addPageToResponse(response.getPage(),
                                                 result);
                }
                result.setElements(results);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#updatePermission(java.lang.String, com.marketcetera.admin.Permission)
     */
    @Override
    public Permission updatePermission(String inName,
                                       Permission inPermission)
    {
        return executeCall(new Callable<Permission>() {
            @Override
            public Permission call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} updating permission {} {}",
                                       getSessionId(),
                                       inName,
                                       inPermission);
                AdminRpc.UpdatePermissionRequest.Builder requestBuilder = AdminRpc.UpdatePermissionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPermission(AdminUtil.getRpcPermission(inPermission));
                requestBuilder.setPermissionName(inName);
                AdminRpc.UpdatePermissionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.UpdatePermissionResponse response = getBlockingStub().updatePermission(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Permission result = AdminUtil.getPermission(response.getPermission(),
                                                            permissionFactory);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#deletePermission(java.lang.String)
     */
    @Override
    public void deletePermission(String inPermissionName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} deleting permission {}",
                                       getSessionId(),
                                       inPermissionName);
                AdminRpc.DeletePermissionRequest.Builder requestBuilder = AdminRpc.DeletePermissionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                String value = StringUtils.trimToNull(inPermissionName);
                if(value != null) {
                    requestBuilder.setPermissionName(value);
                }
                AdminRpc.DeletePermissionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending request",
                                       getSessionId());
                AdminRpc.DeletePermissionResponse response = getBlockingStub().deletePermission(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#getUserAttribute(java.lang.String, com.marketcetera.admin.UserAttributeType)
     */
    @Override
    public UserAttribute getUserAttribute(String inUsername,
                                          UserAttributeType inAttributeType)
    {
        return executeCall(new Callable<UserAttribute>() {
            @Override
            public UserAttribute call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} getting user attribute {} for {}",
                                       getSessionId(),
                                       inAttributeType,
                                       inUsername);
                AdminRpc.ReadUserAttributeRequest.Builder requestBuilder = AdminRpc.ReadUserAttributeRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setAttributeType(AdminUtil.getRpcUserAttributeType(inAttributeType).getAttribute());
                AdminRpc.ReadUserAttributeRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.ReadUserAttributeResponse response = getBlockingStub().readUserAttribute(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                UserAttribute result = null;
                if(response.hasUserAttribute()) {
                    result = AdminUtil.getUserAttribute(response.getUserAttribute());
                }
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#setUserAttribute(java.lang.String, com.marketcetera.admin.UserAttributeType, java.lang.String)
     */
    @Override
    public void setUserAttribute(String inUsername,
                                 UserAttributeType inAttributeType,
                                 String inAttribute)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} setting user attribute {} to {} for {}",
                                       getSessionId(),
                                       inAttributeType,
                                       inAttribute,
                                       inUsername);
                AdminRpc.WriteUserAttributeRequest.Builder requestBuilder = AdminRpc.WriteUserAttributeRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setAttributeType(AdminUtil.getRpcUserAttributeType(inAttributeType).getAttribute());
                requestBuilder.setAttribute(inAttribute);
                AdminRpc.WriteUserAttributeRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                AdminRpc.WriteUserAttributeResponse response = getBlockingStub().writeUserAttribute(request);
                SLF4JLoggerProxy.trace(AdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /**
     * Validate and start the object.
     */
    @Override
    @PostConstruct
    public void start()
    {
        Validate.notNull(permissionFactory,
                         "Permission factory required");
        Validate.notNull(userFactory,
                         "User factory required");
        Validate.notNull(roleFactory,
                         "Role factory required");
        Validate.notNull(userAttributeFactory,
                         "User attribute factory required");
        Validate.notNull(passwordService,
                         "User attribute factory required");
        try {
            super.start();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the permissionFactory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    public PermissionFactory getPermissionFactory()
    {
        return permissionFactory;
    }
    /**
     * Sets the permissionFactory value.
     *
     * @param inPermissionFactory a <code>PermissionFactory</code> value
     */
    public void setPermissionFactory(PermissionFactory inPermissionFactory)
    {
        permissionFactory = inPermissionFactory;
    }
    /**
     * Get the roleFactory value.
     *
     * @return a <code>RoleFactory</code> value
     */
    public RoleFactory getRoleFactory()
    {
        return roleFactory;
    }
    /**
     * Sets the roleFactory value.
     *
     * @param inRoleFactory a <code>RoleFactory</code> value
     */
    public void setRoleFactory(RoleFactory inRoleFactory)
    {
        roleFactory = inRoleFactory;
    }
    /**
     * Get the userFactory value.
     *
     * @return a <code>UserFactory</code> value
     */
    public UserFactory getUserFactory()
    {
        return userFactory;
    }
    /**
     * Sets the userFactory value.
     *
     * @param inUserFactory a <code>UserFactory</code> value
     */
    public void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * Get the userAttributeFactory value.
     *
     * @return a <code>UserAttributeFactory</code> value
     */
    public UserAttributeFactory getUserAttributeFactory()
    {
        return userAttributeFactory;
    }
    /**
     * Sets the userAttributeFactory value.
     *
     * @param inUserAttributeFactory a <code>UserAttributeFactory</code> value
     */
    public void setUserAttributeFactory(UserAttributeFactory inUserAttributeFactory)
    {
        userAttributeFactory = inUserAttributeFactory;
    }
    /**
     * Get the passwordService value.
     *
     * @return a <code>PasswordService</code> value
     */
    public PasswordService getPasswordService()
    {
        return passwordService;
    }
    /**
     * Sets the passwordService value.
     *
     * @param inPasswordService a <code>PasswordService</code> value
     */
    public void setPasswordService(PasswordService inPasswordService)
    {
        passwordService = inPasswordService;
    }
    /**
     * Create a new AdminRpcClient instance.
     *
     * @param inParameters an <code>AdminRpcClientParameters</code> value
     */
    AdminRpcClient(AdminRpcClientParameters inParameters)
    {
        super(inParameters);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected AdminRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return AdminRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected AdminRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return AdminRpcServiceGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogin(org.marketcetera.rpc.base.BaseRpc.LoginRequest)
     */
    @Override
    protected LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest)
     */
    @Override
    protected LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeHeartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    protected void executeHeartbeat(HeartbeatRequest inRequest,
                                    StreamObserver<BaseRpc.HeartbeatResponse> inObserver)
    {
        getAsyncStub().heartbeat(inRequest,
                                 inObserver);
    }
    /**
     * provides password services
     */
    @Autowired
    private PasswordService passwordService;
    /**
     * creates {@link UserAttributeFactory} objects
     */
    @Autowired
    private UserAttributeFactory userAttributeFactory;
    /**
     * creates {@link Permission} objects
     */
    @Autowired
    private PermissionFactory permissionFactory;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * creates {@link Role} objects
     */
    @Autowired
    private RoleFactory roleFactory;
    /**
     * The client's application ID: the application name.
     */
    public static final String APP_ID_NAME = "AdminRpcClient"; //$NON-NLS-1$
    /**
     * The client's application ID: the version.
     */
    public static final VersionInfo APP_ID_VERSION = ApplicationVersion.getVersion(AdminClient.class);
    /**
     * The client's application ID: the ID.
     */
    public static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
}
