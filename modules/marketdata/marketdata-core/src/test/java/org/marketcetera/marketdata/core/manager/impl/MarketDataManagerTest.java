package org.marketcetera.marketdata.core.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link MarketDataManagerImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataManagerTest
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
        moduleManager = new ModuleManager();
        moduleManager.init();
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
        marketDataManager = new MarketDataManagerImpl();
        marketDataManager.setDefaultMarketDataProvider(BogusFeedModuleFactory.IDENTIFIER);
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
        try {
            moduleManager.stop();
        } catch (Exception ignored) {}
        moduleManager = null;
    }
    /**
     * Test multiple content types in the same request.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMultipleContentTypes()
            throws Exception
    {
        MarketDataRequest request = MarketDataRequestBuilder.newRequest().withProvider(BogusFeedModuleFactory.IDENTIFIER).withSymbols("GOOG").withContent(Content.TOP_OF_BOOK,Content.LATEST_TICK).create();
        final List<Event> events = new ArrayList<>();
        ISubscriber subscriber = new ISubscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
            @Override
            public void publishTo(Object inData)
            {
                SLF4JLoggerProxy.trace(MarketDataManagerTest.this,
                                       "Received: {}",
                                       inData);
                events.add((Event)inData);
            }
        };
        long requestId = marketDataManager.requestMarketData(request,
                                                             subscriber);
        while(events.size() < 20) {
            Thread.sleep(1000);
        }
        marketDataManager.cancelMarketDataRequest(requestId);
    }
    /**
     * Test a market data request with a provider.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testWithProvider()
            throws Exception
    {
        MarketDataRequest request = MarketDataRequestBuilder.newRequest().withProvider(BogusFeedModuleFactory.IDENTIFIER).withSymbols("GOOG").create();
        final List<Event> events = new ArrayList<>();
        ISubscriber subscriber = new ISubscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
            @Override
            public void publishTo(Object inData)
            {
                SLF4JLoggerProxy.trace(MarketDataManagerTest.this,
                                       "Received: {}",
                                       inData);
                events.add((Event)inData);
            }
        };
        long requestId = marketDataManager.requestMarketData(request,
                                                             subscriber);
        while(events.size() < 20) {
            Thread.sleep(1000);
        }
        marketDataManager.cancelMarketDataRequest(requestId);
    }
    /**
     * Test a market data request without a provider.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testWithNoProvider()
            throws Exception
    {
        MarketDataRequest request = MarketDataRequestBuilder.newRequest().withSymbols("GOOG").create();
        final List<Event> events = new ArrayList<>();
        ISubscriber subscriber = new ISubscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
            @Override
            public void publishTo(Object inData)
            {
                SLF4JLoggerProxy.trace(MarketDataManagerTest.this,
                                       "Received: {}",
                                       inData);
                events.add((Event)inData);
            }
        };
        long requestId = marketDataManager.requestMarketData(request,
                                                             subscriber);
        while(events.size() < 20) {
            Thread.sleep(1000);
        }
        marketDataManager.cancelMarketDataRequest(requestId);
    }
    /**
     * test module manager instance
     */
    private ModuleManager moduleManager;
    /**
     * test market data manager instance
     */
    private MarketDataManagerImpl marketDataManager;
}
