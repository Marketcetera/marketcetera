package org.marketcetera.admin.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserAttributeFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.core.PlatformServices;

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
        user = generateUser(user.getName(),
                            null);
        rpcUser = AdminRpcUtil.getRpcUser(user).orElse(null);
        assertNotNull(rpcUser);
        verifyRpcUser(user,
                      rpcUser);
        user = generateUser(null,
                            generateString());
        rpcUser = AdminRpcUtil.getRpcUser(user).orElse(null);
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
     * Test {@link AdminRpcUtil#getPermission(com.marketcetera.admin.AdminRpc.Permission, PermissionFactory)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetPermission()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getPermission(null,
                                               permissionFactory).isPresent());
        AdminRpc.Permission rpcPermission = generateRpcPermission();
        Permission permission = AdminRpcUtil.getPermission(rpcPermission,
                                                           permissionFactory).orElse(null);
        assertNotNull(permission);
        verifyPermission(rpcPermission,
                         permission);
    }
    /**
     * Test {@link AdminRpcUtil#getRpcPermission(Permission)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcPermission()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getRpcPermission(null).isPresent());
        Permission permission = generatePermission();
        AdminRpc.Permission rpcPermission = AdminRpcUtil.getRpcPermission(permission).orElse(null);
        assertNotNull(rpcPermission);
        verifyRpcPermission(permission,
                            rpcPermission);
    }
    /**
     * Test {@link AdminRpcUtil#getUserAttribute(com.marketcetera.admin.AdminRpc.UserAttribute)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetUserAttribute()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getUserAttribute(null,
                                                  userAttributeFactory,
                                                  userFactory).isPresent());
        AdminRpc.UserAttribute rpcUserAttribute = generateRpcUserAttribute();
        UserAttribute userAttribute = AdminRpcUtil.getUserAttribute(rpcUserAttribute,
                                                                    userAttributeFactory,
                                                                    userFactory).orElse(null);
        assertNotNull(userAttribute);
        verifyUserAttribute(rpcUserAttribute,
                            userAttribute);
    }
    /**
     * Test {@link AdminRpcUtil#getRpcUserAttribute(UserAttribute)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcUserAttribute()
            throws Exception
    {
        assertFalse(AdminRpcUtil.getRpcUserAttribute(null).isPresent());
        UserAttribute userAttribute = generateUserAttribute();
        AdminRpc.UserAttribute rpcUserAttribute = AdminRpcUtil.getRpcUserAttribute(userAttribute).orElse(null);
        assertNotNull(rpcUserAttribute);
        verifyRpcUserAttribute(userAttribute,
                               rpcUserAttribute);
    }
    /**
     * Verify the given actual user has the given expected values.
     *
     * @param inExpectedUser an <code>AdminRpc.User</code> value
     * @param inActualUser a <code>User</code> value
     */
    public static void verifyUser(AdminRpc.User inExpectedUser,
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
     * @param inActualUser a <code>User</code> value
     */
    public static void verifyModelUser(User inExpectedUser,
                                       User inActualUser)
    {
        assertEquals(inExpectedUser.getName(),
                     inActualUser.getName());
        assertEquals(inExpectedUser.getDescription(),
                     inActualUser.getDescription());
        assertEquals(inExpectedUser.isActive(),
                     inActualUser.isActive());
    }
    /**
     * Verify the given actual user has the given expected values.
     *
     * @param inExpectedUser a <code>User</code> value
     * @param inActualUser an <code>AdminRpc.User</code> value
     */
    public static void verifyRpcUser(User inExpectedUser,
                                     AdminRpc.User inActualUser)
    {
        assertEquals(inExpectedUser.getName(),
                     StringUtils.trimToNull(inActualUser.getName()));
        assertEquals(inExpectedUser.getDescription(),
                     StringUtils.trimToNull(inActualUser.getDescription()));
        assertEquals(inExpectedUser.isActive(),
                     inActualUser.getActive());
    }
    /**
     * Generate an RPC user with random values.
     *
     * @return an <code>AdminRpc.User</code> value
     */
    public static AdminRpc.User generateRpcUser()
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
    public static User generateUser()
    {
        return generateUser(generateString(),
                            generateString());
    }
    /**
     * Generate a user with random values and the given password.
     *
     * @param inPassword a <code>String</code> value
     * @return a <code>User</code> value
     */
    public static User generateUser(String inPassword)
    {
        return generateUser(generateString(),
                            inPassword,
                            generateString());
    }
    /**
     * Generate a user with the given values.
     *
     * @param inUsername a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>User</code> value
     */
    public static User generateUser(String inUsername,
                                    String inDescription)
    {
        return generateUser(inUsername,
                            generateString(),
                            inDescription);
    }
    /**
     * Generate a user with the given values.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>User</code> value
     */
    public static User generateUser(String inUsername,
                                    String inPassword,
                                    String inDescription)
    {
        return userFactory.create(inUsername,
                                  inPassword,
                                  inDescription,
                                  true);
    }
    /**
     * Generate a permission with random values.
     *
     * @return a <code>Permission</code> value
     */
    public static Permission generatePermission()
    {
        return permissionFactory.create(generateString(),
                                        generateString());
    }
    /**
     * Generate an RPC permission with random values.
     *
     * @return an <code>AdminRpc.Permission</code> value
     */
    public static AdminRpc.Permission generateRpcPermission()
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
    public static Role generateRole(int inUserCount,
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
    public static AdminRpc.Role generateRpcRole(int inUserCount,
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
    public static void verifyRole(AdminRpc.Role inExpectedRole,
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
     * Verify the expected role matches the actual role.
     *
     * @param inExpectedRole a <code>Role</code> value
     * @param inActualRole a <code>Role</code> value
     */
    public static void verifyModelRole(Role inExpectedRole,
                                       Role inActualRole)
    {
        assertEquals(inExpectedRole.getDescription(),
                     inActualRole.getDescription());
        assertEquals(inExpectedRole.getName(),
                     inActualRole.getName());
        verifyModelPermissions(inExpectedRole.getPermissions(),
                               inActualRole.getPermissions());
        verifyModelUsers(inExpectedRole.getSubjects(),
                         inActualRole.getSubjects());
    }
    /**
     * Verify the given permission.
     *
     * @param inExpectedPermission an <code>AdminRpc.Permission</code> value
     * @param inActualPermission a <code>Permission</code> value
     */
    public static void verifyPermission(AdminRpc.Permission inExpectedPermission,
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
     * @param inActualPermission a <code>Permission</code> value
     */
    public static void verifyModelPermission(Permission inExpectedPermission,
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
    public static void verifyRpcPermission(Permission inActualPermission,
                                           AdminRpc.Permission inExpectedPermission)
    {
        assertEquals(inActualPermission.getName(),
                     inExpectedPermission.getName());
        assertEquals(inActualPermission.getDescription(),
                     inExpectedPermission.getDescription());
    }
    /**
     * Verify the given user attribute.
     *
     * @param inExpectedUserAttribute an <code>AdminRpc.UserAttribute</code> value
     * @param inActualUserAttribute a <code>UserAttribute</code> value
     */
    public static void verifyUserAttribute(AdminRpc.UserAttribute inExpectedUserAttribute,
                                           UserAttribute inActualUserAttribute)
    {
        assertEquals(inExpectedUserAttribute.getAttribute(),
                     inActualUserAttribute.getAttribute());
        verifyUserAttributeType(inExpectedUserAttribute.getAttributeType(),
                                inActualUserAttribute.getAttributeType());
        verifyUser(inExpectedUserAttribute.getUser(),
                   inActualUserAttribute.getUser());
    }
    /**
     * Verify the given user attribute value.
     *
     * @param inExpectedUserAttributeType an <code>AdminRpc.UserAttribute</code> value
     * @param inActualUserAttributeType a <code>UserAttribute</code> value
     */
    public static void verifyUserAttributeType(AdminRpc.UserAttributeType inExpectedUserAttributeType,
                                               UserAttributeType inActualUserAttributeType)
    {
        switch(inExpectedUserAttributeType) {
            case DisplayLayoutUserAttributeType:
                assertEquals(UserAttributeType.DISPLAY_LAYOUT,
                             inActualUserAttributeType);
                break;
            case StrategyEnginesUserAttributeType:
                assertEquals(UserAttributeType.STRATEGY_ENGINES,
                             inActualUserAttributeType);
                break;
            case UNRECOGNIZED:
            case UnknownUserAttributeType:
            default:
                throw new UnsupportedOperationException(inExpectedUserAttributeType.name());
        }
    }
    /**
     * Verify the given user attribute.
     *
     * @param inExpectedUserAttribute a <code>UserAttribute</code> value
     * @param inActualUserAttribute an <code>AdminRpc.UserAttribute</code> value
     */
    public static void verifyRpcUserAttribute(UserAttribute inExpectedUserAttribute,
                                              AdminRpc.UserAttribute inActualUserAttribute)
    {
        assertEquals(inExpectedUserAttribute.getAttribute(),
                     inActualUserAttribute.getAttribute());
        verifyRpcUserAttributeType(inExpectedUserAttribute.getAttributeType(),
                                   inActualUserAttribute.getAttributeType());
        verifyRpcUser(inExpectedUserAttribute.getUser(),
                      inActualUserAttribute.getUser());
    }
    /**
     * Verify the given user attribute type.
     *
     * @param inExpectedUserAttributeType a <code>UserAttributeType</code> value
     * @param inActualUserAttributeType an <code>AdminRpc.UserAttributeType</code> value
     */
    public static void verifyRpcUserAttributeType(UserAttributeType inExpectedUserAttributeType,
                                                  AdminRpc.UserAttributeType inActualUserAttributeType)
    {
        switch(inExpectedUserAttributeType) {
            case DISPLAY_LAYOUT:
                assertEquals(AdminRpc.UserAttributeType.DisplayLayoutUserAttributeType,
                             inActualUserAttributeType);
                break;
            case STRATEGY_ENGINES:
                assertEquals(AdminRpc.UserAttributeType.StrategyEnginesUserAttributeType,
                             inActualUserAttributeType);
                break;
            default:
                throw new UnsupportedOperationException(inExpectedUserAttributeType.name());
        }
    }
    /**
     * Generate a user attribute with random values.
     *
     * @return a <code>UserAttribute</code> value
     */
    public static UserAttribute generateUserAttribute()
    {
        return userAttributeFactory.create(generateUser(),
                                           UserAttributeType.STRATEGY_ENGINES,
                                           generateString());
    }
    /**
     * Generate an RPC user attribute with random values.
     *
     * @return an <code>AdminRpc.UserAttribute</code> value
     */
    public static AdminRpc.UserAttribute generateRpcUserAttribute()
    {
        AdminRpc.UserAttribute.Builder builder = AdminRpc.UserAttribute.newBuilder();
        builder.setAttribute(generateString());
        builder.setAttributeType(AdminRpc.UserAttributeType.DisplayLayoutUserAttributeType);
        builder.setUser(generateRpcUser());
        return builder.build();
    }
    /**
     * Verify the given RPC role.
     *
     * @param inExpectedRole a <code>Role</code> value
     * @param inActualRole an <code>AdminRpc.Role</code> value
     */
    public static void verifyRpcRole(Role inExpectedRole,
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
    public static void verifyRpcPermissions(Collection<Permission> inExpectedPermissions,
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
    public static void verifyPermissions(Collection<AdminRpc.Permission> inExpectedPermissions,
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
     * Verify the given permissions.
     *
     * @param inExpectedPermissions a <code>Collection&lt;Permission</code> value
     * @param inActualPermissions a <code>Collection&lt;Permission&gt;</code> value
     */
    public static void verifyModelPermissions(Collection<Permission> inExpectedPermissions,
                                              Collection<Permission> inActualPermissions)
    {
        assertEquals(inExpectedPermissions.size(),
                     inActualPermissions.size());
        Map<String,Permission> expectedPermissions = inExpectedPermissions.stream().collect(Collectors.toMap(Permission::getName,
                                                                                                             Function.identity()));
        Map<String,Permission> actualPermissions = inActualPermissions.stream().collect(Collectors.toMap(Permission::getName,
                                                                                                         Function.identity()));
        for(Map.Entry<String,Permission> entry : actualPermissions.entrySet()) {
            Permission expectedPermission = expectedPermissions.get(entry.getKey());
            assertNotNull("No expected permission named '" + entry.getKey() + "'",
                          expectedPermission);
            verifyModelPermission(entry.getValue(),
                                  expectedPermission);
        }
    }
    /**
     * Verify the given RPC users.
     *
     * @param inExpectedUsers a <code>Collection&lt;User&gt;</code> value
     * @param inActualUsers a <code>Collection&lt;AdminRpc.User</code> value
     */
    public static void verifyRpcUsers(Collection<User> inExpectedUsers,
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
    public static void verifyUsers(Collection<AdminRpc.User> inExpectedUsers,
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
     * Verify the given users.
     *
     * @param inExpectedUsers a <code>Collection&lt;User</code> value
     * @param inActualUsers a <code>Collection&lt;User&gt;</code> value
     */
    public static void verifyModelUsers(Collection<User> inExpectedUsers,
                                        Collection<User> inActualUsers)
    {
        assertEquals(inExpectedUsers.size(),
                     inActualUsers.size());
        Map<String,User> expectedUsers = inExpectedUsers.stream().collect(Collectors.toMap(User::getName,
                                                                                           Function.identity()));
        Map<String,User> actualUsers = inActualUsers.stream().collect(Collectors.toMap(User::getName,
                                                                                       Function.identity()));
        for(Map.Entry<String,User> entry : actualUsers.entrySet()) {
            User expectedUser = expectedUsers.get(entry.getKey());
            assertNotNull("No expected user named '" + entry.getKey() + "'",
                          expectedUser);
            verifyModelUser(entry.getValue(),
                            expectedUser);
        }
    }
    /**
     * Generate a string value.
     *
     * @return a <code>String</code> value
     */
    private static String generateString()
    {
        return PlatformServices.generateId();
    }
    /**
     * creates {@link User} objects
     */
    private static UserFactory userFactory = new SimpleUserFactory();
    /**
     * creates {@link Role} objects
     */
    private static RoleFactory roleFactory = new SimpleRoleFactory();
    /**
     * creates {@link Permission} objects
     */
    private static PermissionFactory permissionFactory = new SimplePermissionFactory();
    /**
     * creates {@link UserAttribute} objects
     */
    private static UserAttributeFactory userAttributeFactory = new SimpleUserAttributeFactory();
}
