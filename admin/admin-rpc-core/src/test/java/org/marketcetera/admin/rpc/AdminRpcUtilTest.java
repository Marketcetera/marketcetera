package org.marketcetera.admin.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;

import com.marketcetera.admin.AdminRpc;

/* $License$ */

/**
 * Tests {@link AdminRpcUtil}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcUtilTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        userFactory = new SimpleUserFactory();
        roleFactory = new SimpleRoleFactory();
        permissionFactory = new SimplePermissionFactory();
    }
    /**
     * Test {@link AdminRpcUtil#getUser(com.marketcetera.admin.AdminRpc.User, org.marketcetera.admin.UserFactory)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetUser()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getUser(null,
                                         userFactory).isPresent());
        AdminRpc.User rpcUser = generateRpcUser();
        User user = AdminRpcUtil.getUser(rpcUser,
                                         userFactory).orElse(null);
        assertNotNull(user);
        verifyUser(rpcUser,
                   user);
    }
    /**
     * Test {@link AdminRpcUtil#getRpcUser(User)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcUser()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getRpcUser(null).isPresent());
        User user = generateUser();
        AdminRpc.User rpcUser = AdminRpcUtil.getRpcUser(user).orElse(null);
        assertNotNull(rpcUser);
        verifyRpcUser(user,
                      rpcUser);
    }
    /**
     * Test {@link AdminRpcUtil#getRpcRole(Role)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcRole()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getRpcRole(null).isPresent());
        Role role = generateRole(0,0);
        AdminRpc.Role rpcRole = AdminRpcUtil.getRpcRole(role).orElse(null);
        assertNotNull(rpcRole);
        verifyRpcRole(role,
                      rpcRole);
        role = generateRole(10,20);
        rpcRole = AdminRpcUtil.getRpcRole(role).orElse(null);
        assertNotNull(rpcRole);
        verifyRpcRole(role,
                      rpcRole);
    }
    /**
     * Test {@link AdminRpcUtil#getRole(com.marketcetera.admin.AdminRpc.Role, RoleFactory)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRole()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getRole(null,
                                         roleFactory,
                                         permissionFactory,
                                         userFactory).isPresent());
        AdminRpc.Role rpcRole = generateRpcRole(0,0);
        Role role = AdminRpcUtil.getRole(rpcRole,
                                         roleFactory,
                                         permissionFactory,
                                         userFactory).orElse(null);
        assertNotNull(role);
        verifyRole(rpcRole,
                   role);
        rpcRole = generateRpcRole(10,20);
        role = AdminRpcUtil.getRole(rpcRole,
                                    roleFactory,
                                    permissionFactory,
                                    userFactory).orElse(null);
        assertNotNull(role);
        verifyRole(rpcRole,
                   role);
    }
    /**
     * Verify the given actual user has the given expected values.
     *
     * @param inExpectedUser an <code>AdminRpc.User</code> value
     * @param inActualUser a <code>User</code> value
     */
    private void verifyUser(AdminRpc.User inExpectedUser,
                            User inActualUser)
    {
        assertEquals(inExpectedUser.getName(),
                     inActualUser.getName());
        assertEquals(inExpectedUser.getDescription(),
                     inActualUser.getDescription());
        assertEquals(inExpectedUser.getActive(),
                     inActualUser.isActive());
    }
    /**
     * Verify the given actual user has the given expected values.
     *
     * @param inExpectedUser a <code>User</code> value
     * @param inActualUser an <code>AdminRpc.User</code> value
     */
    private void verifyRpcUser(User inExpectedUser,
                               AdminRpc.User inActualUser)
    {
        assertEquals(inExpectedUser.getName(),
                     inActualUser.getName());
        assertEquals(inExpectedUser.getDescription(),
                     inActualUser.getDescription());
        assertEquals(inExpectedUser.isActive(),
                     inActualUser.getActive());
    }
    /**
     * Generate an RPC user with random values.
     *
     * @return an <code>AdminRpc.User</code> value
     */
    private AdminRpc.User generateRpcUser()
    {
        AdminRpc.User.Builder builder = AdminRpc.User.newBuilder();
        builder.setActive(true);
        builder.setDescription(generateString());
        builder.setName(generateString());
        return builder.build();
    }
    /**
     * Generate a user with random values.
     *
     * @return a <code>User</code> value
     */
    private User generateUser()
    {
        return userFactory.create(generateString(),
                                  generateString(),
                                  generateString(),
                                  true);
    }
    /**
     * Generate a permission with random values.
     *
     * @return a <code>Permission</code> value
     */
    private Permission generatePermission()
    {
        return permissionFactory.create(generateString(),
                                        generateString());
    }
    /**
     * Generate an RPC permission with random values.
     *
     * @return an <code>AdminRpc.Permission</code> value
     */
    private AdminRpc.Permission generateRpcPermission()
    {
        AdminRpc.Permission.Builder builder = AdminRpc.Permission.newBuilder();
        builder.setDescription(generateString());
        builder.setName(generateString());
        return builder.build();
    }
    /**
     * Generate a role with random values.
     *
     * @param inUserCount an <code>int</code> value
     * @param inPermissionCount an <code>int</code> value
     * @return a <code>Role</code> value
     */
    private Role generateRole(int inUserCount,
                              int inPermissionCount)
    {
        Role role = roleFactory.create(generateString(),
                                       generateString());
        for(int i=0;i<inUserCount;i++) {
            role.getSubjects().add(generateUser());
        }
        for(int i=0;i<inPermissionCount;i++) {
            role.getPermissions().add(generatePermission());
        }
        return role;
    }
    /**
     * Generate an RPC role with the given number of users and permissions.
     *
     * @param inUserCount an <code>int</code> value
     * @param inPermissionCount an <code>int</code> value
     * @return an <code>AdminRpc.Role</code> value
     */
    private AdminRpc.Role generateRpcRole(int inUserCount,
                                          int inPermissionCount)
    {
        AdminRpc.Role.Builder builder = AdminRpc.Role.newBuilder();
        builder.setDescription(generateString());
        builder.setName(generateString());
        for(int i=0;i<inUserCount;i++) {
            builder.addUser(generateRpcUser());
        }
        for(int i=0;i<inPermissionCount;i++) {
            builder.addPermission(generateRpcPermission());
        }
        return builder.build();
    }
    /**
     * Verify the given role.
     *
     * @param inExpectedRole an <code>AdminRpc.Role</code> value
     * @param inActualRole a <code>Role</code> value
     */
    private void verifyRole(AdminRpc.Role inExpectedRole,
                            Role inActualRole)
    {
        assertEquals(inExpectedRole.getDescription(),
                     inActualRole.getDescription());
        assertEquals(inExpectedRole.getName(),
                     inActualRole.getName());
        verifyPermissions(inExpectedRole.getPermissionList(),
                          inActualRole.getPermissions());
        verifyUsers(inExpectedRole.getUserList(),
                    inActualRole.getSubjects());
    }
    /**
     * Verify the given permission.
     *
     * @param inExpectedPermission an <code>AdminRpc.Permission</code> value
     * @param inActualPermission a <code>Permission</code> value
     */
    private void verifyPermission(AdminRpc.Permission inExpectedPermission,
                                  Permission inActualPermission)
    {
        assertEquals(inExpectedPermission.getName(),
                     inActualPermission.getName());
        assertEquals(inExpectedPermission.getDescription(),
                     inActualPermission.getDescription());
    }
    /**
     * Verify the given permission.
     *
     * @param inExpectedPermission a <code>Permission</code> value
     * @param inActualPermission an <code>AdminRpc.Permission</code> value
     */
    private void verifyRpcPermission(Permission inActualPermission,
                                     AdminRpc.Permission inExpectedPermission)
    {
        assertEquals(inActualPermission.getName(),
                     inExpectedPermission.getName());
        assertEquals(inActualPermission.getDescription(),
                     inExpectedPermission.getDescription());
    }
    /**
     * Verify the given RPC role.
     *
     * @param inExpectedRole a <code>Role</code> value
     * @param inActualRole an <code>AdminRpc.Role</code> value
     */
    private void verifyRpcRole(Role inExpectedRole,
                               AdminRpc.Role inActualRole)
    {
        assertEquals(inExpectedRole.getDescription(),
                     inActualRole.getDescription());
        assertEquals(inExpectedRole.getName(),
                     inActualRole.getName());
        assertEquals(inExpectedRole.getPermissions().size(),
                     inActualRole.getPermissionCount());
        verifyRpcPermissions(inExpectedRole.getPermissions(),
                             inActualRole.getPermissionList());
        verifyRpcUsers(inExpectedRole.getSubjects(),
                       inActualRole.getUserList());
    }
    /**
     * Verify the given RPC permissions.
     *
     * @param inExpectedPermissions a <code>Collection&lt;Permission&gt;</code> value
     * @param inActualPermissions a <code>Collection&lt;AdminRpc.Permission</code> value
     */
    private void verifyRpcPermissions(Collection<Permission> inExpectedPermissions,
                                      Collection<AdminRpc.Permission> inActualPermissions)
    {
        assertEquals(inExpectedPermissions.size(),
                     inActualPermissions.size());
        Map<String,Permission> permissions = inExpectedPermissions.stream().collect(Collectors.toMap(Permission::getName,
                                                                                                     Function.identity()));
        Map<String,AdminRpc.Permission> rpcPermissions = inActualPermissions.stream().collect(Collectors.toMap(AdminRpc.Permission::getName,
                                                                                                               Function.identity()));
        for(Map.Entry<String,Permission> entry : permissions.entrySet()) {
            AdminRpc.Permission rpcPermission = rpcPermissions.get(entry.getKey());
            assertNotNull("No RPC permission named '" + entry.getKey() + "'",
                          rpcPermission);
            verifyRpcPermission(entry.getValue(),
                                rpcPermission);
        }
    }
    /**
     * Verify the given permissions.
     *
     * @param inExpectedPermissions a <code>Collection&lt;AdminRpc.Permission</code> value
     * @param inActualPermissions a <code>Collection&lt;Permission&gt;</code> value
     */
    private void verifyPermissions(Collection<AdminRpc.Permission> inExpectedPermissions,
                                   Collection<Permission> inActualPermissions)
    {
        assertEquals(inExpectedPermissions.size(),
                     inActualPermissions.size());
        Map<String,AdminRpc.Permission> rpcPermissions = inExpectedPermissions.stream().collect(Collectors.toMap(AdminRpc.Permission::getName,
                                                                                                                 Function.identity()));
        Map<String,Permission> permissions = inActualPermissions.stream().collect(Collectors.toMap(Permission::getName,
                                                                                                   Function.identity()));
        for(Map.Entry<String,Permission> entry : permissions.entrySet()) {
            AdminRpc.Permission rpcPermission = rpcPermissions.get(entry.getKey());
            assertNotNull("No RPC permission named '" + entry.getKey() + "'",
                          rpcPermission);
            verifyRpcPermission(entry.getValue(),
                                rpcPermission);
        }
    }
    /**
     * Verify the given RPC users.
     *
     * @param inExpectedUsers a <code>Collection&lt;User&gt;</code> value
     * @param inActualUsers a <code>Collection&lt;AdminRpc.User</code> value
     */
    private void verifyRpcUsers(Collection<User> inExpectedUsers,
                                Collection<AdminRpc.User> inActualUsers)
    {
        assertEquals(inExpectedUsers.size(),
                     inActualUsers.size());
        Map<String,User> users = inExpectedUsers.stream().collect(Collectors.toMap(User::getName,
                                                                                   Function.identity()));
        Map<String,AdminRpc.User> rpcUsers = inActualUsers.stream().collect(Collectors.toMap(AdminRpc.User::getName,
                                                                                             Function.identity()));
        for(Map.Entry<String,User> entry : users.entrySet()) {
            AdminRpc.User rpcUser = rpcUsers.get(entry.getKey());
            assertNotNull("No RPC user named '" + entry.getKey() + "'",
                          rpcUser);
            verifyRpcUser(entry.getValue(),
                          rpcUser);
        }
    }
    /**
     * Verify the given users.
     *
     * @param inExpectedUsers a <code>Collection&lt;AdminRpc.User</code> value
     * @param inActualUsers a <code>Collection&lt;User&gt;</code> value
     */
    private void verifyUsers(Collection<AdminRpc.User> inExpectedUsers,
                                   Collection<User> inActualUsers)
    {
        assertEquals(inExpectedUsers.size(),
                     inActualUsers.size());
        Map<String,AdminRpc.User> rpcUsers = inExpectedUsers.stream().collect(Collectors.toMap(AdminRpc.User::getName,
                                                                                               Function.identity()));
        Map<String,User> users = inActualUsers.stream().collect(Collectors.toMap(User::getName,
                                                                                 Function.identity()));
        for(Map.Entry<String,User> entry : users.entrySet()) {
            AdminRpc.User rpcUser = rpcUsers.get(entry.getKey());
            assertNotNull("No RPC user named '" + entry.getKey() + "'",
                          rpcUser);
            verifyRpcUser(entry.getValue(),
                          rpcUser);
        }
    }
    /**
     * Generate a string value.
     *
     * @return a <code>String</code> value
     */
    private String generateString()
    {
        return UUID.randomUUID().toString();
    }
    /**
     * creates {@link User} objects
     */
    private UserFactory userFactory;
    /**
     * creates {@link Role} objects
     */
    private RoleFactory roleFactory;
    /**
     * creates {@link Permission} objects
     */
    private PermissionFactory permissionFactory;
}
