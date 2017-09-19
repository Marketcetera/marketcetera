package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataContextClassProvider;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClient;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.utils.OrderHistoryManagerTest;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests the market data RPC client and server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRpcClientServerTest.java 17251 2016-09-08 23:18:29Z colin $
 * @since 2.4.0
 */
public class MarketDataRpcClientServerTest
        extends RpcTestBase<MarketDataRpcClientParameters,MarketDataRpcClient,SessionId,MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase,MarketDataRpcService<SessionId>>
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
        OrderHistoryManagerTest.once();
    }
    /**
     * Run before each test.
     *  
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        serviceAdapter = new MockMarketDataServiceAdapter();
        super.setup();
        client = createClient();
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
//        client.addServerStatusListener(statusListener);
        assertTrue(status.get());
        // kill the server
        rpcServer.stop();
        assertFalse(rpcServer.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !client.isRunning();
            }
        });
        assertFalse(status.get());
        rpcServer.start();
        assertTrue(rpcServer.isRunning());
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
//        assertTrue(serviceAdapter.getRequests().isEmpty());
//        long id = client.request(MarketDataRequestBuilder.newRequestFromString("SYMBOLS=METC"),
//                                 true);
//        assertTrue(id > 0);
//        assertEquals(1,
//                     serviceAdapter.getRequests().size());
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
        client.cancel(String.valueOf(timestamp));
        assertEquals(1,
                     serviceAdapter.getCanceledIds().size());
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
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<MarketDataRpcClientParameters,MarketDataRpcClient> getRpcClientFactory()
    {
        return new MarketDataRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected MarketDataRpcClientParameters getClientParameters(String inHostname,
                                                                int inPort,
                                                                String inUsername,
                                                                String inPassword)
    {
        MarketDataRpcClientParameters parameters = new MarketDataRpcClientParameters();
        parameters.setContextClassProvider(MarketDataContextClassProvider.INSTANCE);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected MarketDataRpcService<SessionId> createTestService()
    {
        MarketDataRpcService<SessionId> service = new MarketDataRpcService<SessionId>();
        service.setServiceAdapter(serviceAdapter);
        return service;
    }
    /**
     * test service adapter value
     */
    private MockMarketDataServiceAdapter serviceAdapter;
    /**
     * test client
     */
    private MarketDataRpcClient client;
}
