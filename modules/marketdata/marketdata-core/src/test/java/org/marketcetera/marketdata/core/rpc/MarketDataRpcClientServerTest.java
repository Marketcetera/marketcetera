package org.marketcetera.marketdata.core.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.webservice.PageRequest;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataContextClassProvider;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.TradeContextClassProvider;
import org.marketcetera.trade.utils.OrderHistoryManagerTest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.RpcServer;
import org.marketcetera.util.ws.stateful.SessionManager;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link RpcClientImpl} and {@link RpcServer}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcClientServerTest
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
     * Tests {@link MarketDataRpcClient#request(org.marketcetera.marketdata.MarketDataRequest, boolean)}.
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
     * Tests {@link MarketDataRpcClient#getLastUpdate(long)}.
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
     * Tests {@link MarketDataRpcClient#cancel(long)}.
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
     * Tests {@link MarketDataRpcClient#getEvents(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetEvents()
            throws Exception
    {
        assertTrue(serviceAdapter.getEventsRequests().isEmpty());
        long timestamp = System.nanoTime();
        Deque<Event> events = client.getEvents(timestamp);
        assertTrue(events.isEmpty());
        assertEquals(1,
                     serviceAdapter.getEventsRequests().size());
        // add some events of each type to return
        Deque<Event> eventsToReturn = serviceAdapter.getEventsToReturn();
        eventsToReturn.add(EventTestBase.generateDividendEvent());
        Equity equity = new Equity("AAPL");
        Option option = OptionUtils.getOsiOptionFromString("MSFT  001022P12345123");
        Instrument[] testInstruments = new Instrument[] { equity,Future.fromString("AAPL-201306"),new Currency("USD/BTC"),option};
        for(Instrument instrument : testInstruments) {
            if(instrument.equals(option)) {
                eventsToReturn.add(EventTestBase.generateOptionAskEvent((Option)instrument,
                                                                        equity));
                eventsToReturn.add(EventTestBase.generateOptionBidEvent((Option)instrument,
                                                                        equity));
                eventsToReturn.add(EventTestBase.generateOptionTradeEvent((Option)instrument,
                                                                          equity));
                eventsToReturn.add(EventTestBase.generateOptionMarketstatEvent((Option)instrument,
                                                                               equity));
            } else {
                eventsToReturn.add(EventTestBase.generateAskEvent(instrument));
                eventsToReturn.add(EventTestBase.generateBidEvent(instrument));
                eventsToReturn.add(EventTestBase.generateTradeEvent(instrument));
                eventsToReturn.add(EventTestBase.generateMarketstatEvent(instrument));
            }
        }
        events = client.getEvents(timestamp);
        assertEquals(eventsToReturn.size(),
                     events.size());
    }
    /**
     * Tests {@link MarketDataRpcClient#getAllEvents(java.util.List)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAllEvents()
            throws Exception
    {
        assertTrue(serviceAdapter.getAllEventsRequests().isEmpty());
        long timestamp = System.nanoTime();
        List<Long> ids = Lists.newArrayList();
        ids.add(timestamp);
        ids.add(timestamp+1);
        ids.add(timestamp+2);
        Map<Long,LinkedList<Event>> events = client.getAllEvents(ids);
        assertTrue(events.isEmpty());
        assertEquals(1,
                     serviceAdapter.getAllEventsRequests().size());
        assertEquals(3,
                     serviceAdapter.getAllEventsRequests().get(0).size());
        // add some events of each type to return
        Map<Long,LinkedList<Event>> eventsToReturn = serviceAdapter.getAllEventsToReturn();
        long id1 = System.nanoTime();
        long id2 = System.nanoTime();
        LinkedList<Event> events1 = new LinkedList<>();
        LinkedList<Event> events2 = new LinkedList<>();
        eventsToReturn.put(id1,
                           events1);
        eventsToReturn.put(id2,
                           events2);
        events1.add(EventTestBase.generateDividendEvent());
        events2.add(EventTestBase.generateDividendEvent());
        Equity equity = new Equity("AAPL");
        Option option = OptionUtils.getOsiOptionFromString("MSFT  001022P12345123");
        Instrument[] testInstruments = new Instrument[] { equity,Future.fromString("AAPL-201306"),new Currency("USD/BTC"),option};
        for(Instrument instrument : testInstruments) {
            if(instrument.equals(option)) {
                events1.add(EventTestBase.generateOptionAskEvent((Option)instrument,
                                                                 equity));
                events1.add(EventTestBase.generateOptionBidEvent((Option)instrument,
                                                                 equity));
                events1.add(EventTestBase.generateOptionTradeEvent((Option)instrument,
                                                                   equity));
                events1.add(EventTestBase.generateOptionMarketstatEvent((Option)instrument,
                                                                        equity));
                events2.add(EventTestBase.generateOptionAskEvent((Option)instrument,
                                                                 equity));
                events2.add(EventTestBase.generateOptionBidEvent((Option)instrument,
                                                                 equity));
                events2.add(EventTestBase.generateOptionTradeEvent((Option)instrument,
                                                                   equity));
                events2.add(EventTestBase.generateOptionMarketstatEvent((Option)instrument,
                                                                        equity));
            } else {
                events1.add(EventTestBase.generateAskEvent(instrument));
                events1.add(EventTestBase.generateBidEvent(instrument));
                events1.add(EventTestBase.generateTradeEvent(instrument));
                events1.add(EventTestBase.generateMarketstatEvent(instrument));
                events2.add(EventTestBase.generateAskEvent(instrument));
                events2.add(EventTestBase.generateBidEvent(instrument));
                events2.add(EventTestBase.generateTradeEvent(instrument));
                events2.add(EventTestBase.generateMarketstatEvent(instrument));
            }
        }
        events = client.getAllEvents(Lists.newArrayList(id1,id2));
        assertEquals(eventsToReturn.size(),
                     events.size());
        assertEquals(eventsToReturn.get(id1).size(),
                     events.get(id1).size());
        assertEquals(eventsToReturn.get(id2).size(),
                     events.get(id2).size());
    }
    /**
     * Tests {@link MarketDataRpcClient#getSnapshot(Instrument, org.marketcetera.marketdata.Content, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetSnapshot()
            throws Exception
    {
        Equity equity = new Equity("AAPL");
        Deque<Event> events = serviceAdapter.getSnapshot(equity,
                                                         Content.LATEST_TICK,
                                                         null);
        assertTrue(events.isEmpty());
        Deque<Event> eventsToReturn = serviceAdapter.getSnapshotEventsToReturn();
        eventsToReturn.add(EventTestBase.generateDividendEvent());
        Option option = OptionUtils.getOsiOptionFromString("MSFT  001022P12345123");
        Instrument[] testInstruments = new Instrument[] { equity,Future.fromString("AAPL-201306"),new Currency("USD/BTC"),option};
        for(Instrument instrument : testInstruments) {
            if(instrument.equals(option)) {
                eventsToReturn.add(EventTestBase.generateOptionAskEvent((Option)instrument,
                                                                        equity));
                eventsToReturn.add(EventTestBase.generateOptionBidEvent((Option)instrument,
                                                                        equity));
                eventsToReturn.add(EventTestBase.generateOptionTradeEvent((Option)instrument,
                                                                          equity));
                eventsToReturn.add(EventTestBase.generateOptionMarketstatEvent((Option)instrument,
                                                                               equity));
            } else {
                eventsToReturn.add(EventTestBase.generateAskEvent(instrument));
                eventsToReturn.add(EventTestBase.generateBidEvent(instrument));
                eventsToReturn.add(EventTestBase.generateTradeEvent(instrument));
                eventsToReturn.add(EventTestBase.generateMarketstatEvent(instrument));
            }
            events = client.getSnapshot(instrument,
                                        Content.LATEST_TICK,
                                        "provider");
            assertEquals(eventsToReturn.size(),
                         events.size());
        }
    }
    /**
     * Tests {@link MarketDataRpcClient#getSnapshotPage(Instrument, Content, String, PageRequest)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetSnapshotPage()
            throws Exception
    {
        Equity equity = new Equity("AAPL");
        Deque<Event> events = serviceAdapter.getSnapshotPage(equity,
                                                             Content.LATEST_TICK,
                                                             null,
                                                             new PageRequest(1,1));
        assertTrue(events.isEmpty());
        Deque<Event> eventsToReturn = serviceAdapter.getSnapshotEventsToReturn();
        eventsToReturn.add(EventTestBase.generateDividendEvent());
        Option option = OptionUtils.getOsiOptionFromString("MSFT  001022P12345123");
        Instrument[] testInstruments = new Instrument[] { equity,Future.fromString("AAPL-201306"),new Currency("USD/BTC"),option};
        for(Instrument instrument : testInstruments) {
            if(instrument.equals(option)) {
                eventsToReturn.add(EventTestBase.generateOptionAskEvent((Option)instrument,
                                                                        equity));
                eventsToReturn.add(EventTestBase.generateOptionBidEvent((Option)instrument,
                                                                        equity));
                eventsToReturn.add(EventTestBase.generateOptionTradeEvent((Option)instrument,
                                                                          equity));
                eventsToReturn.add(EventTestBase.generateOptionMarketstatEvent((Option)instrument,
                                                                               equity));
            } else {
                eventsToReturn.add(EventTestBase.generateAskEvent(instrument));
                eventsToReturn.add(EventTestBase.generateBidEvent(instrument));
                eventsToReturn.add(EventTestBase.generateTradeEvent(instrument));
                eventsToReturn.add(EventTestBase.generateMarketstatEvent(instrument));
            }
            events = client.getSnapshotPage(instrument,
                                            Content.LATEST_TICK,
                                            "provider",
                                            new PageRequest(1,Integer.MAX_VALUE));
            assertEquals(eventsToReturn.size(),
                         events.size());
        }
    }
    /**
     * Tests {@link MarketDataRpcClient#getAvailableCapability()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAvailableCapability()
            throws Exception
    {
        assertEquals(0,
                     serviceAdapter.getCapabilityRequests().get());
        Set<Capability> capabilities = client.getAvailableCapability();
        assertTrue(capabilities.isEmpty());
        assertEquals(1,
                     serviceAdapter.getCapabilityRequests().get());
        serviceAdapter.getCapabilitiesToReturn().addAll(EnumSet.allOf(Capability.class));
        capabilities = client.getAvailableCapability();
        assertEquals(capabilities,
                     serviceAdapter.getCapabilitiesToReturn());
        assertEquals(2,
                     serviceAdapter.getCapabilityRequests().get());
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
        MarketDataRpcService<MockSession> service = new MarketDataRpcService<>();
        server.getServiceSpecs().add(service);
        service.setServiceAdapter(serviceAdapter);
        server.setContextClassProvider(MarketDataContextClassProvider.INSTANCE);
        server.start();
        client = new MarketDataRpcClient("username",
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
     * test service adapter value
     */
    private MockMarketDataServiceAdapter serviceAdapter;
    /**
     * test authenticator value
     */
    private MockAuthenticator authenticator;
    /**
     * test session manager value
     */
    private SessionManager<MockSession> sessionManager;
    /**
     * test hostname
     */
    private String hostname;
    /**
     * test port
     */
    private int port;
    /**
     * minimum test port value
     */
    private static final int MIN_PORT_NUMBER = 10000;
    /**
     * maximum test port value
     */
    private static final int MAX_PORT_NUMBER = 65535;
    /**
     * test client value
     */
    private MarketDataRpcClient client;
    /**
     * tests server value
     */
    private RpcServer<MockSession> server;
}
