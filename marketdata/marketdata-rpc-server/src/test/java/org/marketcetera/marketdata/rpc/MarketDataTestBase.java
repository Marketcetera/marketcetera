package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Deque;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.admin.rpc.AdminTestBase;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.manual.ManualFeedModule;
import org.marketcetera.marketdata.manual.ManualFeedModuleFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        extends AdminTestBase
        implements MarketDataListener
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
        reset();
        marketDataClient = generateMarketDataClient("test",
                                                    "test");
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
        if(marketDataClient != null) {
            try {
                marketDataClient.stop();
            } catch (Exception ignored) {}
            marketDataClient = null;
        }
        super.cleanup();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
     */
    @Override
    public void receiveMarketData(Event inEvent)
    {
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
        synchronized(marketDataErrors) {
            marketDataErrors.add(inThrowable);
            marketDataErrors.notifyAll();
        }
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
        resetMarketDataEventsAndErrors();
    }
    /**
     * Reset event structures.
     */
    protected void resetMarketDataEventsAndErrors()
    {
        marketDataEvents.clear();
        marketDataErrors.clear();
    }
    /**
     * Verify that market data structures are empty.
     */
    protected void assertNoMarketDataEventsOrErrors()
    {
        assertNoMarketDataEvents();
        assertNoMarketDataErrors();
    }
    /**
     * Verify that there are no market data events.
     */
    protected void assertNoMarketDataEvents()
    {
        assertTrue(marketDataEvents.isEmpty());
    }
    /**
     * Verify that there are no market data errors.
     */
    protected void assertNoMarketDataErrors()
    {
        assertTrue(marketDataErrors.isEmpty());
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
        long start = System.currentTimeMillis();
        synchronized(marketDataErrors) {
            while(marketDataErrors.isEmpty()) {
                if(System.currentTimeMillis() > start + 10000) {
                    fail("No error in 10s");
                }
                marketDataErrors.wait(100);
            }
            return marketDataErrors.remove();
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
        long start = System.currentTimeMillis();
        synchronized(marketDataEvents) {
            while(marketDataEvents.isEmpty()) {
                if(System.currentTimeMillis() > start + 10000) {
                    fail("No event in 10s");
                }
                marketDataEvents.wait(100);
            }
            return marketDataEvents.remove();
        }
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
    /**
     * RPC hostname
     */
    @Value("${metc.rpc.hostname}")
    private String rpcHostname = "127.0.0.1";
    /**
     * RPC port
     */
    @Value("${metc.rpc.port}")
    private int rpcPort = 18999;
    /**
     * provides access to market data services
     */
    protected MarketDataClient marketDataClient;
    /**
     * holds events received
     */
    protected final Deque<Event> marketDataEvents = Lists.newLinkedList();
    /**
     * holds market data errors received
     */
    protected final Deque<Throwable> marketDataErrors = Lists.newLinkedList();
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
