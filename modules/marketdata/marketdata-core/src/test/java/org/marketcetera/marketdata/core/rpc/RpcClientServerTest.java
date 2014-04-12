package org.marketcetera.marketdata.core.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.client.rpc.RpcClientImpl;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataContextClassProvider;
import org.marketcetera.trade.TradeContextClassProvider;
import org.marketcetera.trade.utils.OrderHistoryManagerTest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.RpcServer;
import org.marketcetera.util.ws.stateful.SessionManager;

/* $License$ */

/**
 * Tests {@link RpcClientImpl} and {@link RpcServer}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RpcClientServerTest
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
        OrderHistoryManagerTest.once();
    }
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        stopClientServer();
        username = "user";
        password = "password";
        hostname = "127.0.0.1";
        port = -1;
        sessionManager = new SessionManager<MockSession>();
        authenticator = new MockAuthenticator();
        serviceAdapter = new MockMarketDataServiceAdapter();
        startClientServer();
        assertTrue(server.isRunning());
        assertTrue(client.isRunning());
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void after()
            throws Exception
    {
        stopClientServer();
    }
    /**
     * Tests disconnection and reconnection.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDisconnection()
            throws Exception
    {
        final AtomicBoolean status = new AtomicBoolean(false);
        ServerStatusListener statusListener = new ServerStatusListener() {
            @Override
            public void receiveServerStatus(boolean inStatus)
            {
                status.set(inStatus);
            }
        };
        client.addServerStatusListener(statusListener);
        assertTrue(status.get());
        // kill the server
        server.stop();
        assertFalse(server.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !client.isRunning();
            }
        });
        assertFalse(status.get());
        server.start();
        assertTrue(server.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.isRunning();
            }
        });
        assertTrue(status.get());
    }
    /**
     * Tests {@link RpcMarketDataClient#request(org.marketcetera.marketdata.MarketDataRequest, boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRequest()
            throws Exception
    {
        assertTrue(serviceAdapter.getRequests().isEmpty());
        long id = client.request(MarketDataRequestBuilder.newRequestFromString("SYMBOLS=METC"),
                                 true);
        assertTrue(id > 0);
        assertEquals(1,
                     serviceAdapter.getRequests().size());
    }
    /**
     * 
     *
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetLastUpdate()
            throws Exception
    {
        assertTrue(serviceAdapter.getLastUpdateRequests().isEmpty());
        long timestamp = System.nanoTime();
        long returnedTimestamp = client.getLastUpdate(timestamp);
        assertEquals(timestamp,
                     returnedTimestamp);
        assertEquals(1,
                     serviceAdapter.getLastUpdateRequests().size());
    }
    /**
     * 
     *
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCancel()
            throws Exception
    {
        assertTrue(serviceAdapter.getCanceledIds().isEmpty());
        long timestamp = System.nanoTime();
        client.cancel(timestamp);
        assertEquals(1,
                     serviceAdapter.getCanceledIds().size());
    }
    /**
     * 
     *
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetEvents()
            throws Exception
    {
        assertTrue(serviceAdapter.getGetEventsRequests().isEmpty());
        long timestamp = System.nanoTime();
        Deque<Event> events = client.getEvents(timestamp);
        assertTrue(events.isEmpty());
        assertEquals(1,
                     serviceAdapter.getGetEventsRequests().size());
        // add some events of each type to return
        Deque<Event> eventsToReturn = serviceAdapter.getEventsToReturn();
        eventsToReturn.add(EventTestBase.generateDividendEvent());
        events = client.getEvents(timestamp);
        assertEquals(1,
                     events.size());
    }
    /**
     * Stops the test client and server.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void stopClientServer()
            throws Exception
    {
        if(client != null && client.isRunning()) {
            try {
                client.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        client = null;
        if(server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        server = null;
    }
    /**
     * Starts the test client server and client.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void startClientServer()
            throws Exception
    {
        server = new RpcServer<MockSession>();
        server.setHostname(hostname);
        if(port == -1) {
            port = assignPort();
        }
        server.setPort(port);
        server.setSessionManager(sessionManager);
        server.setAuthenticator(authenticator);
        server.setContextClassProvider(TradeContextClassProvider.INSTANCE);
        MarketdataRpcService<MockSession> service = new MarketdataRpcService<>();
        server.getServiceSpecs().add(service);
        service.setServiceAdapter(serviceAdapter);
        server.setContextClassProvider(MarketDataContextClassProvider.INSTANCE);
        server.start();
        client = new RpcMarketDataClient("username",
                                         "password",
                                         hostname,
                                         port,
                                         null);
        client.setContextClassProvider(MarketDataContextClassProvider.INSTANCE);
        client.start();
    }
    /**
     * Assigns a port value that is not in use.
     * 
     * @return an <code>int</code> value
     */
    private int assignPort()
    {
        for(int i=MIN_PORT_NUMBER;i<=MAX_PORT_NUMBER;i++) {
            try(ServerSocket ss = new ServerSocket(i)) {
                ss.setReuseAddress(true);
                try(DatagramSocket ds = new DatagramSocket(i)) {
                    ds.setReuseAddress(true);
                    return i;
                }
            } catch (IOException e) {}
        }
        throw new IllegalArgumentException("No available ports");
    }
    /**
     * 
     */
    private MockMarketDataServiceAdapter serviceAdapter;
    /**
     * 
     */
    private MockAuthenticator authenticator;
    /**
     * 
     */
    private SessionManager<MockSession> sessionManager;
    /**
     * 
     */
    private String hostname;
    /**
     * 
     */
    private int port;
    /**
     * 
     */
    private String username;
    /**
     * 
     */
    private String password;
    /**
     * 
     */
    private static final int MIN_PORT_NUMBER = 10000;
    /**
     * 
     */
    private static final int MAX_PORT_NUMBER = 65535;
    /**
     * 
     */
    private RpcMarketDataClient client;
    /**
     * 
     */
    private RpcServer<MockSession> server;
}
