package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.marketcetera.admin.User;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataPermissions;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;

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
            assertNoMarketDataEventsOrErrors();
            noPermissionClient.request(request,
                                       this);
            Throwable permissionError = waitForMarketDataError();
            assertNotAuthorized(permissionError,
                                MarketDataPermissions.RequestMarketDataAction.name());
            // no events
            assertNoMarketDataEvents();
            // test snapshots
            resetMarketDataEventsAndErrors();
            assertFalse(authorizationService.authorizeNoException(noPermissionUser.getName(),
                                                                  MarketDataPermissions.RequestMarketDataSnapshotAction.name()));
            assertNoMarketDataEventsOrErrors();
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
     * Test {@link MarketDataClient#request(MarketDataRequest, MarketDataListener)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRequestMarketDataSuccess()
            throws Exception
    {
        MarketDataRequest request = generateMarketDataRequest();
        assertNoMarketDataEventsOrErrors();
        marketDataClient.request(request,
                                 this);
        assertNoMarketDataEventsOrErrors();
        Event sentEvent = EventTestBase.generateAskEvent(new Equity("METC"));
        Thread.sleep(5000);
        sendEvent(sentEvent,
                  request);
        Event receivedEvent = waitForMarketDataEvent();
        assertEquals(sentEvent,
                     receivedEvent);
    }
}
