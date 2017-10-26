package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Deque;
import java.util.EnumSet;

import org.junit.Test;
import org.marketcetera.admin.User;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataPermissions;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.manual.ManualFeedModuleFactory;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

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
        // test empty snapshot, no page
        Instrument instrument = new Equity("METC");
        for(Content content : Content.values()) {
            if(content == Content.DIVIDEND) {
                // TODO see MATP-997
                continue;
            }
            Deque<Event> expectedEvents = cacheManager.getSnapshot(instrument,
                                                                   content);
            Deque<Event> actualEvents = marketDataClient.getSnapshot(instrument,
                                                                     content);
            assertEquals(expectedEvents,
                         actualEvents);
        }
    }
}
