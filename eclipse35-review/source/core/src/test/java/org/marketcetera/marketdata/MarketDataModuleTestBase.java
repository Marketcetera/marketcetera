package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkDataListener;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.module.ConfigurationProviderTest.MockConfigurationProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.CollectionAssert;

/* $License$ */

/**
 * Base class for market data provider <code>Module</code> tests.
 * 
 * <p>Unit tests for market data provider modules should extend this class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
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
    protected ModuleFactory factory;
    /**
     * configuration provider to use to set up the module to be tested, if necessary
     */
    protected MockConfigurationProvider provider;
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
    /**
     * Sets up fake configuration files for module config.
     * 
     * <p>Subclasses may extend this method to add their needed configuration values.  The
     * configuration will automatically get added to the <code>ModuleManager</code> used by
     * these tests.  This is typically necessary if the market data provider needs credentials
     * to run.  The default implementation does nothing.
     *
     * @param inProvider a <code>MockConfigurationProvider</code> value
     */
    protected void populateConfigurationProvider(MockConfigurationProvider inProvider)
    {
    }
    /**
     * Indicates the expected capabilities for this feed.
     * 
     * <p>Subclasses may override this method to indicate their expected capabilties.  The
     * default implementation returns an empty array.
     *
     * @return a <code>Capability[]</code> value
     */
    protected Capability[] getExpectedCapabilities()
    {
        return new Capability[0];
    }
    /**
     * Indicates a {@link Capability} that the feed does not support.
     * 
     * <p>If possible, subclasses should override this method to indicate any unsupported
     * capability.  If not possible, retain this implementation which returns null.
     *
     * @return a <code>Capability</code> that is not supported or null if they are all supported
     */
    protected Capability getUnexpectedCapability()
    {
        return null;
    }
    /**
     * Returns an <code>MXBean</code> Proxy for the market data module being tested.
     *
     * @return an <code>AbstractMarketDataModuleMXBean</code> value
     * @throws Exception if an error occurs
     */
    protected final AbstractMarketDataModuleMXBean getMXBeanProxy()
        throws Exception
    {
        ObjectName objectName = getInstanceURN().toObjectName();
        MBeanServerConnection mMBeanServer = ModuleTestBase.getMBeanServer(); 
        return JMX.newMXBeanProxy(mMBeanServer,
                                  objectName,
                                  AbstractMarketDataModuleMXBean.class,
                                  true);
    }
    /**
     * Returns a valid provider for this market data provider.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getProvider();
    /**
     * Tests that the feed's capabilities match the expected values.
     */
    @Test
    public void capabilities()
        throws Exception
    {
        AbstractMarketDataModuleMXBean mBeanProxy = getMXBeanProxy();
        CollectionAssert.assertArrayPermutation(getExpectedCapabilities(),
                                                mBeanProxy.getCapabilities().toArray(new Capability[0]));
        Capability unsupportedCapability = getUnexpectedCapability();
        if(unsupportedCapability != null) {
            assertFalse("The feed is not supposed to support " + unsupportedCapability,
                        mBeanProxy.getCapabilities().contains(unsupportedCapability));
        }
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
        MarketDataRequest request = MarketDataRequest.newRequest().withSymbols("GOOG").fromProvider(getProvider());
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
        MarketDataRequest request = MarketDataRequest.newRequest().withSymbols("GOOG");
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
    @Test@Ignore
    public void reconnect()
        throws Exception
    {
        MarketDataRequest request = MarketDataRequest.newRequest().withSymbols("GOOG");
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
        // reconnect and wait for more data
        // TODO - need to figure out how to make this magic incantation work
        ObjectName objectName = getInstanceURN().toObjectName();
        MBeanServerConnection mMBeanServer = null;
        AbstractMarketDataModuleMXBean mMBeanProxy = JMX.newMXBeanProxy(mMBeanServer,
                                                                        objectName,
                                                                        AbstractMarketDataModuleMXBean.class,
                                                                        true);
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
     * @return a <code>ModuleFactory</code> value
     */
    protected abstract ModuleFactory getFactory();
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
     * @version $Id$
     * @since 1.0.0
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
