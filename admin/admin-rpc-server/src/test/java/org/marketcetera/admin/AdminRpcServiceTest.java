package org.marketcetera.admin;

import org.marketcetera.admin.AdminRpcClient;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserAttributeFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.admin.rpc.AdminRpcService;
import org.marketcetera.admin.service.PasswordServiceImpl;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.ws.tags.SessionId;

import com.marketcetera.admin.AdminRpcServiceGrpc;

/* $License$ */

/**
 * Tests {@link AdminRpcService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcServiceTest
        extends RpcTestBase<AdminRpcClientParameters,AdminRpcClient,SessionId,AdminRpcServiceGrpc.AdminRpcServiceImplBase,AdminRpcService<SessionId>>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<AdminRpcClientParameters,AdminRpcClient> getRpcClientFactory()
    {
        return new AdminRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected AdminRpcClientParameters getClientParameters(String inHostname,
                                                           int inPort,
                                                           String inUsername,
                                                           String inPassword)
    {
        AdminRpcClientParameters parameters = new AdminRpcClientParameters();
        parameters.setHeartbeatInterval(1000);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#prepareClient(org.marketcetera.rpc.client.RpcClient)
     */
    @Override
    protected void prepareClient(AdminRpcClient inClient)
    {
        inClient.setPasswordService(new PasswordServiceImpl());
        inClient.setPermissionFactory(new SimplePermissionFactory());
        inClient.setRoleFactory(new SimpleRoleFactory());
        inClient.setUserAttributeFactory(new SimpleUserAttributeFactory());
        inClient.setUserFactory(new SimpleUserFactory());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected AdminRpcService<SessionId> createTestService()
    {
        return new AdminRpcService<SessionId>();
    }
//    /**
//     * Test {@link AdminClient#getPermissionsForUsername(String)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetPermissionNames()
//            throws Exception
//    {
//        Set<Permission> expectedPermissions = authorizationService.findAllPermissionsByUsername("test");
//        assertFalse(expectedPermissions.isEmpty());
//        assertEquals(expectedPermissions.size(),
//                     adminClient.getPermissionsForCurrentUser().size());
//    }
//    /**
//     * Test {@link AdminClient#readUsers()}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testReadUsers()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for ReadUserAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                testClient.readUsers();
//            }
//        };
//        testClient.stop();
//        assertEquals(4,
//                     adminClient.readUsers().size());
//    }
//    /**
//     * Test {@link AdminClient#updateUser(String, com.marketcetera.ors.security.PersistentUser)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testUpdateUser()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for UpdateUserAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                PersistentUser updatedUser = new PersistentUser();
//                updatedUser.setActive(false);
//                updatedUser.setDescription("updated description");
//                updatedUser.setName("not-trade-anymore");
//                testClient.updateUser("trader",
//                                      updatedUser);
//            }
//        };
//        testClient.stop();
//        User user = userService.findByName("trader");
//        assertNotNull(user);
//        assertFalse("updated-description".equals(user.getDescription()));
//    }
//    /**
//     * Test {@link AdminClient#createUser(PersistentUser, String)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testCreateUser()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for CreateUserAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                PersistentUser newUser = new PersistentUser();
//                newUser.setActive(true);
//                newUser.setDescription("updated description");
//                newUser.setName("new user");
//                testClient.createUser(newUser,
//                                      "password");
//            }
//        };
//        testClient.stop();
//        User user = userService.findByName("new user");
//        assertNull(user);
//        PersistentUser newUser = new PersistentUser();
//        newUser.setActive(true);
//        newUser.setDescription("updated description");
//        newUser.setName("new user");
//        adminClient.createUser(newUser,
//                                  "password");
//        user = userService.findByName("new user");
//        assertNotNull(user);
//        assertEquals("updated description",
//                     user.getDescription());
//        assertTrue(user.isActive());
//        userService.delete(user);
//    }
//    /**
//     * Test {@link AdminClient#changeUserPassword(String, String, String)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testChangeUserPassword()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for ChangeUserPasswordAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                testClient.changeUserPassword("trader",
//                                              "trader",
//                                              "new-password");
//            }
//        };
//        testClient.stop();
//        PersistentUser newUser = new PersistentUser();
//        newUser.setActive(true);
//        newUser.setDescription("updated description");
//        newUser.setName("new user");
//        adminClient.createUser(newUser,
//                                  "password");
//        adminClient.changeUserPassword("new user",
//                                          "password",
//                                          "new-password");
//        AdminClient newTestClient = generateClient("new user",
//                                                   "new-password");
//        newTestClient.stop();
//        newUser = (PersistentUser)userService.findByName(newUser.getName());
//        userService.delete(newUser);
//    }
//    /**
//     * Test {@link AdminClient#deleteUser(String)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testDeleteUser()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for DeleteUserAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                testClient.deleteUser("trader");
//            }
//        };
//        // user doesn't exist
//        new ExpectedFailure<RuntimeException>("IllegalArgumentException: Unknown user: not-a-real-user") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                adminClient.deleteUser("not-a-real-user");
//            }
//        };
//        PersistentUser newUser = new PersistentUser();
//        newUser.setActive(true);
//        newUser.setDescription("updated description");
//        newUser.setName("new user");
//        adminClient.createUser(newUser,
//                               "password");
//        assertNotNull(userService.findByName(newUser.getName()));
//        adminClient.deleteUser(newUser.getName());
//        assertNull(userService.findByName(newUser.getName()));
//    }
//    /**
//     * Test {@link AdminClient#readPermissions()}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testReadPermission()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for ReadPermissionAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                testClient.readPermissions();
//            }
//        };
//        testClient.stop();
//        assertEquals(permissionDao.count(),
//                     adminClient.readPermissions().size());
//    }
//    /**
//     * Test {@link AdminClient#updatePermission(String, Permission)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testUpdatePermission()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for UpdatePermissionAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                Permission updatedPermission = permissionFactory.create(AdminPermissions.AddSessionAction.name(),
//                                                                        "some new description");
//                testClient.updatePermission(AdminPermissions.AddSessionAction.name(),
//                                            updatedPermission);
//            }
//        };
//        testClient.stop();
//        Permission newPermission = permissionFactory.create(generateId(),
//                                                            "new permission description");
//        adminClient.createPermission(newPermission);
//        assertNotNull(authorizationService.findPermissionByName(newPermission.getName()));
//        Permission updatedPermission = permissionFactory.create(newPermission.getName(),
//                                                                "updated description");
//        adminClient.updatePermission(newPermission.getName(),
//                                     updatedPermission);
//        Permission dbPermission = permissionDao.findByName(updatedPermission.getName());
//        assertEquals(dbPermission.getName(),
//                     updatedPermission.getName());
//        assertEquals(dbPermission.getDescription(),
//                     updatedPermission.getDescription());
//    }
//    /**
//     * Test {@link AdminClient#deletePermission(String)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testDeletePermission()
//            throws Exception
//    {
//        // no permission
//        final AdminClient testClient = generateTraderClient();
//        new ExpectedFailure<RuntimeException>("NotAuthorizedException: trader is not authorized for DeletePermissionAction") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                testClient.deletePermission(AdminPermissions.AddSessionAction.name());
//            }
//        };
//        // permission doesn't exist
//        new ExpectedFailure<RuntimeException>("IllegalArgumentException: Unknown permission: not-a-real-permission") {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                adminClient.deletePermission("not-a-real-permission");
//            }
//        };
//        Permission newPermission = permissionFactory.create(generateId(),
//                                                            "new permission description");
//        adminClient.createPermission(newPermission);
//        assertNotNull(authorizationService.findPermissionByName(newPermission.getName()));
//        adminClient.deletePermission(newPermission.getName());
//        assertNull(authorizationService.findPermissionByName(newPermission.getName()));
//    }
//    /**
//     * Generate an admin client logged in as user: trader.
//     *
//     * @return an <code>AdminClient</code> value
//     */
//    private AdminClient generateTraderClient()
//    {
//        return generateClient("trader",
//                              "trader");
//    }
//    /**
//     * Generate an admin client logged in with the given user/password.
//     *
//     * @param inUsername a <code>String</code> value
//     * @param inPassword a <code>String</code> value
//     * @return an <code>AdminClient</code> value
//     */
//    private AdminClient generateClient(String inUsername,
//                                       String inPassword)
//    {
//        AdminClient testClient = adminClientFactory.create(inUsername,
//                                                           inPassword,
//                                                           "127.0.0.1",
//                                                           8999);
//        testClient.start();
//        return testClient;
//    }
//    /**
//     * creates {@link Permission} objects
//     */
//    private PermissionFactory permissionFactory;
//    /**
//     * creates {@link AdminClient} objects
//     */
//    private AdminRpcClientFactory adminClientFactory;
//    /**
//     * provides access to admin services
//     */
//    private AdminClient adminClient;
}
