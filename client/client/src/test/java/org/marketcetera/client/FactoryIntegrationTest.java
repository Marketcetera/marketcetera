package org.marketcetera.client;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.trade.Factory;
import org.marketcetera.util.test.RegExAssert;
import org.marketcetera.util.ws.stateless.Node;

/* $License$ */

/**
 * Verifies integration with
 * {@link org.marketcetera.trade.Factory#setOrderIDFactory(org.marketcetera.core.IDFactory)}.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class FactoryIntegrationTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void setup()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        initServer();
        new ClientManager();
    }
    /**
     * Runs once after all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @AfterClass
    public static void closeServer()
            throws Exception
    {
        if (sServer != null) {
            sServer.close();
            sServer = null;
        }
    }
    /**
     * Tests using a null id prefix for order ids.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void nullIDPrefix()
            throws Exception
    {
        initClient(null);
        String idPattern = MockServiceImpl.ID_PREFIX + "\\d+00\\d";
        Factory factory = Factory.getInstance();
        //Create orders and verify their IDs match expected patterns
        RegExAssert.assertMatches(idPattern, factory.createOrderSingle().getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderCancel(null).getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderReplace(null).getOrderID().toString());
    }
    /**
     * Tests using an id prefix for order ids.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void idPrefix()
            throws Exception
    {
        String prefix = "cetera";
        initClient(prefix);
        String idPattern = prefix + MockServiceImpl.ID_PREFIX + "\\d+00\\d";
        Factory factory = Factory.getInstance();
        //Create orders and verify their IDs match expected patterns
        RegExAssert.assertMatches(idPattern, factory.createOrderSingle().getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderCancel(null).getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderReplace(null).getOrderID().toString());
    }
    /**
     * Tests generating multiple IDs.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void generateMultipleIDs()
            throws Exception
    {
        String prefix = "cetera";
        initClient(prefix);
        Factory factory = Factory.getInstance();
        //Create orders enough times to request IDs from server more than once.
        String idPattern = prefix + MockServiceImpl.ID_PREFIX + "\\d{4,5}";
        short max = ClientIDFactory.MAX_CLIENT_ID * 3;
        for(int i = 0; i < max; i++) {
            RegExAssert.assertMatches(idPattern, factory.createOrderSingle().getOrderID().toString());
        }
    }
    /**
     * Runs after each test.
     */
    @After
    public void closeClient()
    {
        if(mClient != null) {
            mClient.close();
        }
    }
    /**
     * Initializes a client instance with default order ID prefix.
     *
     * @param inOrderIDPrefix a <code>String</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     * @throws ClientInitException if an error occurs initializing the client
     */
    private void initClient(String inOrderIDPrefix)
            throws ConnectionException, ClientInitException
    {
        ClientParameters parameters = new ClientParameters("name",
                                                           "name".toCharArray(),
                                                           MockServer.URL,
                                                           Node.DEFAULT_HOST,
                                                           Node.DEFAULT_PORT,
                                                           inOrderIDPrefix);
        mClient = ClientManager.getManagerInstance().init(parameters);
    }
    /**
     * Initializes the server instance if necessary.
     */
    private static void initServer()
    {
        if(sServer == null) {
            sServer = new MockServer();
        }
    }
    /**
     * test server
     */
    private static MockServer sServer;
    /**
     * test client
     */
    private Client mClient;
}
