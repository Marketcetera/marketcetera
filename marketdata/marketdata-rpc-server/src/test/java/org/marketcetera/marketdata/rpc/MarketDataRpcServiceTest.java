package org.marketcetera.marketdata.rpc;

import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClient;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests the market data RPC client and server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRpcClientServerTest.java 17251 2016-09-08 23:18:29Z colin $
 * @since 2.4.0
 */
public class MarketDataRpcServiceTest
        extends RpcTestBase<MarketDataRpcClientParameters,MarketDataRpcClient,SessionId,MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase,MarketDataRpcService<SessionId>>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<MarketDataRpcClientParameters, MarketDataRpcClient> getRpcClientFactory()
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
        parameters.setHeartbeatInterval(1000);
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
        MockMarketDataServiceAdapter serviceAdapter = new MockMarketDataServiceAdapter();
        MarketDataRpcService<SessionId> service = new MarketDataRpcService<>();
        service.setServiceAdapter(serviceAdapter);
        return service;
    }
//    /**
//     * Tests {@link MarketDataRpcClient#request(org.marketcetera.marketdata.MarketDataRequest, boolean)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testRequest()
//            throws Exception
//    {
////        assertTrue(serviceAdapter.getRequests().isEmpty());
////        long id = client.request(MarketDataRequestBuilder.newRequestFromString("SYMBOLS=METC"),
////                                 true);
////        assertTrue(id > 0);
////        assertEquals(1,
////                     serviceAdapter.getRequests().size());
//    }
//    /**
//     * Tests {@link MarketDataRpcClient#cancel(long)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Ignore@Test
//    public void testCancel()
//            throws Exception
//    {
//        assertTrue(serviceAdapter.getCanceledIds().isEmpty());
//        long timestamp = System.nanoTime();
//        client.cancel(String.valueOf(timestamp));
//        assertEquals(1,
//                     serviceAdapter.getCanceledIds().size());
//    }
//    /**
//     * Tests {@link MarketDataRpcClient#getSnapshot(Instrument, org.marketcetera.marketdata.Content, String)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetSnapshot()
//            throws Exception
//    {
//        Equity equity = new Equity("AAPL");
//        Deque<Event> events = serviceAdapter.getSnapshot(equity,
//                                                         Content.LATEST_TICK);
//        assertTrue(events.isEmpty());
//        Deque<Event> eventsToReturn = serviceAdapter.getSnapshotEventsToReturn();
//        eventsToReturn.add(EventTestBase.generateDividendEvent());
//        Option option = OptionUtils.getOsiOptionFromString("MSFT  001022P12345123");
//        Instrument[] testInstruments = new Instrument[] { equity,Future.fromString("AAPL-201306"),new Currency("USD/BTC"),option};
//        for(Instrument instrument : testInstruments) {
//            if(instrument.equals(option)) {
//                eventsToReturn.add(EventTestBase.generateOptionAskEvent((Option)instrument,
//                                                                        equity));
//                eventsToReturn.add(EventTestBase.generateOptionBidEvent((Option)instrument,
//                                                                        equity));
//                eventsToReturn.add(EventTestBase.generateOptionTradeEvent((Option)instrument,
//                                                                          equity));
//                eventsToReturn.add(EventTestBase.generateOptionMarketstatEvent((Option)instrument,
//                                                                               equity));
//            } else {
//                eventsToReturn.add(EventTestBase.generateAskEvent(instrument));
//                eventsToReturn.add(EventTestBase.generateBidEvent(instrument));
//                eventsToReturn.add(EventTestBase.generateTradeEvent(instrument));
//                eventsToReturn.add(EventTestBase.generateMarketstatEvent(instrument));
//            }
//            events = client.getSnapshot(instrument,
//                                        Content.LATEST_TICK);
//            assertEquals(eventsToReturn.size(),
//                         events.size());
//        }
//    }
//    /**
//     * Tests {@link MarketDataRpcClient#getSnapshotPage(Instrument, Content, String, PageRequest)}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetSnapshotPage()
//            throws Exception
//    {
//        Equity equity = new Equity("AAPL");
//        Deque<Event> events = serviceAdapter.getSnapshotPage(equity,
//                                                             Content.LATEST_TICK,
//                                                             new PageRequest(1,1));
//        assertTrue(events.isEmpty());
//        Deque<Event> eventsToReturn = serviceAdapter.getSnapshotEventsToReturn();
//        eventsToReturn.add(EventTestBase.generateDividendEvent());
//        Option option = OptionUtils.getOsiOptionFromString("MSFT  001022P12345123");
//        Instrument[] testInstruments = new Instrument[] { equity,Future.fromString("AAPL-201306"),new Currency("USD/BTC"),option};
//        for(Instrument instrument : testInstruments) {
//            if(instrument.equals(option)) {
//                eventsToReturn.add(EventTestBase.generateOptionAskEvent((Option)instrument,
//                                                                        equity));
//                eventsToReturn.add(EventTestBase.generateOptionBidEvent((Option)instrument,
//                                                                        equity));
//                eventsToReturn.add(EventTestBase.generateOptionTradeEvent((Option)instrument,
//                                                                          equity));
//                eventsToReturn.add(EventTestBase.generateOptionMarketstatEvent((Option)instrument,
//                                                                               equity));
//            } else {
//                eventsToReturn.add(EventTestBase.generateAskEvent(instrument));
//                eventsToReturn.add(EventTestBase.generateBidEvent(instrument));
//                eventsToReturn.add(EventTestBase.generateTradeEvent(instrument));
//                eventsToReturn.add(EventTestBase.generateMarketstatEvent(instrument));
//            }
//            events = client.getSnapshot(instrument,
//                                            Content.LATEST_TICK,
//                                            new PageRequest(1,Integer.MAX_VALUE));
//            assertEquals(eventsToReturn.size(),
//                         events.size());
//        }
//    }
//    /**
//     * Tests {@link MarketDataRpcClient#getAvailableCapability()}.
//     *
//     * @throws Exception if an unexpected error occurs
//     */
//    @Test
//    public void testGetAvailableCapability()
//            throws Exception
//    {
//        assertEquals(0,
//                     serviceAdapter.getCapabilityRequests().get());
//        Set<Capability> capabilities = client.getAvailableCapability();
//        assertTrue(capabilities.isEmpty());
//        assertEquals(1,
//                     serviceAdapter.getCapabilityRequests().get());
//        serviceAdapter.getCapabilitiesToReturn().addAll(EnumSet.allOf(Capability.class));
//        capabilities = client.getAvailableCapability();
//        assertEquals(capabilities,
//                     serviceAdapter.getCapabilitiesToReturn());
//        assertEquals(2,
//                     serviceAdapter.getCapabilityRequests().get());
//    }
}
