package com.marketcetera.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

import com.marketcetera.admin.rpc.AdminRpcService;
import com.marketcetera.fix.ActiveFixSession;
import com.marketcetera.ors.security.SimpleUser;
import com.marketcetera.test.MarketceteraTestBase;

/* $License$ */

/**
 * Tests {@link AdminRpcService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcServiceTest
        extends MarketceteraTestBase
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
        super.setup();
        adminClientFactory = applicationContext.getBean(AdminClientFactory.class);
        permissionFactory = applicationContext.getBean(PermissionFactory.class);
        adminClient = adminClientFactory.create("test",
                                                "test",
                                                "127.0.0.1",
                                                8999);
        adminClient.start();
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void teardown()
            throws Exception
    {
        if(adminClient != null) {
            try {
                adminClient.stop();
            } catch (Exception ignored) {}
            adminClient = null;
        }
    }
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
     * Test {@link AdminClient#readFixSessions()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetFixSessions()
            throws Exception
    {
        assertTrue(adminClient.readFixSessions().isEmpty());
        createInitiatorSession(1);
        List<ActiveFixSession> fixSessions = adminClient.readFixSessions();
        assertEquals(1,
                     fixSessions.size());
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
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for ReadUserAction") {
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
     * Test {@link AdminClient#updateUser(String, com.marketcetera.ors.security.SimpleUser)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateUser()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for UpdateUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                SimpleUser updatedUser = new SimpleUser();
                updatedUser.setActive(false);
                updatedUser.setDescription("updated description");
                updatedUser.setName("not-trade-anymore");
                testClient.updateUser("trader",
                                      updatedUser);
            }
        };
        testClient.stop();
        SimpleUser user = userService.findByName("trader");
        assertNotNull(user);
        assertFalse("updated-description".equals(user.getDescription()));
    }
    /**
     * Test {@link AdminClient#createUser(SimpleUser, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreateUser()
            throws Exception
    {
        // no permission
        final AdminClient testClient = generateTraderClient();
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for CreateUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                SimpleUser newUser = new SimpleUser();
                newUser.setActive(true);
                newUser.setDescription("updated description");
                newUser.setName("new user");
                testClient.createUser(newUser,
                                      "password");
            }
        };
        testClient.stop();
        SimpleUser user = userService.findByName("new user");
        assertNull(user);
        SimpleUser newUser = new SimpleUser();
        newUser.setActive(true);
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
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for ChangeUserPasswordAction") {
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
        SimpleUser newUser = new SimpleUser();
        newUser.setActive(true);
        newUser.setDescription("updated description");
        newUser.setName("new user");
        adminClient.createUser(newUser,
                                  "password");
        adminClient.changeUserPassword("new user",
                                          "password",
                                          "new-password");
        AdminClient newTestClient = generateClient("new user",
                                                   "new-password");
        newTestClient.stop();
        newUser = userService.findByName(newUser.getName());
        userService.delete(newUser);
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
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for DeleteUserAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.deleteUser("trader");
            }
        };
        // user doesn't exist
        new ExpectedFailure<RuntimeException>("IllegalArgumentException: Unknown user: not-a-real-user") {
            @Override
            protected void run()
                    throws Exception
            {
                adminClient.deleteUser("not-a-real-user");
            }
        };
        SimpleUser newUser = new SimpleUser();
        newUser.setActive(true);
        newUser.setDescription("updated description");
        newUser.setName("new user");
        adminClient.createUser(newUser,
                               "password");
        assertNotNull(userService.findByName(newUser.getName()));
        adminClient.deleteUser(newUser.getName());
        assertNull(userService.findByName(newUser.getName()));
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
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for ReadPermissionAction") {
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
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for UpdatePermissionAction") {
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
        Permission newPermission = permissionFactory.create(generateId(),
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
        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for DeletePermissionAction") {
            @Override
            protected void run()
                    throws Exception
            {
                testClient.deletePermission(AdminPermissions.AddSessionAction.name());
            }
        };
        // permission doesn't exist
        new ExpectedFailure<RuntimeException>("IllegalArgumentException: Unknown permission: not-a-real-permission") {
            @Override
            protected void run()
                    throws Exception
            {
                adminClient.deletePermission("not-a-real-permission");
            }
        };
        Permission newPermission = permissionFactory.create(generateId(),
                                                            "new permission description");
        adminClient.createPermission(newPermission);
        assertNotNull(authorizationService.findPermissionByName(newPermission.getName()));
        adminClient.deletePermission(newPermission.getName());
        assertNull(authorizationService.findPermissionByName(newPermission.getName()));
    }
    /**
     * Generate an admin client logged in as user: trader.
     *
     * @return an <code>AdminClient</code> value
     */
    private AdminClient generateTraderClient()
    {
        return generateClient("trader",
                              "trader");
    }
    /**
     * Generate an admin client logged in with the given user/password.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return an <code>AdminClient</code> value
     */
    private AdminClient generateClient(String inUsername,
                                       String inPassword)
    {
        AdminClient testClient = adminClientFactory.create(inUsername,
                                                           inPassword,
                                                           "127.0.0.1",
                                                           8999);
        testClient.start();
        return testClient;
    }
    /**
     * creates {@link Permission} objects
     */
    private PermissionFactory permissionFactory;
    /**
     * creates {@link AdminClient} objects
     */
    private AdminClientFactory adminClientFactory;
    /**
     * provides access to admin services
     */
    private AdminClient adminClient;
}
