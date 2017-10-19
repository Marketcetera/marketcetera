package org.marketcetera.admin.rpc;

import java.util.Optional;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.UserFactory;

import com.marketcetera.admin.AdminRpc;

/* $License$ */

/**
 * Provides utilities for handling admin RPC data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AdminRpcUtil
{
    /**
     * Get the user from the given RPC value.
     *
     * @param inUser an <code>AdminRpc.User</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return an <code>Optional&lt;User&gt;</code> value
     */
    public static Optional<User> getUser(AdminRpc.User inUser,
                                         UserFactory inUserFactory)
    {
        User user = null;
        if(inUser != null) {
            user = inUserFactory.create(inUser.getName(),
                                        "********",
                                        inUser.getDescription(),
                                        inUser.getActive());
        }
        return user==null?Optional.empty():Optional.of(user);
    }
    /**
     * Get the RPC user from the given value.
     *
     * @param inUser a <code>User</code> value
     * @return an <code>Optional&lt;AdminRpc.User&gt;</code> value
     */
    public static Optional<AdminRpc.User> getRpcUser(User inUser)
    {
        if(inUser == null) {
            return Optional.empty();
        }
        AdminRpc.User.Builder userBuilder = AdminRpc.User.newBuilder();
        if(inUser.getDescription() != null) {
            userBuilder.setDescription(inUser.getDescription());
        }
        if(inUser.getName() != null) {
            userBuilder.setName(inUser.getName());
        }
        userBuilder.setActive(inUser.isActive());
        return Optional.of(userBuilder.build());
    }
    /**
     * Get an RPC role from the given value.
     *
     * @param inRole a <code>Role</code> value
     * @return an <code>Optional&lt;AdminRpc.Role&gt;</code> value
     */
    public static Optional<AdminRpc.Role> getRpcRole(Role inRole)
    {
        if(inRole == null) {
            return Optional.empty();
        }
        AdminRpc.Role.Builder roleBuilder = AdminRpc.Role.newBuilder();
        if(inRole.getDescription() != null) {
            roleBuilder.setDescription(inRole.getDescription());
        }
        if(inRole.getName() != null) {
            roleBuilder.setName(inRole.getName());
        }
        for(Permission permission : inRole.getPermissions()) {
            getRpcPermission(permission).ifPresent(value->roleBuilder.addPermission(value));
        }
        for(User subject : inRole.getSubjects()) {
            getRpcUser(subject).ifPresent(value->roleBuilder.addUser(value));
        }
        return Optional.of(roleBuilder.build());
    }
    /**
     * Get the role from the given values.
     *
     * @param inRole an <code>AdminRpc.Role</code> value
     * @param inRoleFactory a <code>RoleFactory</code> value
     * @param inPermissionFactory a <code>PermissionFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return an <code>Optional&lt;Role&gt;</code> value
     */
    public static Optional<Role> getRole(AdminRpc.Role inRole,
                                         RoleFactory inRoleFactory,
                                         PermissionFactory inPermissionFactory,
                                         UserFactory inUserFactory)
    {
        if(inRole == null) {
            return Optional.empty();
        }
        Role role = inRoleFactory.create(inRole.getName(),
                                         inRole.getDescription());
        for(AdminRpc.Permission rpcPermission : inRole.getPermissionList()) {
            getPermission(rpcPermission,inPermissionFactory).ifPresent(value->role.getPermissions().add(value));
        }
        for(AdminRpc.User rpcUser : inRole.getUserList()) {
            getUser(rpcUser,inUserFactory).ifPresent(value->role.getSubjects().add(value));
        }
        return Optional.of(role);
    }
    /**
     * Get the RPC permission from the given value.
     *
     * @param inPermission a <code>Permission</code> value
     * @return an <code>Optional&lt;AdminRpc.Permission&gt;</code> value
     */
    public static Optional<AdminRpc.Permission> getRpcPermission(Permission inPermission)
    {
        if(inPermission == null) {
            return Optional.empty();
        }
        AdminRpc.Permission.Builder builder = AdminRpc.Permission.newBuilder();
        if(inPermission.getDescription() != null) {
            builder.setDescription(inPermission.getDescription());
        }
        if(inPermission.getName() != null) {
            builder.setName(inPermission.getName());
        }
        return Optional.of(builder.build());
    }
    /**
     * Get the permission from the given value.
     *
     * @param inPermission an <code>AdminRpc.Permission</code> value
     * @param inPermissionFactory a <code>PermissionFactory</code> value
     * @return an <code>Optional&lt;Permission&gt;</code> value
     */
    public static Optional<Permission> getPermission(AdminRpc.Permission inPermission,
                                                     PermissionFactory inPermissionFactory)
    {
        if(inPermission == null) {
            return Optional.empty();
        }
        return Optional.of(inPermissionFactory.create(inPermission.getName(),
                                                      inPermission.getDescription()));
    }
    /**
     *
     *
     * @param inAttributeType
     * @return
     */
    public static AdminRpc.UserAttribute getRpcUserAttributeType(UserAttributeType inAttributeType)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inUserAttribute
     * @return
     */
    public static UserAttribute getUserAttribute(AdminRpc.UserAttribute inUserAttribute)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inUserAttribute
     * @return
     */
    public static AdminRpc.UserAttribute getRpcUserAttribute(UserAttribute inUserAttribute)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
}
