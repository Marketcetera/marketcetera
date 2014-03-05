package org.marketcetera.marketdata.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.marketdata.core.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.core.module.MarketDataCoreModule;
import org.marketcetera.marketdata.core.module.MarketDataCoreModuleFactory;
import org.marketcetera.module.*;
import org.marketcetera.modules.receiver.ReceiverModule;
import org.marketcetera.modules.receiver.ReceiverModuleFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link MarketDataCoreModule}.
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
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
        receiver = new EventReceiver();
        receiverUrn = moduleManager.createModule(ReceiverModuleFactory.PROVIDER_URN,
                                                 "receiver");
        ReceiverModule.getModuleForInstanceName("receiver").subscribe(receiver);
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
        // valid request, but invalid provider
        new ExpectedFailure<MarketDataProviderNotAvailable>() {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                                                 "symbols=METC:provider=notaprovider:content=TOP_OF_BOOK") });
            }
        };
        // valid market data request
        String validRequest = "symbols=METC:provider=bogus:content=TOP_OF_BOOK";
        assertNotNull(MarketDataRequestBuilder.newRequestFromString(validRequest));
        DataFlowID flow = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                                                           validRequest),
                                                                           new DataRequest(receiverUrn) });
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return receiver.data.size() >= 2;
            }
        });
        moduleManager.cancel(flow);
        // try again with a MarketDataRequest object
        receiver.data.clear();
        validRequest = "symbols=GOOG:provider=bogus:content=TOP_OF_BOOK";
        flow = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(MarketDataCoreModuleFactory.INSTANCE_URN,
                                                                                MarketDataRequestBuilder.newRequestFromString(validRequest)),
                                                                new DataRequest(receiverUrn) });
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return receiver.data.size() >= 2;
            }
        });
        moduleManager.cancel(flow);
    }
    /**
     * Receives test events.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class EventReceiver
            implements ISubscriber
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return true;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public void publishTo(Object inData)
        {
            SLF4JLoggerProxy.debug(MarketDataCoreModuleTest.class,
                                   "Received: {}",
                                   inData);
            data.add(inData);
        }
        /**
         * stores data received
         */
        private final List<Object> data = Lists.newArrayList();
    }
    /**
     * receives test events
     */
    private EventReceiver receiver;
    /**
     * test receiver module used to verify events
     */
    private ModuleURN receiverUrn;
    /**
     * test module manager value
     */
    private ModuleManager moduleManager;
}
