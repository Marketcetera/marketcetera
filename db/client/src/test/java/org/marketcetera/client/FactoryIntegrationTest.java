package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.util.test.RegExAssert;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.trade.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.After;
import org.junit.Test;

/* $License$ */
/**
 * Verifies integration with
 * {@link org.marketcetera.trade.Factory#setOrderIDFactory(org.marketcetera.core.IDFactory)}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FactoryIntegrationTest {
    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
        initServer();
    }

    @AfterClass
    public static void closeServer() throws Exception {
        if (sServer != null) {
            sServer.close();
            sServer = null;
        }
    }
    @Test
    public void nullIDPrefix() throws Exception {
        initClient(null);
        String idPattern = MockServiceImpl.ID_PREFIX + "\\d+00\\d";
        Factory factory = Factory.getInstance();
        //Create orders and verify their IDs match expected patterns
        RegExAssert.assertMatches(idPattern, factory.createOrderSingle().getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderCancel(null).getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderReplace(null).getOrderID().toString());
    }
    
    @Test
    public void idPrefix() throws Exception {
        String prefix = "cetera";
        initClient(prefix);
        String idPattern = prefix + MockServiceImpl.ID_PREFIX + "\\d+00\\d";
        Factory factory = Factory.getInstance();
        //Create orders and verify their IDs match expected patterns
        RegExAssert.assertMatches(idPattern, factory.createOrderSingle().getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderCancel(null).getOrderID().toString());
        RegExAssert.assertMatches(idPattern, factory.createOrderReplace(null).getOrderID().toString());
    }

    @Test
    public void generateMultipleIDs() throws Exception {
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

    @After
    public void closeClient() {
        if(mClient != null) {
            mClient.close();
        }
    }
    private void initClient(String inOrderIDPrefix)
            throws ConnectionException, ClientInitException {
        ClientParameters parameters = new ClientParameters("name",
                "name".toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT,
                inOrderIDPrefix);
        ClientManager.init(parameters);
        mClient = ClientManager.getInstance();
    }

    private static void initServer() {
        if (sServer == null) {
            sServer = new MockServer();
        }
    }

    private static MockServer sServer;
    private Client mClient;
}
