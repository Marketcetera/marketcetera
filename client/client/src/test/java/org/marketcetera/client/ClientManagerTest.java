package org.marketcetera.client;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link ClientManager}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClientManagerTest
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
        LoggerConfiguration.logSetup();
        new ClientManager();
    }
    /**
     * Tests {@link ClientManager#getManagerInstance()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetManagerInstance()
            throws Exception
    {
        assertNotNull(ClientManager.getManagerInstance());
        new ExpectedFailure<IllegalArgumentException>(Messages.CLIENT_ALREADY_INITIALIZED.getText()) {
            protected void run()
                    throws Exception
            {
                new ClientManager();
            }
        };
    }
    /**
     * Tests {@link ClientManager#init(ClientParameters)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testInit()
            throws Exception
    {
        ClientManager.getManagerInstance().setClientFactory(null);
        final ClientParameters parameters = new ClientParameters("username",
                                                                 "password".toCharArray(),
                                                                 "URL",
                                                                 "hostname",
                                                                 1024);
        ClientParametersSpec key = parameters.getParametersSpec();
        new ExpectedFailure<ClientInitException>(Messages.NO_CLIENT_FACTORY) {
            protected void run()
                    throws Exception
            {
                ClientManager.getManagerInstance().init(parameters);
            }
        };
        final Client myClient = new MockClient();
        final ClientFactory myFactory = new ClientFactory() {
            @Override
            public Client getClient(ClientParameters inClientParameters,
                                    ClientLifecycleManager inClientLifecycleManager)
                    throws ClientInitException, ConnectionException
            {
                return myClient;
            }
        };
        ClientManager.getManagerInstance().setClientFactory(myFactory);
        assertNull(ClientManager.getManagerInstance().getInstance(key));
        new ExpectedFailure<ClientInitException>(Messages.CLIENT_NOT_INITIALIZED) {
            protected void run()
                    throws Exception
            {
                ClientManager.getManagerInstance().getInstance();
            }
        };
        assertEquals(myClient,
                     ClientManager.getManagerInstance().init(parameters));
        assertEquals(myClient,
                     ClientManager.getManagerInstance().getInstance());
        Client secondClient = ClientManager.getManagerInstance().init(parameters);
        assertSame(myClient,
                   secondClient);
    }
}
