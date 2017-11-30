package org.marketcetera.test.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.marketdata.cache.MarketDataCacheModuleFactory;
import org.marketcetera.marketdata.manual.ManualFeedModule;
import org.marketcetera.marketdata.manual.ManualFeedModuleFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.marketdata.service.MarketDataCacheManager;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.test.IntegrationTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;

import junitparams.JUnitParamsRunner;

/* $License$ */

/**
 * Provides common test behavior for admin server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootTest(classes=MarketDataTestConfiguration.class)
@RunWith(JUnitParamsRunner.class)
@ComponentScan(basePackages={"org.marketcetera"})
@EntityScan(basePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
public abstract class MarketDataTestBase
        extends IntegrationTestBase
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        marketDataClient = generateMarketDataClient("test",
                                                    "test");
        marketDataTestEventListener = new MarketDataTestEventListener();
        marketDataTestStatusListener = new MarketDataTestStatusListener();
        reset();
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        stopMarketDataCacheFlow();
        if(marketDataClient != null) {
            try {
                marketDataClient.stop();
            } catch (Exception ignored) {}
            marketDataClient = null;
        }
        super.cleanup();
    }
    /**
     * Generate an {@link MarketDataClient} owned by user "trader".
     *
     * @return a <code>MarketDataClient</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected MarketDataClient generateTraderMarketDataClient()
            throws Exception
    {
        return generateMarketDataClient("trader",
                                        "trader");
    }
    /**
     * Generate a <code>MarketDataClient</code> with the given user/password.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>MarketDataClient</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected MarketDataClient generateMarketDataClient(String inUsername,
                                                        String inPassword)
            throws Exception
    {
        MarketDataRpcClientParameters params = new MarketDataRpcClientParameters();
        params.setHostname(rpcHostname);
        params.setPort(rpcPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        MarketDataClient adminClient = marketDataClientFactory.create(params);
        adminClient.start();
        return adminClient;
    }
    /**
     * Generate a minimal market data request.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    protected MarketDataRequest generateMarketDataRequest()
    {
        MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        builder.withProvider(ManualFeedModuleFactory.IDENTIFIER);
        builder.withSymbols("METC");
        return builder.create();
    }
    /**
     * Reset all test structures.
     */
    protected void reset()
    {
        cacheManager.clear();
        marketDataTestEventListener.reset();
        marketDataTestStatusListener.reset();
    }
    /**
     * Reset received market data events.
     */
    protected void resetMarketDataEvents()
    {
        marketDataTestEventListener.resetMarketDataEvents();
    }
    /**
     * Reset received market data errors.
     */
    protected void resetMarketDataErrors()
    {
        marketDataTestEventListener.resetMarketDataErrors();
    }
    /**
     * Reset received market data status.
     */
    protected void resetMarketDataStatus()
    {
        marketDataTestStatusListener.resetMarketDataStatus();
    }
    /**
     * Verify that market data structures are empty.
     */
    protected void assertNoMarketData()
    {
        assertNoMarketDataEvents();
        assertNoMarketDataErrors();
        assertNoMarketDataStatus();
    }
    /**
     * Verify that there are no market data events.
     */
    protected void assertNoMarketDataStatus()
    {
        marketDataTestStatusListener.assertNoMarketDataStatus();
    }
    /**
     * Verify that there are no market data events.
     */
    protected void assertNoMarketDataEvents()
    {
        marketDataTestEventListener.assertNoMarketDataEvents();
    }
    /**
     * Verify that there are no market data errors.
     */
    protected void assertNoMarketDataErrors()
    {
        marketDataTestEventListener.assertNoMarketDataErrors();
    }
    /**
     * Wait for a market data error to be received.
     *
     * @return a <code>Throwable</code> value
     * @throws Exception if an event is not received in 10s
     */
    protected Throwable waitForMarketDataError()
            throws Exception
    {
        return marketDataTestEventListener.waitForMarketDataError();
    }
    /**
     * Wait for a market data event to be received.
     *
     * @return an <code>Event</code> value
     * @throws Exception if an event is not received in 10s
     */
    protected Event waitForMarketDataEvent()
            throws Exception
    {
        return marketDataTestEventListener.waitForMarketDataEvent();
    }
    /**
     * Wait for a market data status value to be received.
     *
     * @return a <code>MarketDataStatus</code> value
     * @throws Exception if an event is not received in 10s
     */
    protected MarketDataStatus waitForMarketDataStatus()
            throws Exception
    {
        return marketDataTestStatusListener.waitForMarketDataStatus();
    }
    /**
     * Send the given event to the request with the given id.
     *
     * @param inEvent an <code>Event</code> value
     * @param inRequest a <code>String</code> value
     */
    protected void sendEvent(Event inEvent,
                             MarketDataRequest inRequest)
    {
        ManualFeedModule moduleInstance = ManualFeedModule.getInstance();
        if(inRequest == null) {
            moduleInstance.emit(null,
                                inEvent);
        } else {
            BiMap<String,MarketDataRequest> requestData = moduleInstance.getRequests();
            String id = null;
            for(Map.Entry<String,MarketDataRequest> entry : requestData.entrySet()) {
                if(entry.getValue().getRequestId().contains(inRequest.getRequestId())) {
                    id = entry.getKey();
                    break;
                }
            }
            assertNotNull("Market data request " + inRequest + " not found",
                          id);
            moduleInstance.emit(id,
                                inEvent);
        }
    }
    protected void feedMarketDataCache(Event inEvent)
    {
        startMarketDataCacheFlow();
        HeadwaterModule headwaterModule = HeadwaterModule.getInstance(cacheHeadwaterName);
        headwaterModule.emit(inEvent,
                             cacheHeadwaterDataFlow);
    }
    protected void startMarketDataCacheFlow()
    {
        if(cacheHeadwaterUrn == null) {
            cacheHeadwaterName = "marketDataCache_" + getClass().getSimpleName();
            cacheHeadwaterUrn = moduleManager.createModule(HeadwaterModuleFactory.PROVIDER_URN,
                                                           cacheHeadwaterName);
            List<DataRequest> dataRequestBuilder = Lists.newArrayList();
            dataRequestBuilder.add(new DataRequest(cacheHeadwaterUrn));
            dataRequestBuilder.add(new DataRequest(MarketDataCacheModuleFactory.INSTANCE_URN));
            cacheHeadwaterDataFlow = moduleManager.createDataFlow(dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]));
        }
    }
    protected void stopMarketDataCacheFlow()
    {
        if(cacheHeadwaterDataFlow != null) {
            moduleManager.cancel(cacheHeadwaterDataFlow);
            cacheHeadwaterDataFlow = null;
        }
        if(cacheHeadwaterUrn != null) {
            moduleManager.stop(cacheHeadwaterUrn);
            moduleManager.deleteModule(cacheHeadwaterUrn);
            cacheHeadwaterUrn = null;
        }
    }
    /**
     * Wait for the given market data request to have been received and processed by the market data adapter.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @throws Exception if an unexpected error occurs or the call times out
     */
    protected void waitForActiveRequest(final MarketDataRequest inRequest)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BiMap<String,MarketDataRequest> requestData = ManualFeedModule.getInstance().getRequests();
                for(Map.Entry<String,MarketDataRequest> entry : requestData.entrySet()) {
                    if(entry.getValue().getRequestId().contains(inRequest.getRequestId())) {
                        return true;
                    }
                }
                return false;
            }
        },10);
    }
    /**
     * Wait for the given market data request to no longer be present in the market data adpater.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @throws Exception if an unexpected error occurs or the call times out
     */
    protected void waitForNoActiveRequest(final MarketDataRequest inRequest)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BiMap<String,MarketDataRequest> requestData = ManualFeedModule.getInstance().getRequests();
                for(Map.Entry<String,MarketDataRequest> entry : requestData.entrySet()) {
                    if(entry.getValue().getRequestId().contains(inRequest.getRequestId())) {
                        return false;
                    }
                }
                return true;
            }
        },10);
    }
    /**
     * Verify that the given status reflects offline for the default market data provider.
     *
     * @param inActualStatus a <code>MarketDataStatus</code> value
     */
    protected void verifyOfflineStatus(MarketDataStatus inActualStatus)
    {
        verifyMarketDataStatus(FeedStatus.OFFLINE,
                               ManualFeedModuleFactory.IDENTIFIER,
                               inActualStatus);
    }
    /**
     * Verify that the given market data status matches the expected values.
     *
     * @param inExpectedFeedStatus a <code>FeedStatus</code> value
     * @param inExpectedProvider a <code>String</code> value
     * @param inActualStatus a <code>MarketDataStatus</code> value
     */
    protected void verifyMarketDataStatus(FeedStatus inExpectedFeedStatus,
                                          String inExpectedProvider,
                                          MarketDataStatus inActualStatus)
    {
        assertEquals(inExpectedFeedStatus,
                     inActualStatus.getFeedStatus());
        assertEquals(inExpectedProvider,
                     inActualStatus.getProvider());
    }
    /**
     * Listens for market data status.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected static class MarketDataTestStatusListener
            implements MarketDataStatusListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataStatusListener#receiveMarketDataStatus(org.marketcetera.marketdata.MarketDataStatus)
         */
        @Override
        public void receiveMarketDataStatus(MarketDataStatus inMarketDataStatus)
        {
            SLF4JLoggerProxy.debug(this,
                                   "Received {}",
                                   inMarketDataStatus);
            synchronized(marketDataStatus) {
                marketDataStatus.add(inMarketDataStatus);
                marketDataStatus.notifyAll();
            }
        }
        /**
         * Wait for a market data status value to be received.
         *
         * @return a <code>MarketDataStatus</code> value
         * @throws Exception if an event is not received in 10s
         */
        protected MarketDataStatus waitForMarketDataStatus()
                throws Exception
        {
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    synchronized(marketDataStatus) {
                        return !marketDataStatus.isEmpty();
                    }
                }
            },10);
            synchronized(marketDataStatus) {
                return marketDataStatus.remove();
            }
        }
        /**
         * Verify that there are no market data events.
         */
        protected void assertNoMarketDataStatus()
        {
            synchronized(marketDataStatus) {
                assertTrue(marketDataStatus.isEmpty());
            }
        }
        /**
         * Reset all status collections.
         */
        protected void reset()
        {
            resetMarketDataStatus();
        }
        /**
         * Reset the market data status collection.
         */
        protected void resetMarketDataStatus()
        {
            synchronized(marketDataStatus) {
                marketDataStatus.clear();
            }
        }
        /**
         * holds market data status receiver
         */
        private final Deque<MarketDataStatus> marketDataStatus = Lists.newLinkedList();
    }
    /**
     * Listens for market data events.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected static class MarketDataTestEventListener
            implements MarketDataListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
         */
        @Override
        public void receiveMarketData(Event inEvent)
        {
            SLF4JLoggerProxy.debug(MarketDataTestBase.class,
                                   "Received {}",
                                   inEvent);
            synchronized(marketDataEvents) {
                marketDataEvents.add(inEvent);
                marketDataEvents.notifyAll();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inThrowable)
        {
            SLF4JLoggerProxy.debug(MarketDataTestBase.class,
                                   "Received {}",
                                   inThrowable);
            synchronized(marketDataErrors) {
                marketDataErrors.add(inThrowable);
                marketDataErrors.notifyAll();
            }
        }
        /**
         * Wait for a market data event to be received.
         *
         * @return an <code>Event</code> value
         * @throws Exception if an event is not received in 10s
         */
        protected Event waitForMarketDataEvent()
                throws Exception
        {
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    synchronized(marketDataEvents) {
                        return !marketDataEvents.isEmpty();
                    }
                }
            },10);
            synchronized(marketDataEvents) {
                return marketDataEvents.remove();
            }
        }
        /**
         * Wait for a market data error to be received.
         *
         * @return a <code>Throwable</code> value
         * @throws Exception if an event is not received in 10s
         */
        protected Throwable waitForMarketDataError()
                throws Exception
        {
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    synchronized(marketDataErrors) {
                        return !marketDataErrors.isEmpty();
                    }
                }
            },10);
            synchronized(marketDataErrors) {
                return marketDataErrors.remove();
            }
        }
        /**
         * Reset test structures.
         */
        protected void reset()
        {
            resetMarketDataErrors();
            resetMarketDataEvents();
        }
        /**
         * Assert that no market data events have been received.
         */
        protected void assertNoMarketDataEvents()
        {
            synchronized(marketDataEvents) {
                assertTrue(marketDataEvents.isEmpty());
            }
        }
        /**
         * Reset the market data error collection.
         */
        protected void resetMarketDataErrors()
        {
            synchronized(marketDataErrors) {
                marketDataErrors.clear();
            }
        }
        /**
         * Reset the market data event collection.
         */
        protected void resetMarketDataEvents()
        {
            synchronized(marketDataEvents) {
                marketDataEvents.clear();
            }
        }
        /**
         * Assert that no market data errors have been received.
         */
        protected void assertNoMarketDataErrors()
        {
            synchronized(marketDataErrors) {
                assertTrue(marketDataErrors.isEmpty());
            }
        }
        /**
         * holds events received
         */
        private final Deque<Event> marketDataEvents = Lists.newLinkedList();
        /**
         * holds market data errors received
         */
        private final Deque<Throwable> marketDataErrors = Lists.newLinkedList();
    }
    protected DataFlowID cacheHeadwaterDataFlow;
    protected String cacheHeadwaterName;
    protected ModuleURN cacheHeadwaterUrn;
    /**
     * listens for market data events
     */
    protected MarketDataTestEventListener marketDataTestEventListener;
    /**
     * listens for market data status
     */
    protected MarketDataTestStatusListener marketDataTestStatusListener;
    /**
     * provides access to market data services
     */
    protected MarketDataClient marketDataClient;
    /**
     * provides access to the market data cache
     */
    @Autowired
    protected MarketDataCacheManager cacheManager;
    /**
     * provides access to user services
     */
    @Autowired
    protected UserService userService;
    /**
     * creates {@link MarketDataClient} objects
     */
    @Autowired
    protected MarketDataRpcClientFactory marketDataClientFactory;
    /**
     * provides access to authorization services
     */
    @Autowired
    protected AuthorizationService authorizationService;
    /**
     * test artifact used to identify the current test case
     */
    @Rule
    public TestName name = new TestName();
    /**
     * rule used to load test context
     */
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    /**
     * test spring method rule
     */
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
}
