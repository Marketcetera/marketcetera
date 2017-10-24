package org.marketcetera.admin.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Test {@link AdminRpcService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcServerTest
        extends AdminTestBase
{
    /**
     * Test {@link AdminClient#getPermissionsForUsername(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetPermissionNames()
            throws Exception
    {
        Set<Permission> expectedPermissions = authorizationService.findAllPermissionsByUsername("test");
        assertFalse(expectedPermissions.isEmpty());
        assertEquals(expectedPermissions.size(),
                     adminClient.getPermissionsForCurrentUser().size());
    }
    /**
     * Test {@link AdminClient#readUsers()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReadUsers()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for ReadUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.readUsers();
            }
        };
        testClient.stop();
        assertEquals(4,
                     adminClient.readUsers().size());
    }
    /**
     * Test {@link AdminClient#updateUser(String, com.marketcetera.ors.security.PersistentUser)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateUser()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for UpdateUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                MutableUser updatedUser = userFactory.create();
                updatedUser.setIsActive(false);
                updatedUser.setDescription("updated description");
                updatedUser.setName("not-trade-anymore");
                testClient.updateUser("trader",
                                      updatedUser);
            }
        };
        testClient.stop();
        User user = userService.findByName("trader");
        assertNotNull(user);
        assertFalse("updated-description".equals(user.getDescription()));
        MutableUser updatedUser = userFactory.create();
        updatedUser.setDescription("New Description-" + PlatformServices.generateId());
        updatedUser.setIsActive(user.isActive());
        updatedUser.setName(user.getName());
        updatedUser.setUserId(user.getUserID());
        adminClient.updateUser(updatedUser.getName(),
                               updatedUser);
        user = userService.findByName("trader");
        assertNotNull(user);
        assertEquals(updatedUser.getDescription(),
                     user.getDescription());
    }
    // TODO create role
    // TODO read roles
    // TODO update role
    // TODO delete role
    /**
     * Test {@link AdminClient#readRoles()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReadRoles()
            throws Exception
    {
        
    }
    /**
     * Test {@link AdminClient#getUserAttribute(String, org.marketcetera.admin.UserAttributeType)} and
     * {@link AdminClient#setUserAttribute(String, UserAttributeType, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReadWriteUserAttribute()
            throws Exception
    {
        // no permission
        MutableUser newUser = userFactory.create();
        newUser.setIsActive(true);
        newUser.setDescription("description");
        newUser.setName("new user");
        adminClient.createUser(newUser,
                              "password");
        final AdminClient testClient = generateAdminClient(newUser.getName(),
                                                           "password");
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: new user is not authorized for ReadUserAttributeAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.getUserAttribute("test",
                                            UserAttributeType.DISPLAY_LAYOUT);
            }
        };
        testClient.stop();
        adminClient.deleteUser(newUser.getName());
        // write attribute that doesn't yet exist
        assertNull(adminClient.getUserAttribute("trader",
                                                UserAttributeType.DISPLAY_LAYOUT));
        String testValue = PlatformServices.generateId();
        adminClient.setUserAttribute("trader",
                                     UserAttributeType.DISPLAY_LAYOUT,
                                     testValue);
        UserAttribute userAttribute = adminClient.getUserAttribute("trader",
                                                                   UserAttributeType.DISPLAY_LAYOUT);
        assertNotNull(userAttribute);
        User traderUser = userService.findByName("trader");
        assertEquals(traderUser,
                     userAttribute.getUser());
        assertEquals(UserAttributeType.DISPLAY_LAYOUT,
                     userAttribute.getAttributeType());
        assertEquals(testValue,
                     userAttribute.getAttribute());
        // write attribute that does exist
        testValue = PlatformServices.generateId();
        adminClient.setUserAttribute("trader",
                                     UserAttributeType.DISPLAY_LAYOUT,
                                     testValue);
        userAttribute = adminClient.getUserAttribute("trader",
                                                     UserAttributeType.DISPLAY_LAYOUT);
        assertNotNull(userAttribute);
        assertEquals(traderUser,
                     userAttribute.getUser());
        assertEquals(UserAttributeType.DISPLAY_LAYOUT,
                     userAttribute.getAttributeType());
        assertEquals(testValue,
                     userAttribute.getAttribute());
        // remove value
        adminClient.setUserAttribute("trader",
                                     UserAttributeType.DISPLAY_LAYOUT,
                                     null);
        assertNull(adminClient.getUserAttribute("trader",
                                                UserAttributeType.DISPLAY_LAYOUT));
    }
    /**
     * Test {@link AdminClient#createUser(User, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreateUser()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for CreateUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                MutableUser newUser = userFactory.create();
                newUser.setIsActive(true);
                newUser.setDescription("updated description");
                newUser.setName("new user");
                testClient.createUser(newUser,
                                      "password");
            }
        };
        testClient.stop();
        User user = userService.findByName("new user");
        assertNull(user);
        MutableUser newUser = userFactory.create();
        newUser.setIsActive(true);
        newUser.setDescription("updated description");
        newUser.setName("new user");
        adminClient.createUser(newUser,
                               "password");
        user = userService.findByName("new user");
        assertNotNull(user);
        assertEquals("updated description",
                     user.getDescription());
        assertTrue(user.isActive());
        userService.delete(user);
    }
    /**
     * Test {@link AdminClient#changeUserPassword(String, String, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testChangeUserPassword()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for ChangeUserPasswordAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.changeUserPassword("trader",
                                              "trader",
                                              "new-password");
            }
        };
        testClient.stop();
        MutableUser newUser = userFactory.create();
        newUser.setIsActive(true);
        newUser.setDescription("updated description");
        newUser.setName("new user");
        adminClient.createUser(newUser,
                               "password");
        adminClient.changeUserPassword("new user",
                                       "password",
                                       "new-password");
        AdminClient newTestClient = generateAdminClient("new user",
                                                        "new-password");
        newTestClient.stop();
        User locatedUser = userService.findByName(newUser.getName());
        assertNotNull(locatedUser);
        userService.delete(locatedUser);
    }
    /**
     * Test {@link AdminClient#deleteUser(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteUser()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for DeleteUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.deleteUser("trader");
            }
        };
        // user doesn't exist
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: IllegalArgumentException: Unknown user: not-a-real-user") {
            @Override
            protected void run()
                    throws Exception
            {
                adminClient.deleteUser("not-a-real-user");
            }
        };
        MutableUser newUser = userFactory.create();
        newUser.setIsActive(true);
        newUser.setDescription("updated description");
        newUser.setName("new user");
        adminClient.createUser(newUser,
                               "password");
        assertNotNull(userService.findByName(newUser.getName()));
        adminClient.deleteUser(newUser.getName());
        assertNull(userService.findByName(newUser.getName()));
    }
    /**
     * Test {@link AdminClient#deactivateUser(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeactivateUser()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for DeleteUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.deactivateUser("trader");
            }
        };
        // user doesn't exist
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: IllegalArgumentException: Unknown user: not-a-real-user") {
            @Override
            protected void run()
                    throws Exception
            {
                adminClient.deactivateUser("not-a-real-user");
            }
        };
        MutableUser newUser = userFactory.create();
        newUser.setIsActive(true);
        newUser.setDescription("updated description");
        newUser.setName("new user");
        adminClient.createUser(newUser,
                               "password");
        User foundUser = userService.findByName(newUser.getName());
        assertNotNull(foundUser);
        assertTrue(foundUser.isActive());
        adminClient.deactivateUser(newUser.getName());
        foundUser = userService.findByName(newUser.getName());
        assertNotNull(foundUser);
        assertFalse(foundUser.isActive());
        adminClient.deleteUser(foundUser.getName());
    }
    /**
     * Test {@link AdminClient#readPermissions()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReadPermission()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for ReadPermissionAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.readPermissions();
            }
        };
        testClient.stop();
        assertEquals(permissionDao.count(),
                     adminClient.readPermissions().size());
    }
    /**
     * Test {@link AdminClient#updatePermission(String, Permission)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdatePermission()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for UpdatePermissionAction") {
            @Override
            protected void run()
                    throws Exception
            {
                Permission updatedPermission = permissionFactory.create(AdminPermissions.AddSessionAction.name(),
                                                                        "some new description");
                testClient.updatePermission(AdminPermissions.AddSessionAction.name(),
                                            updatedPermission);
            }
        };
        testClient.stop();
        Permission newPermission = permissionFactory.create(PlatformServices.generateId(),
                                                            "new permission description");
        adminClient.createPermission(newPermission);
        assertNotNull(authorizationService.findPermissionByName(newPermission.getName()));
        Permission updatedPermission = permissionFactory.create(newPermission.getName(),
                                                                "updated description");
        adminClient.updatePermission(newPermission.getName(),
                                     updatedPermission);
        Permission dbPermission = permissionDao.findByName(updatedPermission.getName());
        assertEquals(dbPermission.getName(),
                     updatedPermission.getName());
        assertEquals(dbPermission.getDescription(),
                     updatedPermission.getDescription());
    }
    /**
     * Test {@link AdminClient#deletePermission(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeletePermission()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: trader is not authorized for DeletePermissionAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.deletePermission(AdminPermissions.AddSessionAction.name());
            }
        };
        // permission doesn't exist
        new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: IllegalArgumentException: Unknown permission: not-a-real-permission") {
            @Override
            protected void run()
                    throws Exception
            {
                adminClient.deletePermission("not-a-real-permission");
            }
        };
        Permission newPermission = permissionFactory.create(PlatformServices.generateId(),
                                                            "new permission description");
        adminClient.createPermission(newPermission);
        assertNotNull(authorizationService.findPermissionByName(newPermission.getName()));
        adminClient.deletePermission(newPermission.getName());
        assertNull(authorizationService.findPermissionByName(newPermission.getName()));
    }
}
