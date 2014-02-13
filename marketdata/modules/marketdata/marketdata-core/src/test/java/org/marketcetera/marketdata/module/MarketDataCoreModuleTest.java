package org.marketcetera.marketdata.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.EnumSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MockMarketDataFeed;
import org.marketcetera.marketdata.MockMarketDataFeedModuleFactory;
import org.marketcetera.marketdata.core.module.MarketDataCoreModuleFactory;
import org.marketcetera.module.*;

import com.google.common.collect.Lists;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataCoreModuleTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void beforeClass()
            throws Exception
    {
        LoggerConfiguration.logSetup();
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
        moduleManager = new ModuleManager();
        moduleManager.init();
        MockMarketDataFeed.instance.setCapabilities(EnumSet.allOf(Capability.class));
        moduleManager.start(MockMarketDataFeedModuleFactory.INSTANCE_URN);
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        moduleManager.stop();
    }
    /**
     * Tests start/stop/status module operations.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testModuleOperations()
            throws Exception
    {
        // instance exists (auto-created)
        ModuleInfo info = moduleManager.getModuleInfo(MarketDataCoreModuleFactory.INSTANCE_URN);
        assertNotNull(info);
        assertNotNull(info.getCreated());
        assertNotNull(info.getStarted());
        // instance started (auto-started)
        assertEquals(ModuleState.STARTED,
                     info.getState());
        new ExpectedFailure<ModuleStateException>(Messages.MODULE_NOT_STARTED_STATE_INCORRECT) {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.start(MarketDataCoreModuleFactory.INSTANCE_URN);
            }
        };
        new ExpectedFailure<ModuleException>(Messages.CANNOT_DELETE_SINGLETON) {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.deleteModule(MarketDataCoreModuleFactory.INSTANCE_URN);
            }
        };
        new ExpectedFailure<ModuleException>(Messages.CANNOT_CREATE_SINGLETON) {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.createModule(MarketDataCoreModuleFactory.PROVIDER_URN);
            }
        };
        moduleManager.stop(MarketDataCoreModuleFactory.INSTANCE_URN);
        info = moduleManager.getModuleInfo(MarketDataCoreModuleFactory.INSTANCE_URN);
        assertEquals(ModuleState.STOPPED,
                     info.getState());
        moduleManager.start(MarketDataCoreModuleFactory.INSTANCE_URN);
        info = moduleManager.getModuleInfo(MarketDataCoreModuleFactory.INSTANCE_URN);
        assertEquals(ModuleState.STARTED,
                     info.getState());
    }
    /**
     * Tests handling of various request types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testPayloadTypes()
            throws Exception
    {
        new ExpectedFailure<UnsupportedRequestParameterType>() {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                                                 this) });
            }
        };
        new ExpectedFailure<IllegalRequestParameterValue>() {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN) });
            }
        };
        new ExpectedFailure<IllegalRequestParameterValue>() {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                                                 null) });
            }
        };
        // String, which is allowed, but with nonsensical contents
        new ExpectedFailure<IllegalRequestParameterValue>() {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                                                 "this is not a valid market data request") });
            }
        };
        prepEvents(10);
        // valid market data request
        String validRequest = "symbols=METC:provider=mock:content=DIVIDEND";
        assertNotNull(MarketDataRequestBuilder.newRequestFromString(validRequest));
//        MarketDataManagerImpl.getInstance().requestMarketData(MarketDataRequestBuilder.newRequestFromString(validRequest),
//                                                              new ISubscriber() {
//                                                                @Override
//                                                                public void publishTo(Object inData)
//                                                                {
//                                                                    System.out.println("Received " + inData);
//                                                                }
//                                                                @Override
//                                                                public boolean isInteresting(Object inData)
//                                                                {
//                                                                    return true;
//                                                                }
//                                                            });
//        DataFlowID flow = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN,
//                                                                                           validRequest) });
//        Thread.sleep(5000);
//        assertNotNull(flow);
//        moduleManager.cancel(flow);
        Thread.sleep(5000);
//        moduleManager.start(MockMarketDataFeedModuleFactory.INSTANCE_URN);
//        Thread.sleep(5000);
//        moduleManager.stop(MockMarketDataFeedModuleFactory.INSTANCE_URN);
//        Thread.sleep(5000);
    }
    /**
     * 
     *
     *
     * @param inEventCount
     */
    private void prepEvents(int inEventCount)
    {
        List<Event> events = Lists.newArrayList();
        for(int i=0;i<inEventCount;i++) {
            events.add(EventTestBase.generateDividendEvent());
        }
        MockMarketDataFeed.instance.setEventsToReturn(events);
    }
    /**
     * test module manager value
     */
    private ModuleManager moduleManager;
}
