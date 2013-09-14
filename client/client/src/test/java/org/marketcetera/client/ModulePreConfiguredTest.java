package org.marketcetera.client;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.util.ws.stateless.Node;

/* $License$ */

/**
 * Tests the {@link ClientModule} module when the client is initialized
 * before the module is created as it would be within photon.
 *
 * @author anshul@marketcetera.com
 */
@Ignore
public class ModulePreConfiguredTest
        extends ClientModuleTestBase
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        new ClientManager();
    }
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void clientSetup()
            throws Exception
    {
        mManager = new ModuleManager();
        //Initialize the client before initializing the module manager
        String username = "me";
        ClientParameters parameters = new ClientParameters(username,
                                                           username.toCharArray(),
                                                           MockServer.URL,
                                                           Node.DEFAULT_HOST,
                                                           Node.DEFAULT_PORT,
                                                           IDPREFIX);
        client = ClientManager.getManagerInstance().init(parameters);
        mManager.init();
    }
}