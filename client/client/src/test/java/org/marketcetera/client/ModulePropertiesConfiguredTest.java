package org.marketcetera.client;

import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.util.ws.stateless.Node;

/* $License$ */

/**
 * Tests the {@link org.marketcetera.client.ClientModule} module when
 * it's configured via the properties file as it would be in strategy agent.
 *
 * @author anshul@marketcetera.com
 */
public class ModulePropertiesConfiguredTest
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
    @Before
    public void clientSetup() throws Exception {
        mManager = new ModuleManager();
        mManager.setConfigurationProvider(CONFIG_PROVIDER);
        CONFIG_PROVIDER.setURL(MockServer.URL);
        CONFIG_PROVIDER.setUsername(USER_NAME);
        CONFIG_PROVIDER.setPassword(USER_NAME);
        CONFIG_PROVIDER.setHostname(Node.DEFAULT_HOST);
        CONFIG_PROVIDER.setPort(Node.DEFAULT_PORT);
        CONFIG_PROVIDER.setIDPrefix(IDPREFIX);
        mManager.init();
        client = ClientManager.getManagerInstance().getInstance();
    }

    @Override
    protected Object getExpectedUsername() {
        return USER_NAME;
    }

    @Override
    protected Object getExpectedURL() {
        return MockServer.URL;
    }

    private static final MockConfigurationProvider CONFIG_PROVIDER =
            new MockConfigurationProvider();

    private static final String USER_NAME = "me";
}
