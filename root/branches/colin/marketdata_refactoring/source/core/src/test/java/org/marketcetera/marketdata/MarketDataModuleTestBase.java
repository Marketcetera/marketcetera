package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkDataListener;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.module.ConfigurationProviderTest.MockConfigurationProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Base class for market data provider <code>Module</code> tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class MarketDataModuleTestBase
        extends ModuleTestBase
{
    /**
     * global singleton module manager
     */
    protected ModuleManager moduleManager;
    /**
     * test destination of market data requests
     */
    protected DataSink dataSink;
    /**
     * the factory to use to create the market data provider modules
     */
    protected ModuleFactory<? extends Module> factory;
    /**
     * configuration provider to use to set up the module to be tested, if necessary
     */
    protected MockConfigurationProvider provider;
    @BeforeClass
    public static void setupOnce()
    {
        System.setProperty(AbstractMarketDataFeed.MARKETDATA_SIMULATION_KEY,
                           "true");
    }
    @AfterClass
    public static void teardownOnce()
    {
        System.setProperty(AbstractMarketDataFeed.MARKETDATA_SIMULATION_KEY,
                           "false");
    }
    @Before
    public void setup()
        throws Exception
    {
        moduleManager = new ModuleManager();
        provider = new MockConfigurationProvider();
        populateConfigurationProvider(provider);
        moduleManager.setConfigurationProvider(provider);
        moduleManager.init();
        dataSink = new DataSink();
        moduleManager.addSinkListener(dataSink);
        factory = getFactory();
        startModule();
    }
    @After
    public void cleanup()
        throws Exception
    {
        stopModule();
        moduleManager.stop();
        moduleManager = null;
    }
    protected void populateConfigurationProvider(MockConfigurationProvider inProvider)
    {
    }
    @Test
    public void badDataRequests()
        throws Exception
    {
        // null request data
        new ExpectedFailure<IllegalRequestParameterValue>(org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                                                          getInstanceURN().toString(),
                                                          null) {
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                 null) });
            }
        };
        // inappropriate datatype
        final Object invalidParam = new Object();
        new ExpectedFailure<UnsupportedRequestParameterType>(org.marketcetera.module.Messages.UNSUPPORTED_REQ_PARM_TYPE,
                                                             getInstanceURN().toString(),
                                                             invalidParam.getClass().getName()) {
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                 invalidParam) });
            }
        };
        // String, but not from a data request
        final String invalidString = "There is no way you can make a data request from this, so there";
        new ExpectedFailure<IllegalRequestParameterValue>(org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                                                          getInstanceURN().toString(),
                                                          invalidString) {
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                 invalidString) });
            }
        };
    }
    @Test
    public void dataRequestFromString()
        throws Exception
    {
        assertTrue(moduleManager.getDataFlows(true).isEmpty());
        org.marketcetera.marketdata.DataRequest request = MarketDataRequest.newFullBookRequest("GOOG");
        final DataFlowID flowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                                   request.toString()) });
        // wait until some arbitrary number of ticks have been received
        AbstractMarketDataFeedTest.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return dataSink.getData(flowID).size() > 20;
            }});
        // cancel the flow
        moduleManager.cancel(flowID);
    }
    @Test
    public void dataRequestProducesData()
        throws Exception
    {
        assertTrue(moduleManager.getDataFlows(true).isEmpty());
        org.marketcetera.marketdata.DataRequest request = MarketDataRequest.newFullBookRequest("GOOG");
        final DataFlowID flowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                                   request) });
        // wait until some arbitrary number of ticks have been received
        AbstractMarketDataFeedTest.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return dataSink.getData(flowID).size() > 20;
            }});
        // cancel the flow
        moduleManager.cancel(flowID);
        
    }
    /**
     * Starts the module to be tested and verifies that it has started.
     *
     * @throws Exception if an error occurs
     */
    protected void startModule()
        throws Exception
    {
        moduleManager.start(getInstanceURN());
        assertEquals(ModuleState.STARTED,
                     moduleManager.getModuleInfo(getInstanceURN()).getState());
    }
    /**
     * Stops the module to be tested and verifies that it has stopped.
     *
     * @throws Exception if an error occurs
     */
    protected void stopModule()
        throws Exception
    {
        moduleManager.stop(getInstanceURN());
        assertEquals(ModuleState.STOPPED,
                     moduleManager.getModuleInfo(getInstanceURN()).getState());
    }
    /**
     * Returns a <code>ModuleFactory</code> instance appropriate for the module to be tested. 
     *
     * @return a <code>ModuleFactory&lt;? extends Module&gt;</code> value
     */
    protected abstract ModuleFactory<? extends Module> getFactory();
    /**
     * Returns a <code>ModuleURN</code> instance appropriate for the module to be tested. 
     *
     * @return a <code>ModuleURN</code> value
     */
    protected abstract ModuleURN getInstanceURN();
    /**
     * Sample <code>SinkDataListener</code> implementation that stores the objects it receives.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id:$
     * @since $Release$
     */
    public static class DataSink
        implements SinkDataListener
    {
        /**
         * data received by the <code>DataFlowID</code> that caused it to be delivered
         */
        private final Map<DataFlowID,List<Object>> data = new HashMap<DataFlowID,List<Object>>();
        /* (non-Javadoc)
         * @see org.marketcetera.module.SinkDataListener#receivedData(org.marketcetera.module.DataFlowID, java.lang.Object)
         */
        @Override
        public void receivedData(DataFlowID inDataFlowID,
                                 Object inData)
        {
            synchronized(data) {
                SLF4JLoggerProxy.debug(this,
                                       "Test DataSink received {}",
                                       inData);
                List<Object> dataForID = data.get(inDataFlowID);
                if(dataForID == null) {
                    dataForID = new ArrayList<Object>();
                    data.put(inDataFlowID,
                             dataForID);
                }
                dataForID.add(inData);
            }
        }
        /**
         * Retrieves the data collected by this object.
         *
         * @param inDataFlowID a <code>DataFlowID</code> value
         * @return a <code>List&lt;Object&gt;</code> value
         */
        public List<Object> getData(DataFlowID inDataFlowID)
        {
            synchronized(data) {
                List<Object> result = data.get(inDataFlowID);
                if(result == null) {
                    return new ArrayList<Object>();
                }
                return new ArrayList<Object>(result);
            }
        }
    }
}
