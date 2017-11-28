package org.marketcetera.test.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.EnumSet;

import org.junit.Test;
import org.marketcetera.admin.User;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataPermissions;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.manual.ManualFeedModuleFactory;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link MarketDataRpcService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcServerTest
        extends MarketDataTestBase
{
    /**
     * Test {@link MarketDataClient#request(org.marketcetera.marketdata.MarketDataRequest, MarketDataListener)} with no permissions.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRequestMarketDataNoPermissions()
            throws Exception
    {
        User noPermissionUser = generateUserNoPermissions(PlatformServices.generateId(),
                                                          "password");
        try(MarketDataClient noPermissionClient = generateMarketDataClient(noPermissionUser.getName(),"password")) {
            assertFalse(authorizationService.authorizeNoException(noPermissionUser.getName(),
                                                                  MarketDataPermissions.RequestMarketDataAction.name()));
            // won't fail, but won't deliver any events, either
            MarketDataRequest request = generateMarketDataRequest();
            assertNoMarketData();
            noPermissionClient.request(request,
                                       marketDataTestEventListener);
            Throwable permissionError = waitForMarketDataError();
            assertNotAuthorized(permissionError,
                                MarketDataPermissions.RequestMarketDataAction.name());
            // no events
            assertNoMarketDataEvents();
            // test snapshots
            reset();
            assertFalse(authorizationService.authorizeNoException(noPermissionUser.getName(),
                                                                  MarketDataPermissions.RequestMarketDataSnapshotAction.name()));
            assertNoMarketData();
            // this will fail directly
            new ExpectedFailure<RuntimeException>("INVALID_ARGUMENT: NotAuthorizedException: " + noPermissionUser.getName() + " is not authorized for " + MarketDataPermissions.RequestMarketDataSnapshotAction) {
                @Override
                protected void run()
                        throws Exception
                {
                    noPermissionClient.getSnapshot(new Equity("METC"),
                                                   Content.TOP_OF_BOOK);
                }
            };
        } finally {
            adminClient.deleteUser(noPermissionUser.getName());
        }
    }
    /**
     * Test {@link MarketDataClient#request(MarketDataRequest, MarketDataListener)} and {@link MarketDataClient#cancel(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRequestAndCancelMarketData()
            throws Exception
    {
        final MarketDataRequest request = generateMarketDataRequest();
        assertNoMarketData();
        marketDataClient.request(request,
                                 marketDataTestEventListener);
        assertNoMarketData();
        Event sentEvent = EventTestBase.generateAskEvent(new Equity("METC"));
        waitForActiveRequest(request);
        sendEvent(sentEvent,
                  request);
        Event receivedEvent = waitForMarketDataEvent();
        assertEquals(sentEvent,
                     receivedEvent);
        // test cancel
        marketDataClient.cancel(request.getRequestId());
        waitForNoActiveRequest(request);
        reset();
        sendEvent(sentEvent,
                  null);
        assertNoMarketData();
        // repeat cancel test
        new ExpectedFailure<IllegalArgumentException>("Unknown market data request id: " + request.getRequestId()) {
            @Override
            protected void run()
                    throws Exception
            {
                marketDataClient.cancel(request.getRequestId());
            }
        };
    }
    /**
     * Test duplicate market data request ids.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDuplicateMarketDataRequest()
            throws Exception
    {
        final MarketDataRequest request = generateMarketDataRequest();
        assertNoMarketData();
        marketDataClient.request(request,
                                 marketDataTestEventListener);
        assertNoMarketData();
        Event sentEvent = EventTestBase.generateAskEvent(new Equity("METC"));
        waitForActiveRequest(request);
        sendEvent(sentEvent,
                  request);
        Event receivedEvent = waitForMarketDataEvent();
        assertEquals(sentEvent,
                     receivedEvent);
        // request again with the same request
        reset();
        new ExpectedFailure<IllegalArgumentException>("Duplicate market data request id: " + request.getRequestId()) {
            @Override
            protected void run()
                    throws Exception
            {
                marketDataClient.request(request,
                                         marketDataTestEventListener);
            }
        };
        // a different client can use the same request id
        try(MarketDataClient traderClient = generateTraderMarketDataClient()) {
            reset();
            traderClient.request(request,
                                 marketDataTestEventListener);
            assertNoMarketData();
            sentEvent = EventTestBase.generateBidEvent(new Equity("METC"));
            waitForActiveRequest(request);
            sendEvent(sentEvent,
                      request);
            receivedEvent = waitForMarketDataEvent();
            assertEquals(sentEvent,
                         receivedEvent);
            traderClient.cancel(request.getRequestId());
        }
        marketDataClient.cancel(request.getRequestId());
    }
    /**
     * Test {@link MarketDataClient#getAvailableCapability()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAvailableCapability()
            throws Exception
    {
        assertEquals(EnumSet.allOf(Capability.class),
                     marketDataClient.getAvailableCapability());
    }
    /**
     * Test {@link MarketDataClient#addMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)} and {@link MarketDataClient#removeMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMarketDataStatusListeners()
            throws Exception
    {
        marketDataClient.addMarketDataStatusListener(marketDataTestStatusListener);
        marketDataClient.addMarketDataStatusListener(marketDataTestStatusListener);
        moduleManager.stop(ManualFeedModuleFactory.INSTANCE_URN);
        verifyOfflineStatus(waitForMarketDataStatus());
        assertNoMarketDataStatus();
        marketDataClient.removeMarketDataStatusListener(marketDataTestStatusListener);
        marketDataClient.removeMarketDataStatusListener(marketDataTestStatusListener);
        reset();
        moduleManager.start(ManualFeedModuleFactory.INSTANCE_URN);
        assertNoMarketDataStatus();
    }
    /**
     * Test {@link MarketDataClient#getSnapshot(org.marketcetera.trade.Instrument, Content)} and {@link MarketDataClient#getSnapshot(org.marketcetera.trade.Instrument, Content, org.marketcetera.persist.PageRequest)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMarketDataSnapshot()
            throws Exception
    {
        Instrument instrument = new Equity("METC");
        // test empty snapshot, no page
        doSnapshotTest(instrument);
        // generate a trade
        TradeEvent tradeEvent = EventTestBase.generateTradeEvent(instrument);
        // send trade to all market data requests
        feedMarketDataCache(tradeEvent);
        doSnapshotTest(instrument);
        // generate a top-of-book
        BidEvent topBid = EventTestBase.generateBidEvent(instrument);
        feedMarketDataCache(topBid);
        doSnapshotTest(instrument);
        AskEvent topAsk = EventTestBase.generateAskEvent(instrument);
        feedMarketDataCache(topAsk);
        doSnapshotTest(instrument);
        // generate a bunch of lesser bids
        for(int i=1;i<100;i++) {
            BidEvent otherBid = EventTestBase.generateBidEvent(instrument,
                                                               topBid.getPrice().subtract(PlatformServices.ONE_PENNY.multiply(new BigDecimal(1))));
            feedMarketDataCache(otherBid);
        }
        doSnapshotTest(instrument);
        // generate a bunch of lesser asks
        for(int i=1;i<100;i++) {
            AskEvent otherAsk = EventTestBase.generateAskEvent(instrument,
                                                               topAsk.getPrice().add(PlatformServices.ONE_PENNY.multiply(new BigDecimal(1))));
            feedMarketDataCache(otherAsk);
        }
        doSnapshotTest(instrument);
    }
    /**
     * Execute a snapshot test with the given values.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doSnapshotTest(Instrument inInstrument)
            throws Exception
    {
        for(Content content : Content.values()) {
            if(content == Content.DIVIDEND) {
                // TODO see MATP-997
                continue;
            }
            SLF4JLoggerProxy.debug(this,
                                   "Unpaged snapshot test for {} {}",
                                   inInstrument,
                                   content);
            Deque<Event> expectedEvents = cacheManager.getSnapshot(inInstrument,
                                                                   content);
            Deque<Event> actualEvents = marketDataClient.getSnapshot(inInstrument,
                                                                     content);
            assertEquals(expectedEvents,
                         actualEvents);
            // repeat with one large page
            SLF4JLoggerProxy.debug(this,
                                   "One large page snapshot test for {} {}",
                                   inInstrument,
                                   content);
            PageRequest pageRequest = PageRequest.ALL;
            CollectionPageResponse<Event> expectedEventsPage = cacheManager.getSnapshot(inInstrument,
                                                                                        content,
                                                                                        pageRequest);
            CollectionPageResponse<Event> actualEventsPage = marketDataClient.getSnapshot(inInstrument,
                                                                                         content,
                                                                                         pageRequest);
            verifyPageResponse(pageRequest,
                               expectedEventsPage,
                               actualEventsPage);
            // repeat with one page of one
            SLF4JLoggerProxy.debug(this,
                                   "First page of one snapshot test for {} {}",
                                   inInstrument,
                                   content);
            pageRequest = new PageRequest(0,
                                          1);
            expectedEventsPage = cacheManager.getSnapshot(inInstrument,
                                                          content,
                                                          pageRequest);
            actualEventsPage = marketDataClient.getSnapshot(inInstrument,
                                                            content,
                                                            pageRequest);
            verifyPageResponse(pageRequest,
                               expectedEventsPage,
                               actualEventsPage);
            // repeat with second page of one
            SLF4JLoggerProxy.debug(this,
                                   "Second page of one snapshot test for {} {}",
                                   inInstrument,
                                   content);
            pageRequest = new PageRequest(1,
                                          1);
            expectedEventsPage = cacheManager.getSnapshot(inInstrument,
                                                          content,
                                                          pageRequest);
            actualEventsPage = marketDataClient.getSnapshot(inInstrument,
                                                            content,
                                                            pageRequest);
            verifyPageResponse(pageRequest,
                               expectedEventsPage,
                               actualEventsPage);
        }
    }
    /**
     * Verify the expected page matches the actual page.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @param inExpectedResults a <code>CollectionPageResponse&lt;T&gt;</code> value
     * @param inActualResults a <code>CollectionPageResponse&lt;T&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private <T> void verifyPageResponse(PageRequest inPageRequest,
                                        CollectionPageResponse<T> inExpectedResults,
                                        CollectionPageResponse<T> inActualResults)
            throws Exception
    {
        assertEquals(inExpectedResults.getElements(),
                     inActualResults.getElements());
        assertEquals(inExpectedResults.getPageMaxSize(),
                     inActualResults.getPageMaxSize());
        assertEquals(inExpectedResults.getPageNumber(),
                     inActualResults.getPageNumber());
        assertEquals(inExpectedResults.getPageSize(),
                     inActualResults.getPageSize());
        assertEquals(inExpectedResults.getSortOrder(),
                     inActualResults.getSortOrder());
        assertEquals(inExpectedResults.getTotalPages(),
                     inActualResults.getTotalPages());
        assertEquals(inExpectedResults.getTotalSize(),
                     inActualResults.getTotalSize());
    }
}
