package org.marketcetera.admin.rpc;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.MutableUserFactory;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.dao.PersistentPermissionDao;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.core.PlatformServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import io.grpc.StatusRuntimeException;
import junitparams.JUnitParamsRunner;

/* $License$ */

/**
 * Provides common test behavior for admin server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootTest(classes=AdminTestConfiguration.class)
@RunWith(JUnitParamsRunner.class)
@ComponentScan(basePackages={"org.marketcetera"})
@EntityScan(basePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
public abstract class AdminTestBase
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
        adminClient = generateAdminClient("test",
                                          "test");
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
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
     * Generate an {@link AdminClient} owned by user "trader".
     *
     * @return an <code>AdminClient</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected AdminClient generateTraderAdminClient()
            throws Exception
    {
        return generateAdminClient("trader",
                                   "trader");
    }
    /**
     * Verify the given exception is for lack of the given permission.
     *
     * @param inThrowable a <code>Throwable</code> value
     * @param inPermissionName a <code>String</code> value
     */
    protected void assertNotAuthorized(Throwable inThrowable,
                                       String inPermissionName)
    {
        assertTrue(inThrowable instanceof StatusRuntimeException);
        assertTrue(inThrowable.getMessage().contains("not authorized"));
        assertTrue(inThrowable.getMessage().contains(inPermissionName));
    }
    /**
     * Generate and create a user with no permissions.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>User</code> value
     */
    protected User generateUserNoPermissions(String inUsername,
                                             String inPassword)
    {
        User noPermissionUser = AdminRpcUtilTest.generateUser(inUsername,
                                                              PlatformServices.generateId());
        noPermissionUser = adminClient.createUser(noPermissionUser,
                                                  inPassword);
        return noPermissionUser;
    }
    /**
     * Generate an <code>AdminClient</code> with the given user/password.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return an <code>AdminClient</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected AdminClient generateAdminClient(String inUsername,
                                              String inPassword)
            throws Exception
    {
        AdminRpcClientParameters params = new AdminRpcClientParameters();
        params.setHostname(rpcHostname);
        params.setPort(rpcPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        AdminClient adminClient = adminClientFactory.create(params);
        adminClient.start();
        return adminClient;
    }
    /**
     * RPC hostname
     */
    @Value("${metc.rpc.hostname}")
    private String rpcHostname = "127.0.0.1";
    /**
     * RPC port
     */
    @Value("${metc.rpc.port}")
    private int rpcPort = 18999;
    /**
     * provides access to admin services
     */
    protected AdminClient adminClient;
    /**
     * provides data store access to permission objects
     */
    @Autowired
    protected PersistentPermissionDao permissionDao;
    /**
     * creates {@link Permission} objects
     */
    @Autowired
    protected PermissionFactory permissionFactory;
    /**
     * creates {@link AdminClient} objects
     */
    @Autowired
    protected AdminRpcClientFactory adminClientFactory;
    /**
     * provides access to authorization services
     */
    @Autowired
    protected AuthorizationService authorizationService;
    /**
     * creates {@link MutableUser} objects
     */
    @Autowired
    protected MutableUserFactory userFactory;
    /**
     * provides access to user services
     */
    @Autowired
    protected UserService userService;
    /**
     * test artifact used to identify the current test case
     */
    @Rule
    public TestName name = new TestName();
    /**
     * rule used to load test context
     */
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    /**
     * test spring method rule
     */
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
}
